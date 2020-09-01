package com.miaosha.error;

/**
 * @author sxy
 * @version 1.0
 * @className miaosha
 * @date 2020/8/24
 */
public enum EmBusinessError implements CommonError{
    //通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),

    //通用错误类型10002
    UNKNOWN_ERROR(10002, "未知错误"),

    //通用错误类型10003
    PATH_ERROR(10002, "访问路径错误"),

    //通用错误类型10003
    TRANSACTION_ERROR(10002, "事务执行错误"),

    USER_LOOGIN_FAIL(10003, "登陆失败"),

    //10000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001, "用户不存在"),

    STOCK_NOT_ENOUGH(20002, "库存不足"),

    USER_NOT_LOGIN(20003, "用户未注册")

    ;

    private int errCode;
    private String errMsg;

    private EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
