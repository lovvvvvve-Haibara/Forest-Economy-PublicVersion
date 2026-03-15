package edu.scau.forestproject.dto.qa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RagIngestResponse {

    @JsonProperty("document_id")
    private String documentId;

    private Integer chunks;
}
