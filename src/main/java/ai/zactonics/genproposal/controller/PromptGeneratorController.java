package ai.zactonics.genproposal.controller;

import ai.zactonics.genproposal.model.*;
import ai.zactonics.genproposal.service.PromptGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/prompt")
public class PromptGeneratorController {

    @Autowired
    private PromptGeneratorService promptService;

    @PostMapping("/generate-proposal")
    public ResponseEntity<ProposalGenerationResponse> generateProposal(
            @RequestBody ProposalGenerationRequest request) {
        
        ProposalGenerationResponse response = new ProposalGenerationResponse();
        
        // Generate prompts for each section
        response.setExecutiveSummaryPrompt(
            promptService.generateExecutiveSummaryPrompt(request));
        
        response.setTechnicalApproachPrompt(
            promptService.generateTechnicalApproachPrompt(request));
        
        response.setTeamQualificationsPrompt(
            promptService.generateTeamQualificationsPrompt(request));
        
        response.setPastPerformancePrompt(
            promptService.generatePastPerformancePrompt(request));
        
        response.setSkillsDevelopmentPrompt(
            promptService.generateSkillsDevelopmentPrompt(request));
        
        response.setCostProposalPrompt(
            promptService.generateCostProposalPrompt(request));
        
        // Generate complete proposal
        response.setCompleteProposal(
            promptService.assembleCompleteProposal(request));
        
        response.setQualificationScore(
            promptService.calculateQualificationScore(request));
        
        response.setRecommendations(
            promptService.generateRecommendations(request));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sample-rfp")
    public ResponseEntity<SampleRfp> getSampleRfp() {
        return ResponseEntity.ok(createSampleRfp());
    }

    private SampleRfp createSampleRfp() {
        SampleRfp rfp = new SampleRfp();
        rfp.setRfpNumber("RFP-2025-CLOUD-001");
        rfp.setTitle("Enterprise Cloud Migration and Modernization Services");
        rfp.setIssuingOrganization("Global Financial Corp");
        rfp.setSubmissionDeadline("October 15, 2025, 5:00 PM EST");
        
        // Background
        rfp.setBackground(
            "Global Financial Corp is seeking a qualified vendor to provide comprehensive " +
            "cloud migration and modernization services for our enterprise applications. " +
            "We currently operate 500+ applications across multiple data centers and need " +
            "to migrate to Microsoft Azure while modernizing our technology stack."
        );
        
        // Scope of Work
        List<String> scopeItems = Arrays.asList(
            "Assessment of current application portfolio and infrastructure",
            "Development of cloud migration strategy and roadmap",
            "Migration of 500+ applications to Azure cloud platform",
            "Implementation of DevOps practices and CI/CD pipelines",
            "Establishment of cloud governance and security frameworks",
            "Implementation of zero-trust security architecture",
            "Training and knowledge transfer to internal teams",
            "24x7 support during transition period"
        );
        rfp.setScopeOfWork(scopeItems);
        
        // Technical Requirements
        List<String> techRequirements = Arrays.asList(
            "Minimum 5 years experience in large-scale cloud migrations",
            "Demonstrated expertise in Microsoft Azure services",
            "Experience with containerization (Docker, Kubernetes)",
            "Strong DevOps and automation capabilities",
            "Security certifications (CISSP, Azure Security Engineer)",
            "Experience with financial services regulations and compliance",
            "Ability to maintain 99.99% uptime during migration"
        );
        rfp.setTechnicalRequirements(techRequirements);
        
        // Staffing Requirements
        Map<String, String> staffingReqs = new LinkedHashMap<>();
        staffingReqs.put("Project Manager", "PMP certified with 10+ years experience");
        staffingReqs.put("Solution Architects", "2 positions - Azure certified (AZ-305)");
        staffingReqs.put("Cloud Engineers", "5 positions - Azure DevOps certified");
        staffingReqs.put("Security Engineers", "2 positions - CISSP or equivalent");
        staffingReqs.put("Data Engineers", "3 positions - Experience with data migration");
        rfp.setStaffingRequirements(staffingReqs);
        
        // Evaluation Criteria
        Map<String, Integer> evalCriteria = new LinkedHashMap<>();
        evalCriteria.put("Technical Approach", 30);
        evalCriteria.put("Team Qualifications", 25);
        evalCriteria.put("Past Performance", 20);
        evalCriteria.put("Cost Proposal", 15);
        evalCriteria.put("Innovation & Value Add", 10);
        rfp.setEvaluationCriteria(evalCriteria);
        
        // Submission Requirements
        List<String> submissionReqs = Arrays.asList(
            "Executive Summary (2 pages maximum)",
            "Technical Approach (15 pages maximum)",
            "Team Qualifications and Resumes",
            "Past Performance (3 similar projects)",
            "Cost Proposal (separate sealed envelope)",
            "Skills Development Plan (if applicable)"
        );
        rfp.setSubmissionRequirements(submissionReqs);
        
        return rfp;
    }
}