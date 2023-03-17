package com.zdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zdy.common.CustomException;
import com.zdy.domain.Setmeal;
import com.zdy.dao.SetmealDao;
import com.zdy.domain.SetmealDish;
import com.zdy.dto.SetmealDto;
import com.zdy.service.ISetmealDishService;
import com.zdy.service.ISetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService setmealDishService;

    /**
     * 保存新增的套餐，即里面的菜品
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //新增套餐
        this.save(setmealDto);
        //获取新增套餐的id
        Long id = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //为新增套餐中的每个菜品设置对应的setmeal_id
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id.toString());
            return item;
        }).collect(Collectors.toList());

        //保存SetmealDishList中所有的数据
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 查询详情，修改操作数据回显
     * @param id
     */
    @Override
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        //创建SetmealDto对象
        SetmealDto setmealDto = new SetmealDto();
        //进行对象的拷贝
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lqw);

        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        //获取setmealDishes
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //删除setmeal_dish中存储的对应的数据
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getDishId,setmealDto.getId());
        setmealDishService.remove(lqw);

        //保存新的数据，因为逻辑删除，要设置新的id值
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setId(IdWorker.getId());
            item.setSetmealId(setmealDto.getId().toString());
            return item;
        }).collect(Collectors.toList());

        //保存数据
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除setmeal和setmeal_dish 中的数据
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithDish(Long[] ids) {
        //1.判断是否可以删除，状态全部为 0：禁用，可以删除
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids)
           .eq(Setmeal::getStatus,1);
        int count = this.count(lqw);

        if (count > 0){
            //不能删除，抛出一样
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //2.先删除setmeal中的数据
        this.removeByIds(Arrays.asList(ids));
        //3.再删除关系表setmeal_dish中的数据
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(qw);
    }
}
