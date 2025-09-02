package ai.zactonics.genproposal.model;

import lombok.Data;

@Data
public class SuccessMetrics {
    private Boolean onTime;
    private Boolean onBudget;
    private String budgetVariance;
    private Double clientSatisfaction;
    private String costSavingsAchieved;
    private String securityIncidentsReduced;
}