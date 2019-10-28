package com.jz.bigdata.util.token;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @program: hslog-parent
 * @description: JwtUtils
 * @author: Savilio
 * @create: 2019-10-28 14:52
 **/


public class    JwtUtils {
    public static Claims parseJWT(String jsonWebToken, String base64Security) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String createJWT(String name, String userId, String role,
                                   String audience, String issuer, long TTLMillis, String                                                                   base64Security) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成签名密钥 就是一个base64加密后的字符串？
        byte[] apiKeySecretBytes =                                                                                                         DatatypeConverter.parseBase64Binary (base64Security);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes,                                                       signatureAlgorithm. getJcaName());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userName", name);
        jsonObject.put("userLoginName", userId);
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .setIssuedAt(now) //创建时间
                .setSubject(jsonObject.toString()) //主题，也差不多是个人的一些信息
                .setIssuer(issuer) //发送谁
                .setAudience(audience) //个人签名
                .signWith(signatureAlgorithm, signingKey); //估计是第三段密钥
        //添加Token过期时间
        if (TTLMillis >= 0) {
            //过期时间
            long expMillis = nowMillis + TTLMillis;
            //现在是什么时间
            Date exp = new Date(expMillis);
            //系统时间之前的token都是不可以被承认的
            builder.setExpiration(exp).setNotBefore(now);
        }
        //生成JWT
        return builder.compact();
    }
}


