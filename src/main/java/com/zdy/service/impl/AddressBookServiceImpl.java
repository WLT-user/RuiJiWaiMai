package com.zdy.service.impl;

import com.zdy.domain.AddressBook;
import com.zdy.dao.AddressBookDao;
import com.zdy.service.IAddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 地址管理 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-06-04
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements IAddressBookService {

}
