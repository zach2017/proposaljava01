package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class Requirement {
    private String reqId;
    private String category;
    private String description;
    private List<String> requiredSkills;
    private List<String> requiredCertifications;
    private Integer minYearsExperience;
    private Integer weight;
} 