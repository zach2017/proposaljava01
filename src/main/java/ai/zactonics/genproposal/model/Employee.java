package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class Employee {
    private String employeeId;
    private String name;
    private String title;
    private Integer yearsExperience;
    private String clearanceLevel;
    private Integer availabilityPercentage;
    private Double hourlyRate;
    private List<Skill> currentSkills;
    private List<Certification> currentCertifications;
    private List<PlannedCertification> plannedCertifications;
    private List<String> resumeHighlights;
}