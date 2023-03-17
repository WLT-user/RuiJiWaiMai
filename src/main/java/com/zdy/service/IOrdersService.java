package com.zdy.service;

import com.zdy.domain.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-05
 */
public interface IOrdersService extends IService<Orders> {

    void submit(Orders orders);
}
