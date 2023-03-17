package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdy.common.BaseContext;
import com.zdy.common.R;
import com.zdy.domain.OrderDetail;
import com.zdy.domain.Orders;
import com.zdy.domain.ShoppingCart;
import com.zdy.dto.OrderDto;
import com.zdy.service.IOrderDetailService;
import com.zdy.service.IOrdersService;
import com.zdy.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-05
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> save(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("提交成功");
    }

    /**
     * 移动端分页查询订单
     * @param page
     * @param pageSize
     * @return
     */
    //包含orders和order_detail两张表中的数据
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> orderPage = new Page<>();
        //获取当前用户的id
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.eq(Orders::getUserId,currentId)
          .orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,qw);
        //进行数据拷贝
        BeanUtils.copyProperties(pageInfo,orderPage,"records");

        //根据订单的id,进行查找对应的order_detail中的数据
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> dtoList = records.stream().map((item) -> {

            //创建一个orderDto的对象
            OrderDto orderDto = new OrderDto();
            //拷贝数据
            BeanUtils.copyProperties(item, orderDto);
            //根据Orders的id获取对应的订单详情
            LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();
            lqw.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> list = orderDetailService.list(lqw);

            //orderDto设置数据
            orderDto.setOrderDetails(list);
            return orderDto;
        }).collect(Collectors.toList());
        //对页面的数据经行设置
        orderPage.setRecords(dtoList);
        return R.success(orderPage);
    }

    /**
     * 员工查看订单页面
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime,String endTime){

        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotEmpty(number),Orders::getNumber,number)
           .ge(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
           .le(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        ordersService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    /**
     * 员工修改订单状态
     * @param orders
     * @return
     */
    @PutMapping()
    public R<Orders> updateStatus(@RequestBody Orders orders){

        if (orders.getId() != null){
            ordersService.updateById(orders);
        }
        return R.success(orders);
    }

    /**
     * 再次下单
     * @param orders
     * @return
     */
    //参数：orders 的 id
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){

        //获取当前用户的id
        Long currentId = BaseContext.getCurrentId();

        //根据订单的id获取该订单下的订单细节
        LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> list = orderDetailService.list(lqw);

        //清空购物车中的数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(qw);

        //将所有的菜品放入购物车中，进行再次购买
        List<ShoppingCart> shoppingCartList = list.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            //为购物车设置数据
            //进行数据的拷贝
            BeanUtils.copyProperties(item, shoppingCart);
            shoppingCart.setId(IdWorker.getId());
            shoppingCart.setUserId(currentId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("再次购买成功");
    }
}

