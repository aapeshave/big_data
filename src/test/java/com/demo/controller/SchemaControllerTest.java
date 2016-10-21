package com.demo.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SchemaControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testAddJsonSchema() throws Exception {
        Map<String, String> params = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(new FileReader("/Users/ajinkya/IdeaProjects/spring_boot_check/person_schema.json"));
        String schema = object.toString();
        params.put("schema", schema);
        params.put("objectName", "person");
    }

    @Test
    public void getJSonSchema() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders
                .get("/schema/SCHEMA__User")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status()
                        .isOk()).andReturn().getResponse().getContentAsString();
    }
}
