package ai.zactonics.genproposal.model;

import lombok.Data;

@Data
public class ProposalSection {
    private String section;
    private Boolean autoGenerate;
    private Boolean includeQualificationPercentage;
    private Boolean mapToRequirements;
    private Boolean includeResumes;
    private Boolean includeCerts;
    private Boolean includeTrainingPlan;
    private Boolean includeTimeline;
    private Boolean includeInvestment;
    private Boolean showImprovedQualification;
    private Integer maxCaseStudies;
    private String prioritizeBy;
}