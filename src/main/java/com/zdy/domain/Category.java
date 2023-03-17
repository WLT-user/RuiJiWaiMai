package com.zdy.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 菜品及套餐分类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 类型   1 菜品分类 2 套餐分类
     */
    private Integer type;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)        //插入时填充字段
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)     //插入和更新时填充字段
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)    //插入时填充字段
    private Long createUser;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)      //插入和更新时填充字段
    private Long updateUser;


    /**
     * 是否删除 进行逻辑删除
     */
    @TableLogic(value = "0",delval = "1")
    private Integer isDeleted;
}
