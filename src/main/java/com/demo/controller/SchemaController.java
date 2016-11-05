package com.demo.controller;

import com.demo.service.SchemaService;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.util.Map;

@RestController
@RequestMapping("/schema")
public class SchemaController {
    @Autowired
    SchemaService schemaService;

    @POST
    @RequestMapping("/")
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

    @POST
    @RequestMapping("/v1/")
    public String addSchemaNewVersion(@RequestBody String entity) {
        if (!StringUtils.isBlank(entity)) {
            return schemaService.addNewSchema(entity);
        }
        return null;
    }

    @GET
    @RequestMapping("/{uuid}")
    public String getSchema(@PathVariable("uuid") String schemaPath) {
        if (!StringUtils.isBlank(schemaPath)) {
            return schemaService.getSchemaFromRedis(schemaPath);
        }
        return null;
    }

    @DELETE
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
