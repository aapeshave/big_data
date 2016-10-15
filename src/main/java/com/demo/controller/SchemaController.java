package com.demo.controller;

import com.demo.service.SchemaService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GET
    @RequestMapping("/{uuid}")
    public String getSchema(@PathVariable("uuid") String schemaPath)
    {
        if (!StringUtils.isBlank(schemaPath))
        {
            return schemaService.getSchemaFromRedis(schemaPath);
        }
        return null;
    }
}
