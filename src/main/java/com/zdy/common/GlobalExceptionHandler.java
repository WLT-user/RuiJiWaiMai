package com.zdy.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    //sql完整性限制违反异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){

        log.info(ex.getMessage());

        //提示用户名重复信息
        if (ex.getMessage().contains("Duplicate entry")){
            //按空格进行分割，进行查看是否为该错误
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+ "已存在";
            return R.error(msg);
        }

        return R.error("未知错误！");
    }


    /**
     * 自定义业务异常处理方法
     * @param ex
     * @return
     */
    //sql完整性限制违反异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){

        log.info(ex.getMessage());

        return R.error(ex.getMessage());
    }


}
