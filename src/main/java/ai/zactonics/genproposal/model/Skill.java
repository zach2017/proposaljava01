package ai.zactonics.genproposal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {
    private String skillName;
    private String proficiencyLevel;
    private Integer yearsExperience;
}