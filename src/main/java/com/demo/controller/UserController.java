package com.demo.controller;

import com.demo.service.SchemaService;
import io.swagger.annotations.ApiOperation;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.POST;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajinkya on 10/17/16.
 */

@Controller
public class UserController {

    @Autowired
    SchemaService schemaService;

    @POST
    @RequestMapping("/user")
    @ApiOperation(value = "Create a user",
            response = String.class)
    public @ResponseBody  String addPerson(@RequestBody String body, HttpServletResponse response)
    {
        try {
            if (schemaService.validateSchema("SCHEMA__User", body))
            {
                System.out.println(body);
                JSONParser parser = new JSONParser();
                try {
                    JSONObject user = (JSONObject) parser.parse(body);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new BadRequestException("Can not validate JSON Schema");
        }
        return null;
    }

}
