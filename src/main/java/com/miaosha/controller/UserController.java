package com.miaosha.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaosha.controller.viewobject.UserVO;
import com.miaosha.response.CommonReturnType;
import com.miaosha.service.model.UserModel;
import com.miaosha.service.UserService;
import com.miaosha.error.BusinessException;
import com.miaosha.error.EmBusinessError;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author sxy
 * @version 1.0
 * @className miaosha
 * @date 2020/8/24
 */

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping("/*")
    @ResponseBody
    public CommonReturnType errorPath() throws BusinessException {
        //兜底返回访问路径错误
        throw new BusinessException(EmBusinessError.PATH_ERROR);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO = convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;

    }

    /**
     * 用户登录接口
     * @param telphone
     * @param password
     * @return
     * @throws BusinessException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登录服务，用来校验用户登录是否合法
        //用户加密后的密码
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMd5(password));

//        Cookie cookie = new Cookie("JSESSIONID", this.httpServletRequest.getSession().getId());
//        cookie.setPath("/");
//        cookie.setHttpOnly(false);
//        httpServletResponse.addCookie(cookie);

        //将登陆凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);

    }

    /**
     * 用户获取otp短信接口
     * @param telphone
     * @return
     */
    @PostMapping(value="/getotp", consumes=CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP验证码同对应用户的手机号关联，使用httpsession的方式绑定手机号与OTPCDOE
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        //将OTP验证码通过短信通道发送给用户，省略
        System.out.println("telphone=" + telphone + "&otpCode=" + otpCode);

        return CommonReturnType.create(null);
    }

    /**
     * 用户注册接口
     * @param telphone
     * @param otpCode
     * @param name
     * @param gender
     * @param age
     * @param password
     * @return
     * @throws BusinessException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(value = "/register", consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") String gender,
                                     @RequestParam(name = "age") String age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        System.out.println("otpCode: "+otpCode +"inSessionOtpCode: "+inSessionOtpCode);
        if (!StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(Integer.valueOf(age));
        userModel.setGender(Byte.valueOf(gender));
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");

        //密码加密
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    /**
     * 密码加密
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

}
