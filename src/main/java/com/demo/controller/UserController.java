package com.demo.controller;

import com.demo.service.SchemaService;
import com.demo.service.TokenService;
import com.demo.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ajinkya on 10/17/16.
 */

@Controller
@Api(description = "Create a new user")
public class UserController {

    @Autowired
    SchemaService schemaService;

    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

    @POST
    @RequestMapping("/user")
    @ApiOperation(value = "Create a user",
            response = String.class, consumes = "application/json")
    @ResponseBody
    public String addPerson(@RequestBody String body, HttpServletResponse response) {
        try {
            if (schemaService.validateSchema("SCHEMA__User", body)) {
                System.out.println(body);
                JSONParser parser = new JSONParser();
                try {
                    JSONObject user = (JSONObject) parser.parse(body);
                    return userService.addUser(user);
                } catch (ParseException e) {
                    throw new BadRequestException("Can not create user");
                }
            }
        } catch (IOException e) {
            throw new BadRequestException("Can not validate JSON Schema");
        }
        return null;
    }

    @GET
    @RequestMapping("/user/{userUid}")
    @ResponseBody
    public String getPerson(@PathVariable("userUid") String userUid) {
        if (!userUid.isEmpty()) {
            String result;
            try {
                result = userService.getUser(userUid);
            } catch (ParseException e) {
                throw new NotFoundException("User Not Found" + e);
            }
            if (result != null) {
                return result;
            } else
                throw new NotFoundException("User Not Found");
        } else {
            throw new BadRequestException("Invalid user uid");
        }
    }

    @RequestMapping(value = "/user/{userUid}", method = RequestMethod.PATCH)
    @ResponseBody
    public String patchUser(@PathVariable("userUid") String userUid,
                            @RequestHeader String token,
                            @RequestParam String parameterName,
                            @RequestBody String parameterValue,
                            HttpServletResponse response) throws IOException {
        if (isTokenValidated(token, response, userUid)) {
            if (schemaService.validateFieldInSchema("SCHEMA__User", parameterName)) {
                try {
                    String s = userService.updateUser(userUid, parameterName, parameterValue);
                    return s;
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendError(500, "Our Servers are having problems");
                }
            } else {
                response.sendError(401, "Bad Request. Parameter doesn't match schema");
            }
        } else {
            response.sendError(500, "Our Servers are having problems");
        }
        return null;
    }

    private Boolean isTokenValidated(String tokenBody, HttpServletResponse response, String userUid) throws IOException {
        if (!StringUtils.isBlank(tokenBody)) {
            try {
                if (tokenService.isTokenValidated(tokenBody, userUid)) {
                    return Boolean.TRUE;
                }
            } catch (ExpiredJwtException e) {
                response.sendError(401, "Token is expired");
            } catch (SignatureException | MalformedJwtException e) {
                response.sendError(401, "Token is not authorized");
            }
        } else {
            throw new NotAuthorizedException("Token is not missing");
        }
        return Boolean.FALSE;
    }

    @POST
    @RequestMapping("/v1/user")
    @ResponseBody
    public String newAddUser(@RequestBody String body, HttpServletResponse response) throws IOException {
        try {
            JSONObject bodyObject = (JSONObject) new JSONParser().parse(body);
            String result = userService.newAddUser(bodyObject);

            result = calculateAndAddETag(response, result);
            return result;
        } catch (ParseException e) {
            response.sendError(500, "Internal Server Error. Parsing Failed");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String calculateAndAddETag(HttpServletResponse response, String result) throws NoSuchAlgorithmException,
            UnsupportedEncodingException,
            ParseException {
        JSONObject responseObject = (JSONObject) new JSONParser().parse(result);
        String eTag = (String) responseObject.get("eTag");
        if (eTag != null) {
            response.addHeader("eTag", eTag);
            responseObject.remove("eTag");
        }
        return responseObject.toJSONString();
    }

    @RequestMapping(value = "/v1/user/{userUid}", method = RequestMethod.GET)
    @ResponseBody
    public String newGetUser(@PathVariable("userUid") String userUid,
                             @RequestHeader(required = true) String token,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        String eTag = request.getHeader("If-None-Match");
        if (isTokenValidated(token, response, userUid)) {
            JSONObject result = userService.newGetUser(userUid);
            if (eTag != null) {

                String newETag = (String) result.get("eTag");
                if (eTag.equals(newETag)) {
                    response.sendError(304, "Object is not modified");
                }
            }
            return result.toJSONString();
        }
        throw new BadRequestException("Authentication Failed");
    }

    @RequestMapping(value = "/v1/user/{userUid}", method = RequestMethod.PATCH)
    @ResponseBody
    public String newPatchUser(@PathVariable("userUid") String userUid,
                               @RequestHeader String token,
                               @RequestParam String parameterName,
                               @RequestBody String parameterValue,
                               @RequestParam String patchKey,
                               HttpServletResponse response) throws IOException {
        if (isTokenValidated(token, response, userUid)) {
            String patchObject = patchKey.split("__", 2)[0];
            if (schemaService.validateFieldInSchema("SCHEMA__" + patchObject, parameterName)) {
                try {
                    return parameterValue;
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendError(500, "Our Servers are having problems");
                }
            } else {
                response.sendError(400, "Bad Request. Parameter doesn't match schema");
            }
        } else {
            response.sendError(401, "Authorization Failed");
        }
        return null;
    }
}
