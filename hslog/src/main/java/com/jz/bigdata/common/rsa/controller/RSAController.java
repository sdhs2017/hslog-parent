package com.jz.bigdata.common.rsa.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yiyang
 * @Date: 2021/3/16 16:47
 * @Description: RSA 加密解密
 */
@Slf4j
@Controller
@RequestMapping("/rsa")
public class RSAController {
    @ResponseBody
    @RequestMapping(value="/getRSAPublicKey", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取RSA加密公钥")
    public String getRSAPublicKey(HttpServletRequest request) {
        try{
            String publicKey = RSAUtil.getPublicKey();
            return Constant.successMessage(publicKey);
        }catch(Exception e){
            log.error("获取RSA加密公钥失败"+e.getMessage());
            return Constant.failureMessage("获取RSA加密公钥失败！");
        }

    }
}
