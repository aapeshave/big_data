package com.demo.service;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Created by ajinkya on 10/17/16.
 */
@Service
public interface UserService {
    /**
     *
     * @param userObject
     * @return
     */
    String addUser(JSONObject userObject);
}
