package com.zdy.service.impl;

import com.zdy.domain.ShoppingCart;
import com.zdy.dao.ShoppingCartDao;
import com.zdy.service.IShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-05
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements IShoppingCartService {

}
