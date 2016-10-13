package com.demo.service;


public interface SchemaService {
    /**
     * Adds schema to Redis
     * @param jsonSchema
     * @return
     */
    String addSchemaToRedis(String jsonSchema, String objectName);

    /**
     * Returns schema as string from REDIS
     * @param pathToSchema
     * @return
     */
    String getSchemaFromRedis(String pathToSchema);
}
