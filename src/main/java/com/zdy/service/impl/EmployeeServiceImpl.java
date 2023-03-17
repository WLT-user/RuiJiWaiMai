package com.zdy.service.impl;

import com.zdy.domain.Employee;
import com.zdy.dao.EmployeeDao;
import com.zdy.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-28
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements IEmployeeService {

}
