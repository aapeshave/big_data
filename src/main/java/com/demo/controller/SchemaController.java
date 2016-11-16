package com.demo.controller;

import com.demo.service.SchemaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(description = "Api to do CRUD operations on Json Schema", produces = "application/json")
@RequestMapping("/schema")
public class SchemaController {
    @Autowired
    SchemaService schemaService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String addSchema(@RequestBody String entity) {
        if (entity != null) {
            JSONParser parser = new JSONParser();
            try {
                Map<String, Object> object = (Map<String, Object>) parser.parse(entity);
                String objectName = (String) object.get("objectName");
                return schemaService.addSchemaToRedis(entity, objectName);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequestMapping(value = "/v1/", method = RequestMethod.POST)
    public String addSchemaNewVersion(@RequestBody String entity) {
        if (!StringUtils.isBlank(entity)) {
            return schemaService.addNewSchema(entity);
        }
        return null;
    }

    @ApiOperation(value = "Get json schema required for any object", response = String.class)
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public String getSchema(@PathVariable("uuid") String schemaPath) {
        if (!StringUtils.isBlank(schemaPath)) {
            return schemaService.getSchemaFromRedis(schemaPath);
        }
        return null;
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    public boolean deleteSchema(@PathVariable("uuid") String schemaPath) {
        if (!StringUtils.isBlank(schemaPath)) {
            return schemaService.deleteSchemaFromRedis(schemaPath);
        }
        return Boolean.FALSE;
    }

    // TODO: Implementation is not done
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PATCH)
    public String updateSchema(@PathVariable("uuid") String schemaPath,
                               @RequestParam String propertyName,
                               @RequestParam String value) {
        System.out.println("schemaPAth: " + schemaPath + "property Name: " + propertyName);
        return null;
    }
}
