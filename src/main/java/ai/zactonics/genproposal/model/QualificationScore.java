package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class QualificationScore {
    private Integer currentScore;
    private Map<String, Integer> scenarioScores;
    private List<String> criticalGaps;
}
