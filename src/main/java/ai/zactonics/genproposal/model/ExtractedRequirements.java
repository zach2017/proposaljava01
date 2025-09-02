package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExtractedRequirements {
    private List<Requirement> mandatoryRequirements;
    private List<Requirement> preferredRequirements;
    private Map<String, Integer> teamCompositionRequirements;
}
