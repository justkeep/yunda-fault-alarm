package com.yunda.faultalarm.biz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunda.faultalarm.biz.enums.BaseResultEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gyk
 */
@Data
public class YundaFaultResponse implements Serializable {
    private static long serialVersionUID=1L;

    /**
     * 时间戳
     */
    @JsonProperty("Time")
    private long time=System.currentTimeMillis();

    /**
     * 状态吗非0位错误码
     */
    @JsonProperty("Code")
    private int code;

    /**
     * 描述信息
     */
    @JsonProperty("msg")
    private String msg;

    /**
     * 数据
     */
    @JsonProperty("Data")
    private Boolean data;



    public YundaFaultResponse(int code , String msg){

        this.code = code;
        this.msg = msg;
    }
    public YundaFaultResponse(){
        super();
    }

    public YundaFaultResponse(BaseResultEnum enumInterface){
        this.code = enumInterface.getCode();
        this.msg = enumInterface.getMsg();
        this.data = null;
    }

    public YundaFaultResponse(int code , String msg, Boolean date){
        this.code = code;
        this.msg = msg;
        this.data=date;
    }

    /**
     * 创建成功
     * @param t
     * @return
     */
    public static  YundaFaultResponse buildSuccess(Boolean t){
        YundaFaultResponse result = new YundaFaultResponse();
        result.setCode(BaseResultEnum.SUCCESS.getCode());
        result.setMsg(BaseResultEnum.SUCCESS.getMsg());
        result.setData(t);
        return result;
    }
    /**
     * 无object返回
     * @return
     */
    public  static YundaFaultResponse buildSuccess(){

        return buildSuccess(null);
    }
    /**
     * http回调错误
     * @param code
     * @param msg
     * @return
     */
    public static YundaFaultResponse buildFail(Integer code, String msg){
        YundaFaultResponse result = new YundaFaultResponse();
        result.setCode(code);
        result.setMsg(msg);
        return  result;
    }
    public static YundaFaultResponse buildFail(){
        YundaFaultResponse result = new YundaFaultResponse();
        result.setCode(BaseResultEnum.UNKNOWN_ERROR.getCode());
        result.setMsg(BaseResultEnum.UNKNOWN_ERROR.getMsg());
        return  result;
    }
}
