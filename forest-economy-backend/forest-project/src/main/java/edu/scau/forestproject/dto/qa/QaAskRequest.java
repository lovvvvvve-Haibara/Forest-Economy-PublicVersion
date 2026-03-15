package edu.scau.forestproject.dto.qa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QaAskRequest {

    @NotBlank(message = "question cannot be blank")
    @Size(max = 2000)
    private String question;

    private Integer topK;
}
