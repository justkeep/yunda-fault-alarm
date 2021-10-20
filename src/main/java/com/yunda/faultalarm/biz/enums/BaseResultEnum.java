package com.yunda.faultalarm.biz.enums;

import javax.servlet.http.HttpServletResponse;

public enum BaseResultEnum {
    /**
     * 位置错误unknown
     */
    UNKNOWN_ERROR(-1,"未知错误"),

    /**
     * 400
     */
    HTTP_REQUEST_BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, "请求参数错误或不完整"),
    /**
     * 401
     */
    HTTP_REQUEST_UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "请先进行认证"),
    /**
     * 403
     */
    HTTP_REQUEST_FORBIDDEN(HttpServletResponse.SC_FORBIDDEN, "无权查看"),
    /**
     * 404
     */
    HTTP_REQUEST_NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "未找到该路径"),
    /**
     * 405
     */
    HTTP_REQUEST_METHOD_NOT_ALLOWED(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "请求方式不支持"),
    /**
     * 406
     */
    HTTP_REQUEST_NOT_ACCEPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE, "不可接受该请求"),
    /**
     * 411
     */
    HTTP_REQUEST_LENGTH_REQUIRED(HttpServletResponse.SC_LENGTH_REQUIRED, "长度受限制"),
    /**
     * 415
     */
    HTTP_REQUEST_UNSUPPORTED_MEDIA_TYPE(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "不支持的媒体类型"),
    /**
     * 416
     */
    HTTP_REQUEST_REQUESTED_RANGE_NOT_SATISFIABLE(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "不能满足请求的范围"),
    /**
     * 500
     */
    HTTP_REQUEST_INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器正在升级，请耐心等待"),
    /**
     * 503
     */
    HTTP_REQUEST_SERVICE_UNAVAILABLE(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "请求超时"),
    /**
     * 演示系统，无法该操作
     */
    HTTP_REQUEST_DEMO_SYSTEM_CANNOT_DO(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "系统暂时异常，请稍后再试"),



    SUCCESS(0,"成功");

    private Integer code;
    private String msg;
    BaseResultEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
