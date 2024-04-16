package com.indiasekeukenservices.kitchenservice.presentation.dto;

import com.indiasekeukenservices.kitchenservice.domain.PreparationStatus;
import lombok.Data;

@Data
public class PreparationStatusUpdateDto {
    private PreparationStatus status;

}
