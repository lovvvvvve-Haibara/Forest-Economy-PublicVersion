package edu.scau.forestproject.dto.qa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RagQaRequest {

    private String question;

    @JsonProperty("top_k")
    private Integer topK;
}
