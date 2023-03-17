package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zdy.common.BaseContext;
import com.zdy.common.R;
import com.zdy.domain.ShoppingCart;
import com.zdy.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-05
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 购物车中添加商品
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据：{}", shoppingCart);

        //1.设置用户的id，指定当前是那个用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //2.查看当前菜品(口味不同，为多个订单)或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        //判断当前订单是菜品，还是套餐
        if (null != dishId) {
            //菜品
            lqw.eq(ShoppingCart::getDishId, dishId);
            //因为前端再次添加，不能再次选择口味，此处，便不必再添加条件
//               .eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        } else {
            //套餐
            lqw.eq(ShoppingCart::getSetmealId, setmealId);
        }
        //3.如果存在，就在原来数据基础上+1
        //4.如果不存在，则添加购物车，数量默认为 1
        ShoppingCart one = shoppingCartService.getOne(lqw);
        if (null == one) {
            //不存在，做新增保存
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
        } else {
            //存在，做修改数据操作
            one.setCreateTime(LocalDateTime.now());
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }

        return R.success(one);
    }

    /**
     * 购物车中减少商品
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        //条件构造器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if (null != dishId) {
            lqw.eq(ShoppingCart::getDishId, dishId);
        } else {
            lqw.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart one = shoppingCartService.getOne(lqw);

        if (one.getNumber() == 1){
            //删除数据库
            shoppingCartService.removeById(one.getId());
        }else{
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }
        return R.success(shoppingCart);
    }


    /**
     * 获取购物车中的数据
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        //获取当前登录用户购物车中所有的订单
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .gt(ShoppingCart::getNumber, 0)
                .orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }

    /**
     * 清空数据
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lqw);
        return R.success("清空购物车");
    }




}

