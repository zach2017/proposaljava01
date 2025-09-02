package ai.zactonics.genproposal.model;

import lombok.Data;

@Data
public class MissingSkill {
    private String skill;
    private Integer requiredCount;
    private Integer currentCount;
    private Integer impactOnScore;
}
