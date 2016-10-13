package com.demo.controller;

import com.demo.service.SchemaService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.POST;
import java.util.Map;

@RestController
@RequestMapping("/schema")
public class SchemaController {
    @Autowired
    SchemaService schemaService;

    @POST
    @RequestMapping("/")
    public String addSchema(@RequestBody String entity)
    {
        if (entity != null)
        {
            JSONParser parser = new JSONParser();
            try {
                Map<String, Object> object = (Map<String, Object>) parser.parse(entity);
                int i=0;
                String objectName = (String) object.get("objectName");
                String jsonSchema = (String) object.get("jsonSchema").toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //return schemaService.addSchemaToRedis(entity.jsonSchema, entity.objectName.toString());
        }
        return null;
    }

    public static class SchemaEntity
    {
        @JsonProperty String jsonSchema;
        @JsonProperty JSONPObject objectName;
    }

}
