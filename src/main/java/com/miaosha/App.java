package com.miaosha;


import com.miaosha.dao.UserDOMapper;
import com.miaosha.entities.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiyu1.sun
 */
@SpringBootApplication(scanBasePackages = {"com.miaosha"})
@RestController
@MapperScan("com.miaosha.dao")
public class App {

    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home() {
        UserDO userDO = userDOMapper.selectByPrimaryKey(1);
        if (userDO == null) {
            return "用户对象不存在";
        } else {
            return userDO.getName();
        }
    }

    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}
