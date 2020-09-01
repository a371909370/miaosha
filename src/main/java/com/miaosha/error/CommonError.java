package com.miaosha.error;

/**
 * @author sxy
 * @version 1.0
 * @className miaosha
 * @date 2020/8/24
 */
public interface CommonError {
    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMs);
}