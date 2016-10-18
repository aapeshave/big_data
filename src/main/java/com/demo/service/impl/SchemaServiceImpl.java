package com.demo.service.impl;

import com.demo.service.SchemaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.ws.rs.BadRequestException;
import java.io.IOException;

@Service
public class SchemaServiceImpl
        implements SchemaService {
    public static final String SCHEMA_PREFIX = "SCHEMA";

    @Override
    public String addSchemaToRedis(String jsonSchema, String objectName) {
        Jedis jedis = new Jedis("localhost");
        try {
            jedis.set(SCHEMA_PREFIX + "__" + objectName, jsonSchema);
            return SCHEMA_PREFIX + "__" + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }

    @Override
    public String getSchemaFromRedis(String pathToSchema) {
        Jedis jedis = new Jedis("localhost");
        try {
            return jedis.get(pathToSchema);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }

    @Override
    public Boolean deleteSchemaFromRedis(String pathToSchema) {
        Jedis jedis = new Jedis("localhost");
        try {
            long result = jedis.del(pathToSchema);
            if (result == 1) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }

    @Override
    public Boolean validateSchema(String pathToSchema, String data) throws IOException {
        String jsonSchema = getSchemaFromRedis(pathToSchema);
        if (jsonSchema!=null && !(jsonSchema.isEmpty()))
        {
            final JsonNode d = JsonLoader.fromString(data);
            final JsonNode s = JsonLoader.fromString(jsonSchema);

            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JsonValidator v = factory.getValidator();

            ProcessingReport report = null;
            try {
                report = v.validate(s, d);
            } catch (ProcessingException e) {
                e.printStackTrace();
            }
            if (report !=null) {
                if (!report.toString().contains("success")) {
                    throw new BadRequestException(
                            report.toString());
                }
                else
                    return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
