package edu.scau.forestproject.dto.qa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RagDocumentItem {

    private String id;
    private String title;

    @JsonProperty("created_at")
    private String createdAt;
}
