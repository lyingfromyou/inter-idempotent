package com.example.interidempotent.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.interidempotent.annotation.AutoIdempotent;
import com.example.interidempotent.constant.CommonConstants;
import com.example.interidempotent.service.TokenService;
import com.example.interidempotent.util.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class BusinessController {
    @Resource
    private TokenService tokenService;



    @PostMapping("/get/token")
    public R getToken() {
        String token = tokenService.createToken();
        R resultVo = new R();
        if (StrUtil.isNotEmpty(token)) {
            resultVo.setCode(CommonConstants.SUCCESS);
            resultVo.setData(token);
            return resultVo;
        }
        return resultVo;
    }


    @AutoIdempotent
    @PostMapping("/test/Idempotence")
    public String testIdempotence() {
        return IdUtil.fastUUID();
    }
}
