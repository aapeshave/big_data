package com.demo.controller;


import com.demo.service.PersonService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@RestController
public class PersonController {


    private static final String PERSON_SCHEMA = "person-schema.json";

    @Autowired
    PersonService personService;

    @GET
    @RequestMapping("/person/{id}")
    public String returnPerson(@PathVariable("id") Integer id)
    {
        System.out.print(id);
        return id.toString();
    }

    @POST
    @RequestMapping("/person")
    public String addPerson(@RequestBody String body)
    {
        personService.addPerson(body);
        isPersonSchemaValidated(body, "Ajinkya");
        return body;
    }

    private Boolean isPersonSchemaValidated(String jsonData, String jsonSchema)
    {
        JSONParser parser = new JSONParser();
        return Boolean.FALSE;
    }
}
