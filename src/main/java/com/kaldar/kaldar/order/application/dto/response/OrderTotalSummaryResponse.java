package com.kaldar.kaldar.order.application.dto.response;

import com.kaldar.kaldar.order.application.dto.request.OrderItemsDTO;

import java.math.BigDecimal;
import java.util.List;

public class OrderTotalSummaryResponse {
    private List<OrderItemsDTO> orderItems;
    private BigDecimal total;

    public List<OrderItemsDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemsDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
