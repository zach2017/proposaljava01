package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class ProposalGenerationResponse {
    private String executiveSummaryPrompt;
    private String technicalApproachPrompt;
    private String teamQualificationsPrompt;
    private String pastPerformancePrompt;
    private String skillsDevelopmentPrompt;
    private String costProposalPrompt;
    private String completeProposal;
    private QualificationScore qualificationScore;
    private List<String> recommendations;
}