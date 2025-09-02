package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SampleRfp {
    private String rfpNumber;
    private String title;
    private String issuingOrganization;
    private String submissionDeadline;
    private String background;
    private List<String> scopeOfWork;
    private List<String> technicalRequirements;
    private Map<String, String> staffingRequirements;
    private Map<String, Integer> evaluationCriteria;
    private List<String> submissionRequirements;
}