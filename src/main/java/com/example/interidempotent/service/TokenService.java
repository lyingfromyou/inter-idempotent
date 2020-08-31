package com.example.interidempotent.service;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {

    /**
     * 创建token
     *
     * @return
     */
    String createToken();

    /**
     * 检验token
     *
     * @param request
     * @return
     */
    boolean checkToken(HttpServletRequest request) throws Exception;


    boolean checkIdempotent(HttpServletRequest request) throws Exception;

}
