package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class SkillsGapAnalysis {
    private String rfpId;
    private Integer currentQualificationPercentage;
    private List<MissingSkill> missingSkills;
    private List<TrainingRecommendation> trainingRecommendations;
    private List<WhatIfScenario> whatIfScenarios;
}