package com.zdy.service;

import com.zdy.domain.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdy.dto.SetmealDto;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
public interface ISetmealService extends IService<Setmeal> {

    //保存新增的套餐，即里面的菜品
    void saveWithDish(SetmealDto setmealDto);

    //查询详情，修改操作数据回显
    SetmealDto getByIdWithDish(Long id);

    //修改套餐数据
    void updateWithDish(SetmealDto setmealDto);

    //删除setmeal和setmeal_dish 中的数据
    void deleteWithDish(Long[] ids);
}
