package com.demo.controller;

import com.demo.service.SchemaService;
import com.demo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
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
    public String getPerson(@PathVariable("userUid") String userUid)
    {
        if (!userUid.isEmpty()){
            String result;
            try {
                result = userService.getUser(userUid);
            } catch (ParseException e) {
                throw new NotFoundException("User Not Found" + e);
            }
            if (result!=null)
            {
                return result;
            }
            else
                throw new NotFoundException("User Not Found");
        }
        else
        {
            throw new BadRequestException("Invalid user uid");
        }
    }

}
