package com.tianrun.redpacket.common.exception;

import lombok.Data;

/**
 * Created by dell on 2018/9/6.
 * @author dell
 */
@Data
public class BusinessException extends Exception {

    private static final long serialVersionUID = 445445701327085003L;

    private String status;

    public BusinessException(){
        this("1", "服务器内部错误");
    }

    public BusinessException(String message){
        this("1", message);
    }

    public BusinessException(String code, String message){
        super(message);
        status = code;
    }
}
