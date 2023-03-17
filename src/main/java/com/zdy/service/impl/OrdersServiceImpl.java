package com.zdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zdy.common.BaseContext;
import com.zdy.common.CustomException;
import com.zdy.domain.*;
import com.zdy.dao.OrdersDao;
import com.zdy.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-05
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders> implements IOrdersService {

    @Autowired
    private IShoppingCartService shoppingCartService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IAddressBookService addressBookService;


    /**
     * 向orders、orders_detail表中添加订单数据
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //1.获取当前用用户的id
        Long currentId = BaseContext.getCurrentId();
        //2.查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        //判断购物车是否为空
        if (list == null || list.size() == 0){
            throw new CustomException("当前购物车为空，不能支付");
        }

        //3.向订单表中插入数据---------一条数据
        //获取用户信息
        User user = userService.getById(currentId);
        //获取地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        //判断地址是否为空
        if (addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //用AtomicInteger类型，对于处理多线程、高并发的数据更安全，计算更准确
        AtomicInteger amount = new AtomicInteger(0);    //金钱数
        long orderId = IdWorker.getId();                           //订单号

        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            //进行数据的拷贝
            BeanUtils.copyProperties(item,orderDetail);
            //BigDecimal类型更准确一些
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //进行orders数据的设置
        //进行数据的拷贝
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
//        orders.setUserName(user.getName());
        orders.setUserName(addressBook.getConsignee());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);
        //4.向订单明细表中插入数据--------多条数据
        orderDetailService.saveBatch(orderDetails);

        //5.清空购物车
        shoppingCartService.remove(lqw);
    }
}
