package ai.zactonics.genproposal.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
public class RfpData {
    private String rfpId;
    private String title;
    private String issuingOrganization;
    private LocalDate dueDate;
    private String contractValue;
    private String contractDuration;
    private ExtractedRequirements extractedRequirements;
    private Map<String, Integer> evaluationCriteria;
}