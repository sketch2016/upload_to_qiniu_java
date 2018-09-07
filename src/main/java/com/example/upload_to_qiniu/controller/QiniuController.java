package com.example.upload_to_qiniu.controller;

import com.example.upload_to_qiniu.AlgorithmUtil;
import com.example.upload_to_qiniu.RsaUtil;
import com.example.upload_to_qiniu.WebMvcConf;
import com.example.upload_to_qiniu.dao.FileInfo;
import com.example.upload_to_qiniu.bean.MyWebSocket;
import com.example.upload_to_qiniu.dao.QiniuDao;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
public class QiniuController {
    String ACCESS_KEY = "lm6f3peedWHayH-xyZdd-xSCqyoNyvRhS7jkdT0e";
    String SECRET_KEY = "f78vnIyeozEbZgqOxemFNVfg4cs9Q0YifnuK9WS_";
    //要上传的空间
    String bucketname = "xiangantest";
    String doman = "7xko2c.com1.z0.glb.clouddn.com";

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    @Autowired
    QiniuDao qiniuDao;

    @Autowired
    MyWebSocket myWebSocket;

    @RequestMapping("/test1")
    @ResponseBody
    public String test() {
        return "welcome to upload page1!";
    }

    @RequestMapping("/uptoken")
    @ResponseBody
    public Map getUpToken() {
        Map result = new HashMap<>();
        String uptoken = auth.uploadToken(bucketname);

        result.put("uptoken", uptoken);
        result.put("domain", doman);
        System.out.println("token:" + result);
        return result;
    }

    @RequestMapping("/downloadtoken")
    @ResponseBody
    public String getDownloadToken(@RequestParam("baseUrl") String baseUrl) {
        String downloadToken = auth.privateDownloadUrl(baseUrl);
        System.out.println("downloadToken:" + downloadToken);

        //storeDownloadUrl(downloadToken);
        return downloadToken;
    }

    @RequestMapping("/storeDownloadUrl")
    @ResponseBody
    public String storeDownloadUrl(@RequestParam("key") String key, @RequestParam("downloadUrl") String downloadUrl) {
        System.out.println("key:" + key + " storeDownloadUrl:" + downloadUrl);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(key);
        fileInfo.setDownloadUrl(downloadUrl);
        fileInfo.setSended("false");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        String time = df.format(System.currentTimeMillis());
        fileInfo.setStoreTime(String.valueOf(time));

        try {
            if (myWebSocket.sendInfo(fileInfo.toString())) {
                fileInfo.setSended("true");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        qiniuDao.save(fileInfo);

        return "store downloadUrl success!";
    }

    public String storeDownloadUrl(String key, String originalName, String downloadUrl) {
        System.out.println("storeDownloadUrl-key:" + key + " storeDownloadUrl:" + downloadUrl);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(key);
        fileInfo.setOriginalName(originalName);
        fileInfo.setDownloadUrl(downloadUrl);
        fileInfo.setSended("false");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        String time = df.format(System.currentTimeMillis());
        fileInfo.setStoreTime(String.valueOf(time));

        try {
            if (myWebSocket.sendInfo(fileInfo.toString())) {
                fileInfo.setSended("true");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        qiniuDao.save(fileInfo);

        return "store downloadUrl success!";
    }

    @RequestMapping("/uploadfile")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        String res = null;
        try {
            String fileName = multipartFile.getOriginalFilename();
            String key = String.valueOf(System.currentTimeMillis());
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();

            uploadToQiNiu(fileInputStream, key, fileName);

            res = downloadToLocalAndAnalysis(multipartFile.getBytes(), key);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private void uploadToQiNiu(final FileInputStream fileInputStream, String key, String fileName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Configuration config = new Configuration(Zone.zone0());
                UploadManager uploadManager = new UploadManager(config);
                Response response = null;
                try {
                    response = uploadManager.put(fileInputStream, key, auth.uploadToken(bucketname), null, null);
                    if (response.isOK()) {
                        String res = response.bodyString();
                        System.out.println("upload success! response = " + res);
                        String baseUrl = "http://" + doman + "/" + key;
                        storeDownloadUrl(key, fileName, getDownloadToken(baseUrl));
                    } else {
                        System.out.println("upload fail! response = " + response.bodyString());
                    }
                } catch (QiniuException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    private String downloadToLocalAndAnalysis(byte[] data, String key) {
        System.out.println(new java.util.Date() + "data.length=" + data.length + " key = " + key);
        String result = null;
        File temFile = new File(WebMvcConf.UPLOAD_DIR);
        if (temFile.exists() && temFile.isDirectory()) {
            for (File childFile : temFile.listFiles()) {
                childFile.delete();
            }
        } else {
            temFile.mkdirs();
        }

        File file = new File(temFile, key);

        try {
            byte[] decryptData = RsaUtil.privateDecrypt(data, RsaUtil.getPrivateKey(WebMvcConf.PRIVATE_KEY));
            //byte[] decryptData = data;
            FileOutputStream out = new FileOutputStream(file);
            out.write(decryptData);
            out.flush();
            out.close();
            System.out.println("store file success, file:" + key);

            result = startAnalysis(decryptData);
            //myWebSocket.sendInfo(result);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("store file fail, file:" + key);
        }

        return result;

    }


    @RequestMapping("/analysis")
    @ResponseBody
    public String startAnalysis(byte[] decryptData) {
        File file = new File(WebMvcConf.UPLOAD_DIR);
        File picture = null;
        if (file.exists() && file.isDirectory()) {
            picture =file.listFiles()[0];

            System.out.println("get picture: picture=" + picture);
        }

        if (picture != null) {
            String result = AlgorithmUtil.getInstance().run(decryptData);
            System.out.println(new java.util.Date() + "result =" + result);
            return result;
        } else {
            return WebMvcConf.ANALYSIS_NO;
        }

    }
}
