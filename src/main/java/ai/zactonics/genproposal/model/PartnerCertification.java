package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class PartnerCertification {
    private String partner;
    private String level;
    private List<String> competencies;
}