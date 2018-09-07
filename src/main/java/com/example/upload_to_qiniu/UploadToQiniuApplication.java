package com.example.upload_to_qiniu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@SpringBootApplication
public class UploadToQiniuApplication {

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "welcome to upload page!";
    }

    public static void main(String[] args) {
        try {
            RsaUtil.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(UploadToQiniuApplication.class, args);
    }
}
