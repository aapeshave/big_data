package com.demo.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by ajinkyapeshave on 11/30/16.
 */
public interface PlanService {
    /**
     *
     * @param planNode get a plan node from controller
     * @return details about the added plan
     */
    String addPlan(JsonNode planNode);
}
