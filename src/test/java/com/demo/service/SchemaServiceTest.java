package com.demo.service;

import com.demo.service.impl.SchemaServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ajinkya on 11/5/16.
 */
public class SchemaServiceTest {

    SchemaServiceImpl schemaService;

    public static final String SCHEMA__address = "{\n" +
            "  \"objectName\": \"address\",\n" +
            "  \"type\": \"object\",\n" +
            "  \"properties\": {\n" +
            "    \"country\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"zipCode\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"city\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"objectName\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"street1\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"street2\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"state\": {\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"required\": [\n" +
            "    \"objectName\",\n" +
            "    \"street1\",\n" +
            "    \"street2\",\n" +
            "    \"city\",\n" +
            "    \"state\",\n" +
            "    \"country\",\n" +
            "    \"zipCode\"\n" +
            "  ]\n" +
            "}";

    @Before
    public void setUp()
    {
        schemaService = new SchemaServiceImpl();
    }

    @Test
    public void testDeleteSchemaFromRedis() throws Exception {

    }

    @Test
    public void testValidateSchema() throws Exception {

    }

    @Test
    public void testValidateFieldInSchema() throws Exception {

    }

    @Test
    public void testAddNewSchema() throws Exception {

    }

    @Test
    public void testPatchSchema() throws Exception {
        String pathToSchema = "SCHEMA__user";
        String parameterName = "address";
        String parameterValue = SCHEMA__address;

        String result = schemaService.patchSchema(pathToSchema, parameterName, parameterValue);
        Assert.assertNotNull(result);
    }
}