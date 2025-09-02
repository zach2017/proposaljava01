package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class CompanyCapabilities {
    private List<CoreCompetency> coreCompetencies;
    private List<IndustryExperience> industryExperience;
    private List<PartnerCertification> partnerCertifications;
}
