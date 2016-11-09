package com.demo.service.impl;


import com.demo.pojo.AccessToken;
import com.demo.pojo.User;
import com.demo.service.PersonService;
import com.demo.service.TokenService;
import com.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PersonServiceImpl
        implements PersonService {

    private String PERSON_COUNT = "PERSON_COUNT";

    private String USER_COUNT = "USER_COUNT";

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

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

    @Override
    @SuppressWarnings("unchecked")
    public String v1AddPerson(String personBody) {
        Jedis jedis = new Jedis("localhost");
        JSONParser parser = new JSONParser();
        JSONObject responseObject = new JSONObject();
        try {
            Map<String, Object> bodyObj = (HashMap<String, Object>) parser.parse(personBody);
            JSONObject personObject = new JSONObject();

            String objectType = null;
            String uid = null;
            // Create initial data for personObject
            processInitialData(jedis, bodyObj, personObject, responseObject);

            for (String propertyKey : bodyObj.keySet()) {
                Object property = bodyObj.get(propertyKey);
                if (property instanceof JSONArray) {
                    JSONArray propertyArray = (JSONArray) property;
                    objectType = null;
                    JSONArray objectKeys = new JSONArray();
                    for (Object object : propertyArray) {
                        objectType = (String) ((JSONObject) object).get("objectName");
                        jedis.incr(objectType);
                        uid = objectType + "__" + jedis.get(objectType);
                        ((JSONObject) object).put("_createdOn", getUnixTimestamp());
                        ((JSONObject) object).put("_id", uid);

                        //Add to Jedis
                        jedis.set(uid, ((JSONObject) object).toJSONString());
                        // This is done to create link
                        objectKeys.add(uid);
                    }
                    personObject.put(objectType, objectKeys);
                    responseObject.put(objectType, objectKeys);
                } else if (property instanceof JSONObject) {
                    JSONObject jsonObject = new JSONObject();
                    objectType = (String) ((JSONObject) property).get("objectName");
                    jsonObject.put("objectType", objectType);
                    if (objectType.equals("user")) {
                        String userString = userService.newAddUser((JSONObject) property);
                        JSONObject userObject = (JSONObject) parser.parse(userString);
                        jsonObject.put("objectValue", userObject.get("user"));
                        responseObject.put("Authorization", userObject.get("Authorization"));
                        responseObject.put(objectType, userObject.get("user"));
                        personObject.put(objectType, jsonObject);
                    } else {
                        jedis.incr(objectType);
                        uid = objectType + "__" + jedis.get(objectType);
                        ((JSONObject) property).put("_createdOn", getUnixTimestamp());
                        ((JSONObject) property).put("_id", uid);
                        jedis.set(uid, ((JSONObject) property).toJSONString());
                        // Creating link over here

                        jsonObject.put("objectValue", uid);
                        personObject.put(objectType, jsonObject);
                        responseObject.put(objectType, jsonObject);
                    }
                } else {
                    personObject.put(propertyKey, property);
                }
            }
            jedis.set((String) responseObject.get(bodyObj.get("objectName")), personObject.toString());
            System.out.println("Printing personObj after adding everything:" + personObject.toString());
            return responseObject.toJSONString();

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String v1GetPerson(String personUid) {
        Jedis jedis = new Jedis("localhost");
        JSONObject response = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            JSONObject resultObject = (JSONObject) new JSONParser().parse(jedis.get(personUid));
            if (resultObject != null) {
                for (Object entryKey : resultObject.keySet()) {
                    Object entry = resultObject.get(entryKey);
                    if (entry instanceof JSONObject) {
                        if (((JSONObject) entry).get("objectType").equals("user")) {
                            JSONObject jsonObject = userService.newGetUser((String) ((JSONObject) entry).get("objectValue"));
                            response.put(((JSONObject) entry).get("objectType"), jsonObject);
                        }
                        JSONObject object = getJSONObjectFromObject(jedis, (JSONObject) entry, parser);
                        response.put(((JSONObject) entry).get("objectType"), object);
                    } else if (entry instanceof JSONArray) {
                        JSONArray arrayEntries = new JSONArray();
                        JSONArray entryArray = (JSONArray) entry;
                        String objectType = null;
                        for (Object object : entryArray) {
                            JSONObject arrayEntry = (JSONObject) parser.parse(jedis.get((String) object));
                            objectType = (String) arrayEntry.get("objectName");
                            arrayEntries.add(arrayEntry);
                        }
                        response.put(objectType, arrayEntries);
                    } else {
                        response.put(entryKey, entry);
                    }
                }
                return response.toJSONString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getJSONObjectFromObject(Jedis jedis, JSONObject entry, JSONParser parser) throws ParseException {
        JSONObject object = entry;
        String objectString = jedis.get((String) object.get("objectValue"));
        JSONObject objectMap = (JSONObject) parser.parse(objectString);
        return objectMap;
    }

    private void processInitialData(Jedis jedis,
                                    Map<String, Object> bodyObj,
                                    JSONObject personObject,
                                    JSONObject responseObject) {
        String objectType = (String) bodyObj.get("objectName");
        jedis.incr(objectType);
        String uid = objectType + "__" + jedis.get(objectType);
        personObject.put("_createdOn", getUnixTimestamp());
        personObject.put("_id", uid);
        responseObject.put(objectType, uid);
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

    private String getUnixTimestamp() {
        Long unixDate = new Date().getTime() / 1000;
        String unixDateString = unixDate.toString();
        return unixDateString;
    }
}
