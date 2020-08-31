package com.example.interidempotent.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.interidempotent.constant.Constant;
import com.example.interidempotent.service.RedisService;
import com.example.interidempotent.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RedisService redisService;


    /**
     * 创建token
     *
     * @return
     */
    @Override
    public String createToken() {
        String str = IdUtil.randomUUID();
        StrBuilder token = new StrBuilder();
        try {
            token.append(Constant.TOKEN_PREFIX).append(str);
            redisService.setEx(token.toString(), token.toString(), 10000L);
            boolean notEmpty = StrUtil.isNotEmpty(token.toString());
            if (notEmpty) {
                return token.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * 检验token
     *
     * @param request
     * @return
     */
    @Override
    public boolean checkToken(HttpServletRequest request) throws Exception {
        String token = request.getHeader(Constant.TOKEN_NAME);
        if (StrUtil.isBlank(token)) {// header中不存在token
            token = request.getParameter(Constant.TOKEN_NAME);
            if (StrUtil.isBlank(token)) {// parameter中也不存在token
//                throw new ServiceException(Constant.ResponseCode.ILLEGAL_ARGUMENT, 100);
                throw new Exception("token 没有");
            }
        }

        if (!redisService.exists(token)) {
//            throw new ServiceException(Constant.ResponseCode.REPETITIVE_OPERATION, 200);
            throw new Exception("重复性操作");
        }

        boolean remove = redisService.remove(token);
        if (!remove) {
            throw new Exception("重复性操作");
//            throw new ServiceException(Constant.ResponseCode.REPETITIVE_OPERATION, 200);
        }
        return true;
    }


    public boolean checkIdempotent(HttpServletRequest request) throws Exception {
        String token = request.getHeader(Constant.TOKEN_NAME);
        if (StrUtil.isBlank(token)) {// header中不存在token
            token = request.getParameter(Constant.TOKEN_NAME);
            if (StrUtil.isBlank(token)) {// parameter中也不存在token
//                throw new ServiceException(Constant.ResponseCode.ILLEGAL_ARGUMENT, 100);
                throw new Exception("没有 token");
            }
        }
        return redisService.setNx(request.getRequestURI(), token, TimeUnit.MINUTES.toSeconds(5));
    }


}