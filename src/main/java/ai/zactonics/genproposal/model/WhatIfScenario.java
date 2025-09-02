package ai.zactonics.genproposal.model;

import lombok.Data;

@Data
public class WhatIfScenario {
    private String scenarioName;
    private String description;
    private Double investment;
    private Integer timelineWeeks;
    private Integer newQualificationPercentage;
    private Integer additionalRfpsQualified;
    private String potentialRevenue;
}