package com.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.elasticsearch.ResourceNotFoundException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Created by ajinkyapeshave on 11/30/16.
 */
public interface PlanService {
    /**
     * @param planNode get a plan node from controller
     * @return details about the added plan
     */
    String addPlan(JsonNode planNode);

    /**
     * @param planUid to get the plan
     * @return the plan associated with the uid
     */
    JSONObject getPlan(String planUid) throws ResourceNotFoundException, ParseException;

    /**
     *
     * @param benefitObject to be added
     * @param planUid
     * @return plan object
     */
    JSONObject addBenefitToPlan(String benefitObject, String planUid) throws ResourceNotFoundException, ParseException;
}
