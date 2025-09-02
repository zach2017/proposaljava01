package ai.zactonics.genproposal.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Certification {
    private String certName;
    private String certId;
    private LocalDate dateObtained;
    private LocalDate expiryDate;
    private String status;
}
