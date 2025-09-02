# proposaljava01
Proposal Writer

- http://localhost:8080/api/proposal/sample-data


## Key Components:

### 1. **ProposalDataController**
- REST controller at `/api/proposal/sample-data`
- Returns complete sample data structure
- Builder methods for each data component

### 2. **Model Classes** (with Lombok @Data annotations)
- **RfpData**: RFP information with requirements and evaluation criteria
- **Employee**: Skills, certifications (current & planned), availability
- **ProjectExperience**: Past projects with success metrics
- **CompanyCapabilities**: Core competencies and industry experience
- **SkillsGapAnalysis**: Missing skills and what-if scenarios
- **ProposalOutputTemplate**: Template configuration

### 3. **Key Features**
- Uses Lombok for automatic getters/setters
- LocalDate for date handling
- Proper package structure under `ai.zactonics.genproposal`
- Focused sample data (2 employees instead of 4 for brevity)

## Usage:
1. Add Lombok dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

2. Start your Spring Boot application
3. Access: `GET http://localhost:8080/api/proposal/sample-data`

The controller returns JSON with all the necessary data for the AI proposal generation system, including the critical skills gap analysis showing:
- Current 72% qualification
- What-if scenarios for training investments
- ROI projections for skills development

This provides a solid foundation for your MVP that can be easily extended with database integration, Azure AI service calls, and actual proposal generation logic.

# A  PromptGeneratorController with a service layer that:

## Key Features:

### 1. **PromptGeneratorController**
- **POST `/api/prompt/generate-proposal`** - Takes the proposal data JSON and generates AI prompts
- **GET `/api/prompt/sample-rfp`** - Returns a sample RFP for testing

### 2. **PromptGeneratorService** 
Generates targeted prompts for each proposal section:
- **Executive Summary** - Highlights qualification percentage and training plans
- **Technical Approach** - Maps capabilities to requirements
- **Team Qualifications** - Features current skills AND planned certifications
- **Past Performance** - Converts projects to relevant case studies
- **Skills Development Plan** - Shows ROI of training investments
- **Cost Proposal** - Includes training costs in budget

### 3. **Smart Analysis Features**
- **Qualification Scoring** - Shows current (72%) vs. potential (95%+) qualification
- **Critical Gap Identification** - Flags high-impact missing skills
- **ROI Recommendations** - Suggests best training investment strategy
- **Capability Assessment** - Evaluates strength for each requirement

### 4. **Sample RFP Included**
The `/api/prompt/sample-rfp` endpoint returns a realistic RFP with:
- Technical requirements matching the skills in your data
- Staffing requirements aligned with team composition
- Evaluation criteria for scoring
- Submission requirements

## How It Works:

1. **Input**: POST the proposal data JSON to `/api/prompt/generate-proposal`

2. **Processing**: The service analyzes:
   - Current qualification (72%)
   - Missing skills impact
   - Training ROI scenarios
   - Team availability

3. **Output**: Structured prompts that can be sent to Azure OpenAI to generate:
   - A complete proposal addressing all requirements
   - Skills development plan showing path to 100% qualification
   - ROI justification for training investments

## Example Usage Flow:

```bash
# 1. Get sample RFP
GET /api/prompt/sample-rfp

# 2. Get proposal data
GET /api/proposal/sample-data

# 3. Generate prompts
POST /api/prompt/generate-proposal
Body: [proposal data from step 2]

# 4. Send prompts to Azure OpenAI for actual content generation
```

## Key Innovation:
The prompts specifically highlight:
- "We're 72% qualified now, but with training we'll be 95% qualified"
- "Training investment of $13,500 qualifies us for $6.5M in additional work"
- Individual employee development paths with timelines

This creates a compelling narrative that turns skill gaps into growth opportunities, demonstrating commitment to meeting requirements while building long-term value.
## Demo Runs

Here's the complete POST request JSON to send to `/api/prompt/generate-proposal`. This is a streamlined version focusing on the key data needed for proposal generation.

## How to Use:

### 1. Using cURL:
```bash
curl -X POST http://localhost:8080/api/prompt/generate-proposal \
  -H "Content-Type: application/json" \
  -d @proposal-request.json
```

### 2. Using Postman:
- Method: `POST`
- URL: `http://localhost:8080/api/prompt/generate-proposal`
- Headers: `Content-Type: application/json`
- Body: Raw JSON (paste the above JSON)

### 3. Expected Response:
```json
{
  "executiveSummaryPrompt": "Generate an executive summary...",
  "technicalApproachPrompt": "Create a detailed technical approach...",
  "teamQualificationsPrompt": "Generate team qualifications...",
  "pastPerformancePrompt": "Create past performance section...",
  "skillsDevelopmentPrompt": "Generate Skills Development Plan...",
  "costProposalPrompt": "Generate cost proposal...",
  "completeProposal": "=== COMPLETE PROPOSAL GENERATION ===...",
  "qualificationScore": {
    "currentScore": 72,
    "scenarioScores": {
      "Quick Win": 80,
      "Strategic Investment": 95,
      "Full Qualification": 100
    },
    "criticalGaps": ["CISSP Certification", "Azure DevOps Expert"]
  },
  "recommendations": [
    "PRIORITY: Implement training plan to reach minimum 80% qualification",
    "CRITICAL: Acquire CISSP Certification (Impact: 10 points)",
    "RECOMMENDED: Pursue 'Strategic Investment' strategy for best ROI"
  ]
}
```

## Key Points in the Request:

1. **Current State**: Shows 72% qualification with 2 employees
2. **Skills Gaps**: Missing CISSP, AZ-400, and ISO 27001 certifications
3. **Training Plans**: Already in progress (AZ-400 at 60%, AZ-500 at 30%)
4. **What-If Scenarios**: Three investment options with ROI projections
5. **Past Performance**: Relevant financial services project worth $5.2M

The response will contain AI prompts that can be sent to Azure OpenAI to generate actual proposal content, highlighting how training investments will improve qualification from 72% to 95% and unlock $6.5M in additional opportunities.

### Sample RFP Response from: api/proposal/sample-data
```
{
  "rfpNumber": "RFP-2025-CLOUD-001",
  "title": "Enterprise Cloud Migration and Modernization Services",
  "issuingOrganization": "Global Financial Corp",
  "submissionDeadline": "October 15, 2025, 5:00 PM EST",
  "background": "Global Financial Corp is seeking a qualified vendor to provide comprehensive cloud migration and modernization services for our enterprise applications. We currently operate 500+ applications across multiple data centers and need to migrate to Microsoft Azure while modernizing our technology stack.",
  "scopeOfWork": [
    "Assessment of current application portfolio and infrastructure",
    "Development of cloud migration strategy and roadmap",
    "Migration of 500+ applications to Azure cloud platform",
    "Implementation of DevOps practices and CI/CD pipelines",
    "Establishment of cloud governance and security frameworks",
    "Implementation of zero-trust security architecture",
    "Training and knowledge transfer to internal teams",
    "24x7 support during transition period"
  ],
  "technicalRequirements": [
    "Minimum 5 years experience in large-scale cloud migrations",
    "Demonstrated expertise in Microsoft Azure services",
    "Experience with containerization (Docker, Kubernetes)",
    "Strong DevOps and automation capabilities",
    "Security certifications (CISSP, Azure Security Engineer)",
    "Experience with financial services regulations and compliance",
    "Ability to maintain 99.99% uptime during migration"
  ],
  "staffingRequirements": {
    "Project Manager": "PMP certified with 10+ years experience",
    "Solution Architects": "2 positions - Azure certified (AZ-305)",
    "Cloud Engineers": "5 positions - Azure DevOps certified",
    "Security Engineers": "2 positions - CISSP or equivalent",
    "Data Engineers": "3 positions - Experience with data migration"
  },
  "evaluationCriteria": {
    "Technical Approach": 30,
    "Team Qualifications": 25,
    "Past Performance": 20,
    "Cost Proposal": 15,
    "Innovation & Value Add": 10
  },
  "submissionRequirements": [
    "Executive Summary (2 pages maximum)",
    "Technical Approach (15 pages maximum)",
    "Team Qualifications and Resumes",
    "Past Performance (3 similar projects)",
    "Cost Proposal (separate sealed envelope)",
    "Skills Development Plan (if applicable)"
  ]
}
```

### Sample Prompt JSON Post

- Curl Test
```
curl -X POST http://localhost:8080/api/prompt/generate-proposal \
  -H "Content-Type: application/json" \
  -d @proposal-request.json
  ```

- Output

```
{
  "rfpData": {
    "rfpId": "RFP-2025-CLOUD-001",
    "title": "Enterprise Cloud Migration and Modernization Services",
    "issuingOrganization": "Global Financial Corp",
    "dueDate": "2025-10-15",
    "contractValue": "$3,500,000",
    "contractDuration": "24 months",
    "extractedRequirements": {
      "mandatoryRequirements": [
        {
          "reqId": "MR-001",
          "category": "technical",
          "description": "Migrate 500+ applications to Azure cloud",
          "requiredSkills": ["Azure Solutions Architect", "Azure DevOps", "Cloud Migration"],
          "requiredCertifications": ["AZ-305", "AZ-400"],
          "minYearsExperience": 5
        },
        {
          "reqId": "MR-002",
          "category": "security",
          "description": "Implement zero-trust security architecture",
          "requiredSkills": ["Cloud Security", "Zero Trust Architecture", "Azure Security"],
          "requiredCertifications": ["CISSP", "Azure Security Engineer AZ-500"],
          "minYearsExperience": 3
        },
        {
          "reqId": "MR-003",
          "category": "compliance",
          "description": "Ensure SOC 2 and ISO 27001 compliance",
          "requiredSkills": ["Compliance Management", "Audit Preparation"],
          "requiredCertifications": ["CISA", "ISO 27001 Lead Auditor"],
          "minYearsExperience": 4
        }
      ],
      "preferredRequirements": [
        {
          "reqId": "PR-001",
          "description": "Experience with financial services industry",
          "requiredSkills": ["Banking Systems", "Financial Regulations"],
          "weight": 15
        }
      ],
      "teamCompositionRequirements": {
        "project_manager": 1,
        "solution_architects": 2,
        "cloud_engineers": 5,
        "security_engineers": 2,
        "data_engineers": 3
      }
    },
    "evaluationCriteria": {
      "technical_approach": 30,
      "team_qualifications": 25,
      "past_performance": 20,
      "price": 15,
      "innovation": 10
    }
  },
  "employeeData": [
    {
      "employeeId": "EMP-001",
      "name": "Sarah Johnson",
      "title": "Senior Cloud Architect",
      "yearsExperience": 8,
      "clearanceLevel": "Secret",
      "availabilityPercentage": 100,
      "hourlyRate": 185.0,
      "currentSkills": [
        {
          "skillName": "Azure Solutions Architecture",
          "proficiencyLevel": "Expert",
          "yearsExperience": 6
        },
        {
          "skillName": "Cloud Migration",
          "proficiencyLevel": "Expert",
          "yearsExperience": 7
        },
        {
          "skillName": "DevOps",
          "proficiencyLevel": "Advanced",
          "yearsExperience": 5
        }
      ],
      "currentCertifications": [
        {
          "certName": "Azure Solutions Architect Expert",
          "certId": "AZ-305",
          "dateObtained": "2023-03-15",
          "expiryDate": "2026-03-15",
          "status": "Active"
        }
      ],
      "plannedCertifications": [
        {
          "certName": "Azure DevOps Engineer Expert",
          "certId": "AZ-400",
          "plannedCompletion": "2025-11-30",
          "trainingStatus": "In Progress",
          "completionPercentage": 60,
          "trainingCost": 2500.0
        }
      ],
      "resumeHighlights": [
        "Led cloud migration for Fortune 500 financial services firm (300+ applications)",
        "Designed multi-region disaster recovery architecture for global retail chain",
        "Reduced infrastructure costs by 40% through cloud optimization"
      ]
    },
    {
      "employeeId": "EMP-002",
      "name": "Michael Chen",
      "title": "Security Engineer",
      "yearsExperience": 5,
      "clearanceLevel": "None",
      "availabilityPercentage": 75,
      "hourlyRate": 165.0,
      "currentSkills": [
        {
          "skillName": "Cloud Security",
          "proficiencyLevel": "Advanced",
          "yearsExperience": 4
        },
        {
          "skillName": "Zero Trust Architecture",
          "proficiencyLevel": "Intermediate",
          "yearsExperience": 2
        }
      ],
      "currentCertifications": [
        {
          "certName": "CompTIA Security+",
          "certId": "SY0-601",
          "dateObtained": "2022-05-10",
          "expiryDate": "2025-05-10",
          "status": "Active"
        }
      ],
      "plannedCertifications": [
        {
          "certName": "CISSP",
          "certId": "CISSP",
          "plannedCompletion": "2025-12-15",
          "trainingStatus": "Not Started",
          "completionPercentage": 0,
          "trainingCost": 5000.0
        },
        {
          "certName": "Azure Security Engineer",
          "certId": "AZ-500",
          "plannedCompletion": "2025-10-01",
          "trainingStatus": "Enrolled",
          "completionPercentage": 30,
          "trainingCost": 3000.0
        }
      ],
      "resumeHighlights": [
        "Implemented zero-trust architecture for healthcare provider",
        "Managed security operations center for 50+ clients"
      ]
    }
  ],
  "projectExperience": [
    {
      "projectId": "PROJ-001",
      "projectName": "National Bank Cloud Transformation",
      "client": "National Bank Corp",
      "industry": "Financial Services",
      "contractValue": "$5,200,000",
      "duration": "18 months",
      "completionDate": "2024-06-30",
      "successMetrics": {
        "onTime": true,
        "onBudget": true,
        "clientSatisfaction": 4.8,
        "costSavingsAchieved": "$3.2M annually"
      },
      "teamMembers": ["EMP-001", "EMP-002"],
      "technologiesUsed": ["Azure", "DevOps", "Kubernetes", "Terraform"],
      "keyAchievements": [
        "Migrated 400+ applications to Azure with zero downtime",
        "Reduced infrastructure costs by 45%",
        "Achieved PCI-DSS compliance"
      ],
      "referenceable": true
    }
  ],
  "companyCapabilities": {
    "coreCompetencies": [
      {
        "capability": "Cloud Migration & Modernization",
        "maturityLevel": "Expert",
        "yearsExperience": 12,
        "successfulProjects": 45,
        "certifiedStaff": 28
      },
      {
        "capability": "DevOps & Automation",
        "maturityLevel": "Advanced",
        "yearsExperience": 8,
        "successfulProjects": 32,
        "certifiedStaff": 18
      },
      {
        "capability": "Cybersecurity",
        "maturityLevel": "Advanced",
        "yearsExperience": 10,
        "successfulProjects": 38,
        "certifiedStaff": 15
      }
    ],
    "industryExperience": [
      {
        "industry": "Financial Services",
        "years": 10,
        "projects": 22,
        "certifications": ["PCI-DSS", "SOX Compliance"]
      }
    ],
    "partnerCertifications": [
      {
        "partner": "Microsoft",
        "level": "Gold Partner",
        "competencies": ["Cloud Platform", "Data Analytics", "Security"]
      }
    ]
  },
  "skillsGapAnalysis": {
    "rfpId": "RFP-2025-CLOUD-001",
    "currentQualificationPercentage": 72,
    "missingSkills": [
      {
        "skill": "Azure DevOps Expert",
        "requiredCount": 2,
        "currentCount": 0,
        "impactOnScore": 8
      },
      {
        "skill": "CISSP Certification",
        "requiredCount": 2,
        "currentCount": 0,
        "impactOnScore": 10
      },
      {
        "skill": "ISO 27001 Lead Auditor",
        "requiredCount": 1,
        "currentCount": 0,
        "impactOnScore": 5
      }
    ],
    "trainingRecommendations": [
      {
        "employeeId": "EMP-001",
        "recommendedCert": "AZ-400",
        "cost": 2500.0,
        "timelineWeeks": 8,
        "roiImprovement": 8
      },
      {
        "employeeId": "EMP-002",
        "recommendedCert": "CISSP",
        "cost": 5000.0,
        "timelineWeeks": 12,
        "roiImprovement": 10
      },
      {
        "employeeId": "EMP-002",
        "recommendedCert": "AZ-500",
        "cost": 3000.0,
        "timelineWeeks": 6,
        "roiImprovement": 7
      }
    ],
    "whatIfScenarios": [
      {
        "scenarioName": "Quick Win",
        "description": "Complete in-progress training only",
        "investment": 5500.0,
        "timelineWeeks": 8,
        "newQualificationPercentage": 80,
        "additionalRfpsQualified": 3,
        "potentialRevenue": "$2,100,000"
      },
      {
        "scenarioName": "Strategic Investment",
        "description": "All recommended training",
        "investment": 13500.0,
        "timelineWeeks": 12,
        "newQualificationPercentage": 95,
        "additionalRfpsQualified": 8,
        "potentialRevenue": "$6,500,000"
      },
      {
        "scenarioName": "Full Qualification",
        "description": "Training + 1 strategic hire",
        "investment": 35000.0,
        "timelineWeeks": 4,
        "newQualificationPercentage": 100,
        "additionalRfpsQualified": 12,
        "potentialRevenue": "$10,200,000"
      }
    ]
  }
}
```

