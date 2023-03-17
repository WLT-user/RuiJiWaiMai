package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdy.common.R;
import com.zdy.domain.Category;
import com.zdy.domain.Dish;
import com.zdy.domain.Setmeal;
import com.zdy.domain.SetmealDish;
import com.zdy.dto.DishDto;
import com.zdy.dto.SetmealDto;
import com.zdy.service.ICategoryService;
import com.zdy.service.IDishService;
import com.zdy.service.ISetmealDishService;
import com.zdy.service.ISetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private ISetmealService setmealService;

    @Autowired
    private ISetmealDishService setmealDishService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IDishService dishService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        lqw.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage, lqw);

        //进行拷贝数据
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        //获取setmealPage中的records数据
        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> dtoList = records.stream().map((item) -> {

            //创建SetmealDto对象
            SetmealDto setmealDto = new SetmealDto();
            //拷贝records中的数据
            BeanUtils.copyProperties(item, setmealDto);
            //根据id查询套餐分类名称
            Long categoryId = item.getCategoryId();
            if (categoryId != null) {
                Category category = categoryService.getById(categoryId);
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(dtoList);
        return R.success(setmealDtoPage);
    }


    /**
     * 删除操作
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> deleteByIds(Long[] ids) {
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 批量修改状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatusByIds(@PathVariable int status, Long[] ids) {

        for (Long id : ids) {
            //根据id进行查询修改操作
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改状态成功");
    }

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping()
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 查询详情，修改操作数据回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐数据
     *
     * @param setmealDto
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功");
    }

    /**
     * 查询套餐列表
     * @param setmeal
     * @return*/
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_'+ #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId())
           .eq(Setmeal::getStatus,1);
        List<Setmeal> list = setmealService.list(lqw);
        return R.success(list);
    }

    /**
     * 获取当前套餐下所有的菜品
     * @return
     */
    //此处路径id为setmeal的id
    @GetMapping("/dish/{id}")
    public R<List<Dish>> dishList(@PathVariable Long id){
        //根据id进行查找菜品
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lqw);

        List<Dish> dishList = list.stream().map((item) -> {
            Dish dish = new Dish();
            //进行数据的拷贝
            Dish d = dishService.getById(item.getDishId());
            BeanUtils.copyProperties(d, dish);

            return d;

        }).collect(Collectors.toList());
        return R.success(dishList);
    }
}

