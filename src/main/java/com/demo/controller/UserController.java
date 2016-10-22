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

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import java.io.IOException;

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
    public
    @ResponseBody
    String patchUser(@PathVariable("userUid") String userUid,
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

}
