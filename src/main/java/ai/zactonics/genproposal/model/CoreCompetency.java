package ai.zactonics.genproposal.model;

import lombok.Data;

@Data
public class CoreCompetency {
    private String capability;
    private String maturityLevel;
    private Integer yearsExperience;
    private Integer successfulProjects;
    private Integer certifiedStaff;
}