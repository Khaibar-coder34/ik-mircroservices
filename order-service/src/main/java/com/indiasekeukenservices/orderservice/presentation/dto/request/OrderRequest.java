package com.indiasekeukenservices.orderservice.presentation.dto.request;

import com.indiasekeukenservices.orderservice.presentation.dto.OrderLineItemsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private List<OrderLineItemsDto> orderLineItemsList;
}
