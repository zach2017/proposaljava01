package ai.zactonics.genproposal.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectExperience {
    private String projectId;
    private String projectName;
    private String client;
    private String industry;
    private String contractValue;
    private String duration;
    private LocalDate completionDate;
    private SuccessMetrics successMetrics;
    private List<String> teamMembers;
    private List<String> technologiesUsed;
    private List<String> keyAchievements;
    private List<String> lessonsLearned;
    private Boolean referenceable;
    private ReferenceContact referenceContact;
}