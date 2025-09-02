package ai.zactonics.genproposal.service;

import ai.zactonics.genproposal.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromptGeneratorService {

    public String generateExecutiveSummaryPrompt(ProposalGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate an executive summary for a proposal responding to RFP: ")
              .append(request.getRfpData().getTitle()).append("\n\n");
        
        prompt.append("Key Points to Include:\n");
        prompt.append("- Company: Our company has ").append(getCompanyStrength(request)).append("\n");
        prompt.append("- Current Qualification: We are currently ")
              .append(request.getSkillsGapAnalysis().getCurrentQualificationPercentage())
              .append("% qualified for this opportunity\n");
        
        if (request.getSkillsGapAnalysis().getCurrentQualificationPercentage() < 100) {
            prompt.append("- With planned training, we can achieve ")
                  .append(getMaxQualification(request))
                  .append("% qualification\n");
        }
        
        prompt.append("- Team Size: ").append(request.getEmployeeData().size())
              .append(" qualified professionals\n");
        prompt.append("- Relevant Experience: ")
              .append(getRelevantProjectCount(request))
              .append(" similar projects completed successfully\n");
        
        prompt.append("\nEmphasize our strengths, acknowledge areas for growth, ");
        prompt.append("and demonstrate commitment to meeting all requirements through training.\n");
        prompt.append("Keep the summary confident, professional, and client-focused.");
        
        return prompt.toString();
    }

    public String generateTechnicalApproachPrompt(ProposalGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a detailed technical approach for the following requirements:\n\n");
        
        var requirements = request.getRfpData().getExtractedRequirements();
        prompt.append("MANDATORY REQUIREMENTS:\n");
        
        for (Requirement req : requirements.getMandatoryRequirements()) {
            prompt.append("- ").append(req.getDescription()).append("\n");
            prompt.append("  Required Skills: ").append(String.join(", ", req.getRequiredSkills())).append("\n");
            prompt.append("  Our Capability: ").append(assessCapability(request, req)).append("\n\n");
        }
        
        prompt.append("\nPROPOSED SOLUTION APPROACH:\n");
        prompt.append("Based on our experience with projects like:\n");
        
        for (ProjectExperience project : request.getProjectExperience()) {
            prompt.append("- ").append(project.getProjectName())
                  .append(" (").append(project.getContractValue()).append(")\n");
        }
        
        prompt.append("\nStructure the technical approach with:\n");
        prompt.append("1. Assessment & Planning Phase\n");
        prompt.append("2. Migration Strategy\n");
        prompt.append("3. Implementation Methodology\n");
        prompt.append("4. Security & Compliance Framework\n");
        prompt.append("5. Quality Assurance & Testing\n");
        prompt.append("6. Knowledge Transfer & Support\n");
        
        return prompt.toString();
    }

    public String generateTeamQualificationsPrompt(ProposalGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a team qualifications section featuring these professionals:\n\n");
        
        for (Employee emp : request.getEmployeeData()) {
            prompt.append("TEAM MEMBER: ").append(emp.getName()).append("\n");
            prompt.append("Role: ").append(emp.getTitle()).append("\n");
            prompt.append("Experience: ").append(emp.getYearsExperience()).append(" years\n");
            prompt.append("Key Skills: ");
            
            String skills = emp.getCurrentSkills().stream()
                .filter(s -> s.getProficiencyLevel().equals("Expert") || 
                            s.getProficiencyLevel().equals("Advanced"))
                .map(Skill::getSkillName)
                .collect(Collectors.joining(", "));
            prompt.append(skills).append("\n");
            
            prompt.append("Certifications: ");
            String certs = emp.getCurrentCertifications().stream()
                .map(Certification::getCertName)
                .collect(Collectors.joining(", "));
            prompt.append(certs).append("\n");
            
            if (!emp.getPlannedCertifications().isEmpty()) {
                prompt.append("Planned Certifications: ");
                String planned = emp.getPlannedCertifications().stream()
                    .map(pc -> pc.getCertName() + " (by " + pc.getPlannedCompletion() + ")")
                    .collect(Collectors.joining(", "));
                prompt.append(planned).append("\n");
            }
            
            prompt.append("Highlights:\n");
            emp.getResumeHighlights().forEach(h -> prompt.append("- ").append(h).append("\n"));
            prompt.append("\n");
        }
        
        prompt.append("\nCreate professional bios that emphasize relevant experience ");
        prompt.append("and demonstrate how this team meets or will meet all requirements.");
        
        return prompt.toString();
    }

    public String generatePastPerformancePrompt(ProposalGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a past performance section based on these relevant projects:\n\n");
        
        for (ProjectExperience project : request.getProjectExperience()) {
            prompt.append("PROJECT: ").append(project.getProjectName()).append("\n");
            prompt.append("Client: ").append(project.getClient()).append("\n");
            prompt.append("Industry: ").append(project.getIndustry()).append("\n");
            prompt.append("Value: ").append(project.getContractValue()).append("\n");
            prompt.append("Duration: ").append(project.getDuration()).append("\n");
            
            if (project.getSuccessMetrics() != null) {
                prompt.append("Performance: ");
                prompt.append("On-Time: ").append(project.getSuccessMetrics().getOnTime()).append(", ");
                prompt.append("On-Budget: ").append(project.getSuccessMetrics().getOnBudget()).append(", ");
                prompt.append("Client Satisfaction: ").append(project.getSuccessMetrics().getClientSatisfaction()).append("\n");
            }
            
            prompt.append("Key Achievements:\n");
            project.getKeyAchievements().forEach(a -> prompt.append("- ").append(a).append("\n"));
            prompt.append("\n");
        }
        
        prompt.append("Format each project as a case study that demonstrates ");
        prompt.append("relevance to the current RFP requirements. ");
        prompt.append("Emphasize quantifiable results and client satisfaction.");
        
        return prompt.toString();
    }

    public String generateSkillsDevelopmentPrompt(ProposalGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        SkillsGapAnalysis gap = request.getSkillsGapAnalysis();
        
        prompt.append("Generate a Skills Development Plan addressing these gaps:\n\n");
        
        prompt.append("CURRENT STATE:\n");
        prompt.append("- Qualification Level: ").append(gap.getCurrentQualificationPercentage()).append("%\n");
        
        prompt.append("\nIDENTIFIED GAPS:\n");
        for (MissingSkill skill : gap.getMissingSkills()) {
            prompt.append("- ").append(skill.getSkill())
                  .append(" (Need ").append(skill.getRequiredCount())
                  .append(", Have ").append(skill.getCurrentCount()).append(")\n");
        }
        
        prompt.append("\nTRAINING PLAN:\n");
        for (TrainingRecommendation rec : gap.getTrainingRecommendations()) {
            prompt.append("- Employee ").append(rec.getEmployeeId())
                  .append(": ").append(rec.getRecommendedCert())
                  .append(" (").append(rec.getTimelineWeeks()).append(" weeks, $")
                  .append(rec.getCost()).append(")\n");
        }
        
        prompt.append("\nIMPROVEMENT SCENARIOS:\n");
        for (WhatIfScenario scenario : gap.getWhatIfScenarios()) {
            prompt.append("\n").append(scenario.getScenarioName()).append(":\n");
            prompt.append("- Investment: $").append(scenario.getInvestment()).append("\n");
            prompt.append("- Timeline: ").append(scenario.getTimelineWeeks()).append(" weeks\n");
            prompt.append("- New Qualification: ").append(scenario.getNewQualificationPercentage()).append("%\n");
            prompt.append("- ROI: ").append(scenario.getPotentialRevenue()).append(" in additional opportunities\n");
        }
        
        prompt.append("\nCreate a professional development plan that shows commitment ");
        prompt.append("to meeting all requirements and continuous improvement.");
        
        return prompt.toString();
    }

    public String generateCostProposalPrompt(ProposalGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a cost proposal structure based on:\n\n");
        
        prompt.append("TEAM COMPOSITION:\n");
        for (Employee emp : request.getEmployeeData()) {
            prompt.append("- ").append(emp.getTitle())
                  .append(" (").append(emp.getName()).append(")")
                  .append(": $").append(emp.getHourlyRate()).append("/hour")
                  .append(", ").append(emp.getAvailabilityPercentage()).append("% available\n");
        }
        
        prompt.append("\nTRAINING INVESTMENTS:\n");
        double totalTrainingCost = request.getSkillsGapAnalysis()
            .getTrainingRecommendations().stream()
            .mapToDouble(TrainingRecommendation::getCost)
            .sum();
        prompt.append("Total Training Investment: $").append(totalTrainingCost).append("\n");
        
        prompt.append("\nCreate a cost breakdown including:\n");
        prompt.append("1. Labor costs by phase\n");
        prompt.append("2. Training and certification costs\n");
        prompt.append("3. Tools and infrastructure\n");
        prompt.append("4. Travel and expenses\n");
        prompt.append("5. Management and overhead\n");
        prompt.append("Demonstrate value and ROI to the client.");
        
        return prompt.toString();
    }

    public String assembleCompleteProposal(ProposalGenerationRequest request) {
        StringBuilder proposal = new StringBuilder();
        
        proposal.append("=== COMPLETE PROPOSAL GENERATION ===\n\n");
        proposal.append("Create a comprehensive proposal for: ")
                .append(request.getRfpData().getTitle()).append("\n\n");
        
        proposal.append("SECTION 1: EXECUTIVE SUMMARY\n");
        proposal.append(generateExecutiveSummaryPrompt(request)).append("\n\n");
        
        proposal.append("SECTION 2: TECHNICAL APPROACH\n");
        proposal.append(generateTechnicalApproachPrompt(request)).append("\n\n");
        
        proposal.append("SECTION 3: TEAM QUALIFICATIONS\n");
        proposal.append(generateTeamQualificationsPrompt(request)).append("\n\n");
        
        proposal.append("SECTION 4: PAST PERFORMANCE\n");
        proposal.append(generatePastPerformancePrompt(request)).append("\n\n");
        
        proposal.append("SECTION 5: SKILLS DEVELOPMENT PLAN\n");
        proposal.append(generateSkillsDevelopmentPrompt(request)).append("\n\n");
        
        proposal.append("SECTION 6: COST PROPOSAL\n");
        proposal.append(generateCostProposalPrompt(request)).append("\n\n");
        
        proposal.append("Format as a professional, persuasive proposal document ");
        proposal.append("that addresses all RFP requirements and evaluation criteria.");
        
        return proposal.toString();
    }

    public QualificationScore calculateQualificationScore(ProposalGenerationRequest request) {
        QualificationScore score = new QualificationScore();
        SkillsGapAnalysis gap = request.getSkillsGapAnalysis();
        
        score.setCurrentScore(gap.getCurrentQualificationPercentage());
        
        // Calculate potential scores for each scenario
        Map<String, Integer> scenarioScores = new HashMap<>();
        for (WhatIfScenario scenario : gap.getWhatIfScenarios()) {
            scenarioScores.put(scenario.getScenarioName(), 
                              scenario.getNewQualificationPercentage());
        }
        score.setScenarioScores(scenarioScores);
        
        // Identify critical gaps
        List<String> criticalGaps = gap.getMissingSkills().stream()
            .filter(s -> s.getImpactOnScore() >= 8)
            .map(MissingSkill::getSkill)
            .collect(Collectors.toList());
        score.setCriticalGaps(criticalGaps);
        
        return score;
    }

    public List<String> generateRecommendations(ProposalGenerationRequest request) {
        List<String> recommendations = new ArrayList<>();
        SkillsGapAnalysis gap = request.getSkillsGapAnalysis();
        
        if (gap.getCurrentQualificationPercentage() < 80) {
            recommendations.add("PRIORITY: Implement training plan to reach minimum 80% qualification");
        }
        
        // Check for critical missing certifications
        for (MissingSkill skill : gap.getMissingSkills()) {
            if (skill.getImpactOnScore() >= 8) {
                recommendations.add("CRITICAL: Acquire " + skill.getSkill() + 
                                  " certification (Impact: " + skill.getImpactOnScore() + " points)");
            }
        }
        
        // Recommend best scenario
        WhatIfScenario bestScenario = gap.getWhatIfScenarios().stream()
            .max(Comparator.comparing(s -> s.getNewQualificationPercentage() / s.getInvestment()))
            .orElse(null);
        
        if (bestScenario != null) {
            recommendations.add("RECOMMENDED: Pursue '" + bestScenario.getScenarioName() + 
                              "' strategy for best ROI");
        }
        
        // Check team availability
        long availableStaff = request.getEmployeeData().stream()
            .filter(e -> e.getAvailabilityPercentage() >= 75)
            .count();
        
        if (availableStaff < 3) {
            recommendations.add("WARNING: Limited staff availability may impact delivery");
        }
        
        return recommendations;
    }

    // Helper methods
    private String getCompanyStrength(ProposalGenerationRequest request) {
        CompanyCapabilities cap = request.getCompanyCapabilities();
        CoreCompetency strongest = cap.getCoreCompetencies().stream()
            .max(Comparator.comparing(CoreCompetency::getSuccessfulProjects))
            .orElse(null);
        
        if (strongest != null) {
            return strongest.getYearsExperience() + " years of experience in " + 
                   strongest.getCapability() + " with " + 
                   strongest.getSuccessfulProjects() + " successful projects";
        }
        return "extensive experience in cloud solutions";
    }

    private int getMaxQualification(ProposalGenerationRequest request) {
        return request.getSkillsGapAnalysis().getWhatIfScenarios().stream()
            .mapToInt(WhatIfScenario::getNewQualificationPercentage)
            .max()
            .orElse(request.getSkillsGapAnalysis().getCurrentQualificationPercentage());
    }

    private int getRelevantProjectCount(ProposalGenerationRequest request) {
        return request.getProjectExperience().size();
    }

    private String assessCapability(ProposalGenerationRequest request, Requirement req) {
        // Check if we have employees with required skills
        long qualifiedCount = request.getEmployeeData().stream()
            .filter(emp -> emp.getCurrentSkills().stream()
                .anyMatch(skill -> req.getRequiredSkills().contains(skill.getSkillName())))
            .count();
        
        if (qualifiedCount >= 2) return "Strong - Multiple qualified staff";
        if (qualifiedCount == 1) return "Moderate - Single qualified staff";
        return "Developing - Training planned";
    }
}