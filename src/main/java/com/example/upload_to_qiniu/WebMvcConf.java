package com.example.upload_to_qiniu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.File;

@Configuration
public class WebMvcConf extends WebMvcConfigurerAdapter {
    public static final String ANALYSIS_YES = "yes";
    public static final String ANALYSIS_NO = "no";

    public static final String UPLOAD_DIR = "upload";

    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW2QZKY3wM4y29nc9R8GfUXQrQPilYmDfS8ZJ2Z8T2a2s9zCx5YJWe1egH8mYz-_A51rvgGCKf4lg5tloB9gcHBKlNzRY4nJEoOf4Om5irAFBK2hx_3-xdxfUh5nIsOV4MOVR6571qz0gLgw-JmTeoSMWR6pk8u7iFsLO01ZjugwIDAQAB";
    public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbZBkpjfAzjLb2dz1HwZ9RdCtA-KViYN9LxknZnxPZraz3MLHlglZ7V6AfyZjP78DnWu-AYIp_iWDm2WgH2BwcEqU3NFjickSg5_g6bmKsAUEraHH_f7F3F9SHmciw5Xgw5VHrnvWrPSAuDD4mZN6hIxZHqmTy7uIWws7TVmO6DAgMBAAECgYAz7m8N-QXh4ewDVWrkSKFWKNnqKxxM1cqSInxLqxnRw9VdpwbAmpGn5vhElBy_VMK1BsoTMfvpkyDQbYi7GBn-syB9zRANr1JmZF0QP1jUSPo_5ppNAZXQ6sU8YlrSgMbvCCPZJGFp9l462cFoi2G_9p2kxRoLauuBr5Fca0_DsQJBAPtCFkqBtegtGcQmS98upzHGUlfj_tDQ_a712OqbxSeBZtZcmn125jmdmpZWwxUqZqELXjEyPCcWCyEddDMPXeUCQQCZsdIrCDa8SXx3qhhrkcEo2eK2tRfgzcg-gQfcAhmCcskGcFbewKALsKUqCQ9Z-b92DolKiaYMFIy5zogFABRHAkB3Za1AFKmDvkLbQjOVyA6tiYfBuldxlY6noK5GtmUW49ghj3nemyzGPk2imXI00aRZbRSCnlOVY1VAlEWOqgO9AkBy0aVxAHHjuAKEY05bDkh_fEcit6dfClzOVRbKBceI7LfBV6uOPdlS4mSBQsN1NF8Uk0d9p9ekrrVzGhgDpEuBAkEAos12f282QhdVIZZKVd59FP4FYxzegmY5YSkvzSodMeBjNNFhf_M_xgI5db1IjzU00DUk9AFGUGZtNrYJW3TWeQ";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //和页面有关的静态目录都放在项目的static目录下
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        //registry.addViewController("/").setViewName("/index");
        registry.addViewController("/qiniu").setViewName("/qiniu");
        registry.addViewController("/ws").setViewName("/ws");
        registry.addViewController("/upload").setViewName("/upload_js");
        registry.addViewController("/upload_java").setViewName("/upload_java");
    }

}
