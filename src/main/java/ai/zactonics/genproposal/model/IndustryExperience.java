package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class IndustryExperience {
    private String industry;
    private Integer years;
    private Integer projects;
    private List<String> certifications;
}