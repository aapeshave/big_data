package com.demo.controller;

import com.demo.service.PersonService;
import com.demo.service.SchemaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.IOException;

/**
 * Created by ajinkya on 10/22/16.
 */

@RestController
@RequestMapping("/v1/person")
public class PersonControllerV1 {

    @Autowired
    SchemaService _schemaService;

    @Autowired
    PersonService _personService;

    private static final String SCHEMA_LOCATION = "SCHEMA__person";

    @POST
    @RequestMapping("/")
    public String addPerson(@RequestBody String body, HttpServletResponse response) throws IOException {
        if (!(body.isEmpty())) {
            try {
                if (_schemaService.validateSchema(SCHEMA_LOCATION, body)) {
                    String result = _personService.v1AddPerson(body);
                    return result;
                } else {
                    response.sendError(403, "Schema not validated");
                }
            } catch (IOException e) {
                response.sendError(403, "Schema not validated");
            }
        } else {
            System.out.println("body is blank");
        }
        return null;
    }

    @GET
    @RequestMapping("/{uuid}")
    public String getPerson(@PathVariable("uuid") String uid) {
        if (!StringUtils.isBlank(uid)) {
            return _personService.v1GetPerson(uid);
        }
        return null;
    }
}
