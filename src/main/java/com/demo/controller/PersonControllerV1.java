package com.demo.controller;

import com.demo.service.PersonService;
import com.demo.service.SchemaService;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.IOException;

/**
 * Created by ajinkya on 10/22/16.
 */

@RestController
@Api(description = "New Api to do CRUD operations on a person with or without user account.")
@RequestMapping("/v1/person")
public class PersonControllerV1 {

    @Autowired
    SchemaService _schemaService;

    @Autowired
    PersonService _personService;

    private static final String SCHEMA_LOCATION = "SCHEMA__person";

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "Create a person",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 403,
                    message = "json schema not validated",
                    responseHeaders = @ResponseHeader(
                            name = "BadRequest",
                            description = "valid json schema should be provided")),
            @ApiResponse(code = 500,
                    message = "Internal Server Error",
                    responseHeaders = @ResponseHeader(
                            name = "GENERAL_ERROR",
                            description = "unhandled exception occured"))
    })
    public String addPerson(@RequestBody String body, HttpServletResponse response) throws IOException {
        if (!(body.isEmpty())) {
            try {
                if (_schemaService.validateSchema(SCHEMA_LOCATION, body)) {
                    String result = _personService.v1AddPerson(body);
                    return result;
                } else {
                    response.sendError(403, "Schema not validated");
                }
            } catch (IOException | ProcessingException e) {
                response.sendError(403, "Schema not validated");
            }
        } else {
            System.out.println("body is blank");
        }
        return null;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a person from database",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500,
                    message = "Internal Server Error",
                    responseHeaders = @ResponseHeader(
                            name = "GENERAL_ERROR",
                            description = "unhandled exception occured"))
    })
    public String getPerson(@PathVariable("uuid") String uid) {
        if (!StringUtils.isBlank(uid)) {
            return _personService.v1GetPerson(uid);
        }
        return null;
    }
}
