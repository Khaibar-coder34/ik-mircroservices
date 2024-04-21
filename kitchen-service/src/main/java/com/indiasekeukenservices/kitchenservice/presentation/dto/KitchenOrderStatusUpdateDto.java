package com.indiasekeukenservices.kitchenservice.presentation.dto;


import com.indiasekeukenservices.kitchenservice.domain.KitchenOrderStatus;
import lombok.Data;

@Data
public class KitchenOrderStatusUpdateDto {
    private KitchenOrderStatus status;

}
