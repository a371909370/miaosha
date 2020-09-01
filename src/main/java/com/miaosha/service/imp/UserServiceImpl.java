package com.miaosha.service.imp;

import com.alibaba.druid.util.StringUtils;
import com.miaosha.dao.UserDOMapper;
import com.miaosha.dao.UserPasswordDOMapper;
import com.miaosha.entities.UserDO;
import com.miaosha.entities.UserPasswordDO;
import com.miaosha.error.BusinessException;
import com.miaosha.error.CommonError;
import com.miaosha.error.EmBusinessError;
import com.miaosha.service.UserService;
import com.miaosha.service.model.UserModel;
import com.miaosha.validator.ValidationResult;
import com.miaosha.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author sxy
 * @version 1.0
 * @className miaosha
 * @date 2020/8/24
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        //调用UserDOMapper获取到对应的用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }

        //通过用户id获取对应的用户加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO, userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {

        //校验
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("register");
        //事务传播行为：required
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            //实现model->dataobject方法
            UserDO userDO = convertFromModel(userModel);
            //insertSelective相对于insert方法，不会覆盖掉数据库的默认值
            try{
                userDOMapper.insertSelective(userDO);
            }catch (DuplicateKeyException e){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR.setErrMsg("手机号重复"));
            }
            userModel.setId(userDO.getId());
            UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
            //insertSelective会判断每个字段是否为空
            userPasswordDOMapper.insertSelective(userPasswordDO);
        } catch (TransactionException ex) {
            ex.printStackTrace();
            transactionManager.rollback(status);
            throw new BusinessException(EmBusinessError.TRANSACTION_ERROR.setErrMsg("数据库事务异常，注册失败"));
        }
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        //通过用户手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if (userDO == null) {
            throw new BusinessException(EmBusinessError.USER_LOOGIN_FAIL, "用户名密码错误");
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);

        //比对用户信息内加密的密码是否和传输进来的密码相匹配
        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOOGIN_FAIL, "用户名密码错误");
        }

        return userModel;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());

        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);

        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;

    }
}
