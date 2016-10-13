package com.demo.service.impl;

import com.demo.service.SchemaService;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class SchemaServiceImpl
    implements SchemaService
{
    public static final String SCHEMA_PREFIX = "SCHEMA";
    @Override
    public String  addSchemaToRedis(String jsonSchema, String objectName) {
        Jedis jedis = new Jedis("localhost");
        try {
            jedis.set(SCHEMA_PREFIX+ "__" +objectName, jsonSchema);
            return SCHEMA_PREFIX+ "__" +objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
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
        }
        finally {
            jedis.close();
        }
    }
}
