package ai.zactonics.genproposal.model;

import lombok.Data;
import java.util.List;

@Data
public class ProposalDataResponse {
    private RfpData rfpData;
    private List<Employee> employeeData;
    private List<ProjectExperience> projectExperience;
    private CompanyCapabilities companyCapabilities;
    private SkillsGapAnalysis skillsGapAnalysis;
    private ProposalOutputTemplate proposalOutputTemplate;
}