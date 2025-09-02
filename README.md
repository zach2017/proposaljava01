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