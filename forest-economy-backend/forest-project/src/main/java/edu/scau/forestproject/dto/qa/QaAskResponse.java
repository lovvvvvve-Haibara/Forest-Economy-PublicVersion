package edu.scau.forestproject.dto.qa;

import lombok.Data;

import java.util.List;

@Data
public class QaAskResponse {

    private String answer;
    private List<QaSourceDto> sources;
}
