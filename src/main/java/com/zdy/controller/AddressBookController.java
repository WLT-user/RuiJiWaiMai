package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zdy.common.BaseContext;
import com.zdy.common.R;
import com.zdy.domain.AddressBook;
import com.zdy.service.IAddressBookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地址管理 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-04
 */
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    /**
     * 获取所有的地址信息
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        //获取当前的用户id
        Long currentId = BaseContext.getCurrentId();

        //根据当前用户进行查询其下所有的地址信息，并根据修改时间降序排序
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(null != currentId,AddressBook::getUserId,currentId)
           .orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(lqw);
        return R.success(list);
    }


    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        //获取当前用户的id
        //为此用户添加新地址
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("新增地址成功");
    }

    /**
     * 查询单个地址，修改地址时，进行数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getOne(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook !=  null){
            return R.success(addressBook);
        }
        return R.error("未找到该地址");
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("地址修改成功");
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> defaultById(@RequestBody AddressBook addressBook){

        //设置该用户下所有的地址均不为默认地址
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId())
           .set(AddressBook::getIsDefault,0);
        //执行修改语句
        addressBookService.update(lqw);

        //将形参设置对应的地址为默认地址
        addressBook.setIsDefault(1);
        //执行修改操作
        addressBookService.updateById(addressBook);
        return R.success("设置默认成功");
    }

    /**
     * 根据id删除地址数据
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> deleteById(Long ids){
        addressBookService.getById(ids);
        return R.success("删除成功");
    }

    /**
     * 获取当前用户的默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //获取当前的用户
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(lqw);
        for (AddressBook addressBook : list) {
            if (addressBook.getIsDefault() == 1){
                return R.success(addressBook);
            }
        }
        return R.error("没有默认地址");
    }
}

