package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class ProposalOutputTemplate {
    private List<ProposalSection> sections;
}