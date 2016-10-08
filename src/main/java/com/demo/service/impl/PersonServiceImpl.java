package com.demo.service.impl;


import com.demo.pojo.AccessToken;
import com.demo.pojo.User;
import com.demo.service.PersonService;
import com.demo.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Service
public class PersonServiceImpl
        implements PersonService {

    private String PERSON_COUNT = "PERSON_COUNT";

    private String USER_COUNT = "USER_COUNT";

    @Autowired
    TokenService tokenService;

    @Override
    public String processAndAddPerson(String personData) {

        Jedis jedis = new Jedis("localhost");

        User user = processUser(personData);

        user = processKeys(jedis, user);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responseMap;
        String response = null;
        try {
            responseMap = new HashMap<>();
            responseMap.put("userUUID", user.getUserUid());
            responseMap.put("personUUID", user.getPerson().getPersonUid());

            // Create a token for user
            AccessToken token = tokenService.createAccessTokenAPI(user.getUserUid(), user.getRole(), "ACCESS_TOKEN");
            if (token != null) {
                user.getTokens().add(token);
                responseMap.put("tokenAUTH", token.getTokenUid());
            }
            jedis.set(user.getPerson().getPersonUid(), mapper.writeValueAsString(user.getPerson()));
            jedis.set(user.getUserUid(), mapper.writeValueAsString(user));
            response = mapper.writeValueAsString(responseMap);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        jedis.close();
        return response;
    }

    @Override
    public String getPerson(String personUID) {
        Jedis jedis = new Jedis("localhost");
        if (personUID != null && !(personUID.isEmpty())) {
            return jedis.get(personUID);
        } else return null;
    }

    private User processKeys(Jedis jedis, User user) {
        jedis.incr(PERSON_COUNT);
        jedis.incr(USER_COUNT);

        String personUid = "person" + "__" + user.getPerson().getFirstName() + "__" + jedis.get(PERSON_COUNT);
        String userUid = "user" + "__" + user.getUserName() + "__" + jedis.get(USER_COUNT);

        user.setUserUid(userUid);
        user.getPerson().setPersonUid(personUid);
        return user;
    }

    private User processUser(String personData) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject personEntity = (JSONObject) parser.parse(personData);


            User user = new User((String) personEntity.get("username"),
                    (String) personEntity.get("password"),
                    (String) personEntity.get("firstName"),
                    (String) personEntity.get("lastName"),
                    (String) personEntity.get("email"));
            return user;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
