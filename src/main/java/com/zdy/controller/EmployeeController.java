package com.zdy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdy.common.R;
import com.zdy.domain.Employee;
import com.zdy.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;


/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author 迷糊小丸子
 * @since 2022-05-28
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    /**
     * 员工登录
     * @param request  
     * @param employee  用户名和密码
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        
        //2.根据页面提交的用户名进行查询数据库
        //获取数据库中所有的用户名，进行对比
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        //进行查找
        Employee emp = employeeService.getOne(queryWrapper);

        //3.判断是否查询到
        if (emp == null){
            return R.error("登录失败~~~~");
        }

        //4.查询到，进行密码对比
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败~~~~");
        }

        //5.查看员工状态 状态 0:禁用，1:正常
        if (emp.getStatus() == 0){
            return R.error("账号已禁用~~~~");
        }

        //6.登录成功，将员工的id存入到session并返回登陆成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前登录的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){

        //为员工添加初始密码,并进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

//        //为员工设置创建、修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //为员工设置创建、修改人，人为session 中的数据
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);
        return R.success("新增员工成功！");
    }


    /**
     * 员工管理分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        Page<Employee> pageInfo = new Page<>(page,pageSize);

        //进行条件查询，并根据更改时间进行排序
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lqw.orderByDesc(Employee::getUpdateTime);

        //page会自动封装到pageInfo 中，则不必再生成一个变量
        employeeService.page(pageInfo, lqw);

        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping()
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

//        long id = Thread.currentThread().getId();
//        log.info("线程的id为：{}",id);

        //获取修改者的id
        Long emp = (Long) request.getSession().getAttribute("employee");

//        //设置员工的修改时间、修改者
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(emp);

        //执行修改操作
        //根据id 进行修改操作，有则改之，无则不改
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("未找到该员工");
    }

}

