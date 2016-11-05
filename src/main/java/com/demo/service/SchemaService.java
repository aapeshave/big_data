package com.demo.service;


import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface SchemaService {
    /**
     * Adds schema to Redis
     * @param jsonSchema
     * @return
     */
    String addSchemaToRedis(String jsonSchema, String objectName) throws ParseException;

    /**
     * Returns schema as string from REDIS
     * @param pathToSchema
     * @return
     */
    String getSchemaFromRedis(String pathToSchema);

    /**
     * Deletes schema stored in the database and return success message
     * @param pathToSchema
     * @return
     */
    Boolean deleteSchemaFromRedis(String pathToSchema);

    /**
     *
     * @param pathToSchema
     * @param data
     * @return
     */
    Boolean validateSchema(String pathToSchema, String data) throws IOException, ProcessingException;

    /**
     *
     * @param pathToSchema
     * @param data
     * @return
     */
    Boolean validateFieldInSchema(String pathToSchema, String data);

    /**
     *
     * @param schemaBody
     * @return
     */
    String addNewSchema(String schemaBody);
}
