package edu.scau.forestproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBriefResponse {
    private Integer id;
    private String username;
    private String phoneNumber;
}
