package com.zdy.filter;

import com.alibaba.fastjson.JSON;
import com.zdy.common.BaseContext;
import com.zdy.common.R;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录过滤器
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
//    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //转换参数类型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //需要放行的url
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/",
                "/front/",
                "user/sendMsg",   //移动端发送短信
                "user/login",       //移动端登录
                "/doc.html",
                "/webjars/",
                "/swagger-resources",
                "/v2/api-docs"
        };

        //1.获取本次请求的url
        String url = request.getRequestURL().toString();

        //2.判断请求是否需要拦截处理
        boolean check = check(url, urls);

        //3.不需要处理，直接放行
        if (check){
            //放行
            filterChain.doFilter(request,response);
            return;
        }

        //4-1.判断员工是否登录状态，如果已登录，直接放行
        //根据session判断是否登录
        if (request.getSession().getAttribute("employee") != null ){

//            long id = Thread.currentThread().getId();
//            log.info("线程的id为：{}",id);

            long id = (long) request.getSession().getAttribute("employee");
            //将id加入到threadLocal中
            BaseContext.setCurrentId(id);


            filterChain.doFilter(request,response);
            return;
        }

        //4-2.判断用户是否登录状态，如果已登录，直接放行
        //根据session判断是否登录
        if (request.getSession().getAttribute("user") != null ){

//            long id = Thread.currentThread().getId();
//            log.info("线程的id为：{}",id);

            long id = (long) request.getSession().getAttribute("user");
            //将id加入到threadLocal中
            BaseContext.setCurrentId(id);


            filterChain.doFilter(request,response);
            return;
        }

        //5.如果未登录，则返回未登录结果
        //通过响应输出流进行写入数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String request,String[] urls){
        //遍历数组
        for (String url : urls) {
            boolean contains = request.contains(url);
            if (contains){
                return true;
            }
        }
        return false;
    }

}
