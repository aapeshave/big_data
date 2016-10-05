package com.demo.controller;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;

@RestController
public class PersonController {
    @GET
    @RequestMapping("/person/{id}")
    public String returnPerson(@PathVariable("id") Integer id)
    {
        System.out.print(id);
        return id.toString();
    }
}
