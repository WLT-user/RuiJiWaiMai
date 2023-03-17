package com.zdy.dao;

import com.zdy.domain.AddressBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 地址管理 Mapper 接口
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-04
 */
@Mapper
public interface AddressBookDao extends BaseMapper<AddressBook> {

}
