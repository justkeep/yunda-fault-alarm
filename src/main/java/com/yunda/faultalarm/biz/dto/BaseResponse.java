package com.yunda.faultalarm.biz.dto;

import com.yunda.faultalarm.biz.enums.BaseResultEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gyk
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static long serialVersionUID=1L;

    /**
     * 时间戳
     */
    private long time=System.currentTimeMillis();

    /**
     * 状态吗非0位错误码
     */
    private int code;

    /**
     * 描述信息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;



    public BaseResponse(int code ,String msg){

        this.code = code;
        this.msg = msg;
    }
    public BaseResponse(){
        super();
    }

    public BaseResponse(BaseResultEnum enumInterface){
        this.code = enumInterface.getCode();
        this.msg = enumInterface.getMsg();
        this.data = null;
    }

    public BaseResponse(int code ,String msg,T date){
        this.code = code;
        this.msg = msg;
        this.data=date;
    }

    /**
     * 创建成功
     * @param t
     * @return
     */
    public static <T>  BaseResponse buildSuccess(T t){
        BaseResponse result = new BaseResponse();
        result.setCode(BaseResultEnum.SUCCESS.getCode());
        result.setMsg(BaseResultEnum.SUCCESS.getMsg());
        result.setData(t);
        return result;
    }
    /**
     * 无object返回
     * @return
     */
    public  static BaseResponse buildSuccess(){

        return buildSuccess(null);
    }
    /**
     * http回调错误
     * @param code
     * @param msg
     * @return
     */
    public static BaseResponse buildFail(Integer code, String msg){
        BaseResponse result = new BaseResponse();
        result.setCode(code);
        result.setMsg(msg);
        return  result;
    }
    public static BaseResponse buildFail(){
        BaseResponse result = new BaseResponse();
        result.setCode(BaseResultEnum.UNKNOWN_ERROR.getCode());
        result.setMsg(BaseResultEnum.UNKNOWN_ERROR.getMsg());
        return  result;
    }
}
