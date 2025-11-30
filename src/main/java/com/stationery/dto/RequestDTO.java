package com.stationery.dto;

import lombok.Data;
import java.util.List;

@Data
public class RequestDTO {
    private Long userId;
    private String superiorEmail;
    private List<RequestItemDTO> items;
}