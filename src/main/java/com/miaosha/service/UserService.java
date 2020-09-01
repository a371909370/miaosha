package com.miaosha.service;

import com.miaosha.error.BusinessException;
import com.miaosha.service.model.UserModel;

/**
 * @author sxy
 * @version 1.0
 * @className miaosha
 * @date 2020/8/24
 */
public interface UserService {

    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    UserModel validateLogin(String telphone, String encodeByMd5rptPassword) throws BusinessException;
}
