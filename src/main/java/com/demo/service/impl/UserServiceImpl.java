package com.demo.service.impl;

import com.demo.service.TokenService;
import com.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.ws.rs.InternalServerErrorException;
import java.util.Date;
import java.util.List;

/**
 * Created by ajinkya on 10/17/16.
 */
@Service
@Configurable("jedisConfiguration")
public class UserServiceImpl
        implements UserService {
    @Autowired
    TokenService tokenService;
    private String PERSON_COUNT = "PERSON_COUNT";
    private String USER_COUNT = "USER_COUNT";

    @Override
    public String addUser(JSONObject userObject) throws JsonProcessingException, ParseException {
        Jedis jedis = new Jedis("localhost");

        jedis.incr(PERSON_COUNT);
        jedis.incr(USER_COUNT);

        JSONObject personObject = (JSONObject) userObject.get("person");
        jedis.del("person");
        processKeys(userObject, jedis, personObject);
        userObject.put("person", personObject);

        JSONObject token = tokenService.createAccessToken((String) userObject.get("userUid"), (String) userObject.get("role"), "ACCESS_TOKEN");

        JSONArray tokens = (JSONArray) userObject.get("tokens");
        if (tokens == null) {
            tokens = new JSONArray();
        }
        assert tokens != null;
        tokens.add(token);
        userObject.put("tokens", tokens);

        jedis.set((String) userObject.get("userUid"), userObject.toJSONString());
        jedis.close();

        JSONObject response = new JSONObject();
        response.put("userUid", userObject.get("userUid"));
        response.put("personUid", personObject.get("personUid"));
        response.put("Authorization Header", token.get("tokenUid"));

        return response.toJSONString();
    }

    @Override
    public String getUser(String userPath) throws ParseException {
        Jedis jedis = new Jedis("localhost");
        String result = jedis.get(userPath);
        JSONObject object = new JSONObject((JSONObject) new JSONParser().parse(result));
        jedis.close();
        return object.toJSONString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String updateUser(String userPath, String parameterName, String parameterValue) {
        try (Jedis jedis = new Jedis("localhost")) {
            JSONObject user = null;
            user = new JSONObject((JSONObject) new JSONParser().parse(jedis.get(userPath)));
            if (user != null) {
                Object object = user.get(parameterName);
                if (object instanceof List) {
                    JSONObject parameterObject = (JSONObject) new JSONParser().parse(parameterValue);
                    JSONArray objectArray = (JSONArray) object;
                    parameterObject.put("createdOn", getUnixTimestamp());
                    objectArray.add(parameterObject);
                } else if (object instanceof JSONObject) {
                    JSONObject parameterObject = (JSONObject) new JSONParser().parse(parameterValue);
                    parameterObject.put("modifiedOn", getUnixTimestamp());
                    user.put(parameterName, parameterObject);
                } else if (object instanceof String) {
                    user.put(parameterName, parameterValue.toString());
                }
                Long del = jedis.del(userPath);
                if (del == 1) {
                    jedis.set(userPath, user.toString());
                    JSONObject response = new JSONObject();
                    response.put("userUid", user.get("userUid"));
                    JSONObject personObj = (JSONObject) user.get("person");
                    response.put("personUid", personObj.get("personUid"));

                    return response.toJSONString();
                }
            }
        } catch (ParseException e) {
            throw new InternalServerErrorException("Failed while patching user");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private void processKeys(JSONObject userObject, Jedis jedis, JSONObject personObject) {
        String personUid = "person" + "__" + personObject.get("firstName") + "__" + jedis.get(PERSON_COUNT);
        String userUid = "user" + "__" + userObject.get("userName") + "__" + jedis.get(USER_COUNT);

        userObject.put("userUid", userUid);
        personObject.put("personUid", personUid);

        userObject.put("createdOn", getUnixTimestamp());
        personObject.put("createdOn", getUnixTimestamp());
    }

    private String getUnixTimestamp() {
        Long unixDate = new Date().getTime()/1000;
        String unixDateString = unixDate.toString();
        return unixDateString;
    }
}
