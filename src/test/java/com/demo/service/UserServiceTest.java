package com.demo.service;

import com.demo.service.impl.UserServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Created by ajinkya on 10/18/16.
 */
public class UserServiceTest {


    UserServiceImpl userService;

    public static final String SAMPLE_VALID_USER_BODY = "{\n" +
            "  \"objectName\": \"user\",\n" +
            "  \"password\": \"labore ut\",\n" +
            "  \"userName\": \"cillum sed\",\n" +
            "  \"role\": {\n" +
            "  \t\"objectName\": \"role\",\n" +
            "    \"roleId\": \"dolor mollit labore\"\n" +
            "  },\n" +
            "  \"tokens\": [\n" +
            "    {\n" +
            "    \t\"objectName\": \"token\",\n" +
            "      \"createdOn\": \"amet\",\n" +
            "      \"issuer\": \"\",\n" +
            "      \"role\": \"culpa non magna amet\",\n" +
            "      \"accessUrl\": \"do\",\n" +
            "      \"tokenId\": \"nisi quis ut\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"_uid\": \"dolor\",\n" +
            "  \"createdOn\": \"ullamco in irure\"\n" +
            "}";

    public static final String SAMPLE_USER_2 = "{\n" +
            "  \"objectName\": \"user\",\n" +
            "  \"password\": \"labore ut\",\n" +
            "  \"userName\": \"cillum sed\",\n" +
            "  \"role\": {\n" +
            "  \t\"objectName\": \"role\",\n" +
            "    \"roleId\": \"dolor mollit labore\",\n" +
            "    \"roleName\": \"admin\"\n" +
            "  },\n" +
            "  \"_uid\": \"dolor\",\n" +
            "  \"createdOn\": \"ullamco in irure\"\n" +
            "}";

    @Before
    public void setUp() throws Exception {
        userService = new UserServiceImpl();
    }

    @Test
    public void testAddUser() throws Exception {

    }

    @Test
    public void testGetUser() throws Exception {

    }

    @Test
    public void testNewAddUser() throws Exception {
        JSONObject object = getSampleUserObject(SAMPLE_USER_2);
        String s = userService.newAddUser(object);
        JSONObject responseObject = (JSONObject) new JSONParser().parse(s);
        String userKey = (String) responseObject.get("user");
        responseObject = userService.newGetUser(userKey);
        System.out.println(responseObject.toJSONString());
    }

    @Test
    public void testNewUpdateRoleName() throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String userUid = "user__1";
        String parameterName = "roleName";
        String parameterKey = "role__1";
        String parameterValue = "READ_ONLY";
        Boolean result = userService.newUpdateUser(userUid, parameterName, parameterKey, parameterValue);
        Assert.assertTrue(result);
    }

    @Test
    public void testNewUpdateRole() throws ParseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String parameterValue = "{\n" +
                "    \"_createdOn\": \"1478272985\",\n" +
                "    \"roleId\": \"1\",\n" +
                "    \"roleName\": \"read__only\",\n" +
                "    \"objectName\": \"role\",\n" +
                "    \"_uid\": \"role__1\"\n" +
                "  }";
        String userUid = "user__1";
        String parameterName = "role";
        String parameterKey = "role__1";
        Boolean result = userService.newUpdateUser(userUid, parameterName, parameterKey, parameterValue);
        Assert.assertTrue(result);
    }


    private JSONObject getSampleUserObject(String userBody) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(userBody);
    }


}