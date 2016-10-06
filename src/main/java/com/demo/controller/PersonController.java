package com.demo.controller;


import com.demo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

@RestController
public class PersonController {

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
        return body;
    }
}
