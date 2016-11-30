package com.demo.service.impl;

import com.demo.service.PlanService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajinkyapeshave on 11/30/16.
 */
@Service
public class PlanServiceImpl implements PlanService {

    @Override
    public String addPlan(JsonNode planNode) {
        if (planNode.has("userUid"))
        {
            ArrayNode benefits = (ArrayNode) planNode.get("benefits");
            for (JsonNode benefit : benefits)
            {
                System.out.print(benefit.toString());
            }
        }
        return null;
    }
}
