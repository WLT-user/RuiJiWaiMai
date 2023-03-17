package com.zdy.dto;

import com.zdy.domain.OrderDetail;
import com.zdy.domain.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
