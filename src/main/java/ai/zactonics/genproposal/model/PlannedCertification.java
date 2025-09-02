package ai.zactonics.genproposal.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PlannedCertification {
    private String certName;
    private String certId;
    private LocalDate plannedCompletion;
    private String trainingStatus;
    private Integer completionPercentage;
    private Double trainingCost;
}
