package ai.zactonics.genproposal.controller;

import ai.zactonics.genproposal.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/proposal")
public class ProposalDataController {

    @GetMapping("/sample-data")
    public ProposalDataResponse getSampleProposalData() {
        ProposalDataResponse response = new ProposalDataResponse();
        
        // Build RFP Data
        response.setRfpData(buildRfpData());
        
        // Build Employee Data
        response.setEmployeeData(buildEmployeeData());
        
        // Build Project Experience
        response.setProjectExperience(buildProjectExperience());
        
        // Build Company Capabilities
        response.setCompanyCapabilities(buildCompanyCapabilities());
        
        // Build Skills Gap Analysis
        response.setSkillsGapAnalysis(buildSkillsGapAnalysis());
        
        // Build Proposal Output Template
        response.setProposalOutputTemplate(buildProposalOutputTemplate());
        
        return response;
    }
    
    private RfpData buildRfpData() {
        RfpData rfp = new RfpData();
        rfp.setRfpId("RFP-2025-CLOUD-001");
        rfp.setTitle("Enterprise Cloud Migration and Modernization Services");
        rfp.setIssuingOrganization("Global Financial Corp");
        rfp.setDueDate(LocalDate.of(2025, 10, 15));
        rfp.setContractValue("$3,500,000");
        rfp.setContractDuration("24 months");
        
        ExtractedRequirements requirements = new ExtractedRequirements();
        
        // Mandatory Requirements
        List<Requirement> mandatoryReqs = new ArrayList<>();
        
        Requirement req1 = new Requirement();
        req1.setReqId("MR-001");
        req1.setCategory("technical");
        req1.setDescription("Migrate 500+ applications to Azure cloud");
        req1.setRequiredSkills(Arrays.asList("Azure Solutions Architect", "Azure DevOps", "Cloud Migration"));
        req1.setRequiredCertifications(Arrays.asList("AZ-305", "AZ-400"));
        req1.setMinYearsExperience(5);
        mandatoryReqs.add(req1);
        
        Requirement req2 = new Requirement();
        req2.setReqId("MR-002");
        req2.setCategory("security");
        req2.setDescription("Implement zero-trust security architecture");
        req2.setRequiredSkills(Arrays.asList("Cloud Security", "Zero Trust Architecture", "Azure Security"));
        req2.setRequiredCertifications(Arrays.asList("CISSP", "Azure Security Engineer AZ-500"));
        req2.setMinYearsExperience(3);
        mandatoryReqs.add(req2);
        
        requirements.setMandatoryRequirements(mandatoryReqs);
        
        // Team Composition
        Map<String, Integer> teamComp = new HashMap<>();
        teamComp.put("project_manager", 1);
        teamComp.put("solution_architects", 2);
        teamComp.put("cloud_engineers", 5);
        teamComp.put("security_engineers", 2);
        teamComp.put("data_engineers", 3);
        requirements.setTeamCompositionRequirements(teamComp);
        
        rfp.setExtractedRequirements(requirements);
        
        // Evaluation Criteria
        Map<String, Integer> evalCriteria = new HashMap<>();
        evalCriteria.put("technical_approach", 30);
        evalCriteria.put("team_qualifications", 25);
        evalCriteria.put("past_performance", 20);
        evalCriteria.put("price", 15);
        evalCriteria.put("innovation", 10);
        rfp.setEvaluationCriteria(evalCriteria);
        
        return rfp;
    }
    
    private List<Employee> buildEmployeeData() {
        List<Employee> employees = new ArrayList<>();
        
        // Employee 1 - Sarah Johnson
        Employee emp1 = new Employee();
        emp1.setEmployeeId("EMP-001");
        emp1.setName("Sarah Johnson");
        emp1.setTitle("Senior Cloud Architect");
        emp1.setYearsExperience(8);
        emp1.setClearanceLevel("Secret");
        emp1.setAvailabilityPercentage(100);
        emp1.setHourlyRate(185.0);
        
        List<Skill> skills1 = new ArrayList<>();
        skills1.add(new Skill("Azure Solutions Architecture", "Expert", 6));
        skills1.add(new Skill("AWS Architecture", "Intermediate", 3));
        skills1.add(new Skill("Cloud Migration", "Expert", 7));
        skills1.add(new Skill("DevOps", "Advanced", 5));
        emp1.setCurrentSkills(skills1);
        
        List<Certification> certs1 = new ArrayList<>();
        Certification cert1 = new Certification();
        cert1.setCertName("Azure Solutions Architect Expert");
        cert1.setCertId("AZ-305");
        cert1.setDateObtained(LocalDate.of(2023, 3, 15));
        cert1.setExpiryDate(LocalDate.of(2026, 3, 15));
        cert1.setStatus("Active");
        certs1.add(cert1);
        emp1.setCurrentCertifications(certs1);
        
        List<PlannedCertification> plannedCerts1 = new ArrayList<>();
        PlannedCertification planned1 = new PlannedCertification();
        planned1.setCertName("Azure DevOps Engineer Expert");
        planned1.setCertId("AZ-400");
        planned1.setPlannedCompletion(LocalDate.of(2025, 11, 30));
        planned1.setTrainingStatus("In Progress");
        planned1.setCompletionPercentage(60);
        planned1.setTrainingCost(2500.0);
        plannedCerts1.add(planned1);
        emp1.setPlannedCertifications(plannedCerts1);
        
        emp1.setResumeHighlights(Arrays.asList(
            "Led cloud migration for Fortune 500 financial services firm (300+ applications)",
            "Designed multi-region disaster recovery architecture for global retail chain",
            "Reduced infrastructure costs by 40% through cloud optimization"
        ));
        
        employees.add(emp1);
        
        // Employee 2 - Michael Chen
        Employee emp2 = new Employee();
        emp2.setEmployeeId("EMP-002");
        emp2.setName("Michael Chen");
        emp2.setTitle("Security Engineer");
        emp2.setYearsExperience(5);
        emp2.setClearanceLevel("None");
        emp2.setAvailabilityPercentage(75);
        emp2.setHourlyRate(165.0);
        
        List<Skill> skills2 = new ArrayList<>();
        skills2.add(new Skill("Cloud Security", "Advanced", 4));
        skills2.add(new Skill("Zero Trust Architecture", "Intermediate", 2));
        skills2.add(new Skill("Network Security", "Expert", 5));
        emp2.setCurrentSkills(skills2);
        
        List<PlannedCertification> plannedCerts2 = new ArrayList<>();
        PlannedCertification planned2 = new PlannedCertification();
        planned2.setCertName("CISSP");
        planned2.setCertId("CISSP");
        planned2.setPlannedCompletion(LocalDate.of(2025, 12, 15));
        planned2.setTrainingStatus("Not Started");
        planned2.setCompletionPercentage(0);
        planned2.setTrainingCost(5000.0);
        plannedCerts2.add(planned2);
        emp2.setPlannedCertifications(plannedCerts2);
        
        employees.add(emp2);
        
        return employees;
    }
    
    private List<ProjectExperience> buildProjectExperience() {
        List<ProjectExperience> projects = new ArrayList<>();
        
        ProjectExperience proj1 = new ProjectExperience();
        proj1.setProjectId("PROJ-001");
        proj1.setProjectName("National Bank Cloud Transformation");
        proj1.setClient("National Bank Corp");
        proj1.setIndustry("Financial Services");
        proj1.setContractValue("$5,200,000");
        proj1.setDuration("18 months");
        proj1.setCompletionDate(LocalDate.of(2024, 6, 30));
        
        SuccessMetrics metrics = new SuccessMetrics();
        metrics.setOnTime(true);
        metrics.setOnBudget(true);
        metrics.setClientSatisfaction(4.8);
        metrics.setCostSavingsAchieved("$3.2M annually");
        proj1.setSuccessMetrics(metrics);
        
        proj1.setTeamMembers(Arrays.asList("EMP-001", "EMP-002", "EMP-003"));
        proj1.setTechnologiesUsed(Arrays.asList("Azure", "DevOps", "Kubernetes", "Terraform"));
        proj1.setKeyAchievements(Arrays.asList(
            "Migrated 400+ applications to Azure with zero downtime",
            "Reduced infrastructure costs by 45%",
            "Improved application performance by 60%",
            "Achieved PCI-DSS compliance"
        ));
        proj1.setReferenceable(true);
        
        projects.add(proj1);
        
        return projects;
    }
    
    private CompanyCapabilities buildCompanyCapabilities() {
        CompanyCapabilities capabilities = new CompanyCapabilities();
        
        List<CoreCompetency> competencies = new ArrayList<>();
        CoreCompetency comp1 = new CoreCompetency();
        comp1.setCapability("Cloud Migration & Modernization");
        comp1.setMaturityLevel("Expert");
        comp1.setYearsExperience(12);
        comp1.setSuccessfulProjects(45);
        comp1.setCertifiedStaff(28);
        competencies.add(comp1);
        
        CoreCompetency comp2 = new CoreCompetency();
        comp2.setCapability("DevOps & Automation");
        comp2.setMaturityLevel("Advanced");
        comp2.setYearsExperience(8);
        comp2.setSuccessfulProjects(32);
        comp2.setCertifiedStaff(18);
        competencies.add(comp2);
        
        capabilities.setCoreCompetencies(competencies);
        
        List<IndustryExperience> industries = new ArrayList<>();
        IndustryExperience ind1 = new IndustryExperience();
        ind1.setIndustry("Financial Services");
        ind1.setYears(10);
        ind1.setProjects(22);
        ind1.setCertifications(Arrays.asList("PCI-DSS", "SOX Compliance"));
        industries.add(ind1);
        
        capabilities.setIndustryExperience(industries);
        
        return capabilities;
    }
    
    private SkillsGapAnalysis buildSkillsGapAnalysis() {
        SkillsGapAnalysis analysis = new SkillsGapAnalysis();
        analysis.setRfpId("RFP-2025-CLOUD-001");
        analysis.setCurrentQualificationPercentage(72);
        
        List<MissingSkill> missingSkills = new ArrayList<>();
        MissingSkill skill1 = new MissingSkill();
        skill1.setSkill("Azure DevOps Expert");
        skill1.setRequiredCount(2);
        skill1.setCurrentCount(0);
        skill1.setImpactOnScore(8);
        missingSkills.add(skill1);
        
        MissingSkill skill2 = new MissingSkill();
        skill2.setSkill("CISSP Certification");
        skill2.setRequiredCount(2);
        skill2.setCurrentCount(0);
        skill2.setImpactOnScore(10);
        missingSkills.add(skill2);
        
        analysis.setMissingSkills(missingSkills);
        
        List<TrainingRecommendation> recommendations = new ArrayList<>();
        TrainingRecommendation rec1 = new TrainingRecommendation();
        rec1.setEmployeeId("EMP-001");
        rec1.setRecommendedCert("AZ-400");
        rec1.setCost(2500.0);
        rec1.setTimelineWeeks(8);
        rec1.setRoiImprovement(8);
        recommendations.add(rec1);
        
        analysis.setTrainingRecommendations(recommendations);
        
        List<WhatIfScenario> scenarios = new ArrayList<>();
        WhatIfScenario scenario1 = new WhatIfScenario();
        scenario1.setScenarioName("Quick Win");
        scenario1.setDescription("Complete in-progress training only");
        scenario1.setInvestment(5500.0);
        scenario1.setTimelineWeeks(8);
        scenario1.setNewQualificationPercentage(80);
        scenario1.setAdditionalRfpsQualified(3);
        scenario1.setPotentialRevenue("$2,100,000");
        scenarios.add(scenario1);
        
        WhatIfScenario scenario2 = new WhatIfScenario();
        scenario2.setScenarioName("Strategic Investment");
        scenario2.setDescription("All recommended training");
        scenario2.setInvestment(13500.0);
        scenario2.setTimelineWeeks(12);
        scenario2.setNewQualificationPercentage(95);
        scenario2.setAdditionalRfpsQualified(8);
        scenario2.setPotentialRevenue("$6,500,000");
        scenarios.add(scenario2);
        
        analysis.setWhatIfScenarios(scenarios);
        
        return analysis;
    }
    
    private ProposalOutputTemplate buildProposalOutputTemplate() {
        ProposalOutputTemplate template = new ProposalOutputTemplate();
        
        List<ProposalSection> sections = new ArrayList<>();
        
        ProposalSection section1 = new ProposalSection();
        section1.setSection("Executive Summary");
        section1.setAutoGenerate(true);
        section1.setIncludeQualificationPercentage(true);
        sections.add(section1);
        
        ProposalSection section2 = new ProposalSection();
        section2.setSection("Technical Approach");
        section2.setAutoGenerate(true);
        section2.setMapToRequirements(true);
        sections.add(section2);
        
        ProposalSection section3 = new ProposalSection();
        section3.setSection("Team Qualifications");
        section3.setIncludeResumes(true);
        section3.setIncludeCerts(true);
        section3.setIncludeTrainingPlan(true);
        sections.add(section3);
        
        ProposalSection section4 = new ProposalSection();
        section4.setSection("Skills Development Plan");
        section4.setIncludeTimeline(true);
        section4.setIncludeInvestment(true);
        section4.setShowImprovedQualification(true);
        sections.add(section4);
        
        template.setSections(sections);
        
        return template;
    }
}