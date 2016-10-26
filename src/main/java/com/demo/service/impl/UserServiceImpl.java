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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        JSONObject token = processAndAddToken(userObject);

        jedis.set((String) userObject.get("userUid"), userObject.toJSONString());
        jedis.close();

        JSONObject response = new JSONObject();
        response.put("userUid", userObject.get("userUid"));
        response.put("personUid", personObject.get("personUid"));
        response.put("Authorization Header", token.get("tokenUid"));

        return response.toJSONString();
    }

	private JSONObject processAndAddToken(JSONObject userObject) throws JsonProcessingException, ParseException {
		JSONObject token = tokenService.createAccessToken((String) userObject.get("userUid"), (String) userObject.get("role"), "ACCESS_TOKEN");

        JSONArray tokens = (JSONArray) userObject.get("tokens");
        if (tokens == null) {
            tokens = new JSONArray();
        }
        assert tokens != null;
        tokens.add(token);
        userObject.put("tokens", tokens);
		return token;
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
	@Override
    public String newAddUser(JSONObject body) {
        Jedis jedis = new Jedis("localhost");
        JSONParser parser = new JSONParser();
        JSONObject responseObject = new JSONObject();
        try {
            Map<String, Object> bodyObj = (HashMap<String, Object>) body;
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
                        ((JSONObject) object).put("_uid", uid);

                        //Add to Jedis
                        jedis.set(uid, ((JSONObject) object).toJSONString());
                        // This is done to create link
                        objectKeys.add(uid);
                    }
                    personObject.put(objectType, objectKeys);
                    responseObject.put(objectType, objectKeys);
                } else if (property instanceof JSONObject) {
                    objectType = (String) ((JSONObject) property).get("objectName");
                    jedis.incr(objectType);
                    uid = objectType + "__" + jedis.get(objectType);
                    ((JSONObject) property).put("_createdOn", getUnixTimestamp());
                    ((JSONObject) property).put("_uid", uid);
                    // TODO: Insert to jedis over here
                    jedis.set(uid, ((JSONObject) property).toJSONString());
                    // Creating link over here
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("objectType", objectType);
                    jsonObject.put("objectValue", uid);
                    personObject.put(objectType, jsonObject);
                    responseObject.put(objectType, jsonObject);
                } else {
                    personObject.put(propertyKey, property);
                }
            }
            // TODO: Insert to jedis over here
            jedis.set((String) responseObject.get(bodyObj.get("objectName")), personObject.toString());
            return responseObject.toJSONString();

        } finally {
            jedis.close();
        }
    }

    private void processInitialData(Jedis jedis,
                                    Map<String, Object> bodyObj,
                                    JSONObject personObject,
                                    JSONObject responseObject) {
        String objectType = (String) bodyObj.get("objectName");
        jedis.incr(objectType);
        String uid = objectType + "__" + jedis.get(objectType);
        personObject.put("_createdOn", getUnixTimestamp());
        personObject.put("_uid", uid);
        responseObject.put(objectType, uid);
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

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject newGetUser(String pathToObject) {
		Jedis jedis = new Jedis("localhost");
        JSONObject response = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            JSONObject resultObject = (JSONObject) new JSONParser().parse(jedis.get(pathToObject));
            if (resultObject != null) {
                for (Object entryKey : resultObject.keySet()) {
                    Object entry = resultObject.get(entryKey);
                    if (entry instanceof JSONObject) {
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
                return response;
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
}
