package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdy.common.R;
import com.zdy.domain.Category;
import com.zdy.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 新增菜品、套餐
     *
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增成功");
    }


    /**
     * 分页查询列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {

        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, lqw);

        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping()
    public R<String> delete(long id) {

        //若菜品或套餐绑定了菜，则不能直接删除
//        categoryService.removeById(id);

        //调用自定一定删除方法
        categoryService.deleteById(id);
        return R.success("删除成功");
    }

    /**
     * 根据id更改菜品、套餐
     *
     * @param category
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody Category category) {

        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据type条件查询
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType() != null,Category::getType,category.getType());
        //进行排序, 按sort升序，按更改时间降序
        lqw.orderByAsc(Category::getSort)
           .orderByDesc(Category::getUpdateTime);

        //执行查询
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }
}

