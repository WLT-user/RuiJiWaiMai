package com.zdy.service.impl;

import com.zdy.domain.OrderDetail;
import com.zdy.dao.OrderDetailDao;
import com.zdy.service.IOrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-05
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements IOrderDetailService {

}
