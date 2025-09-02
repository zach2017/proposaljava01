package ai.zactonics.genproposal.model;

import lombok.Data;

@Data
public class TrainingRecommendation {
    private String employeeId;
    private String recommendedCert;
    private Double cost;
    private Integer timelineWeeks;
    private Integer roiImprovement;
}