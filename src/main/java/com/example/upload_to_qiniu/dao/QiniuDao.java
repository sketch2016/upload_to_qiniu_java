package com.example.upload_to_qiniu.dao;

import com.example.upload_to_qiniu.dao.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QiniuDao extends JpaRepository<FileInfo, Long> {

    FileInfo findByName(String name);

    FileInfo findByDownloadUrl(String downloadUrl);

    FileInfo findByStoreTime(String storeTime);

    FileInfo findBySended(String sended);

    List<FileInfo> findAllBySended(String sended);

    void deleteByname(String name);

    void deleteByDownloadUrl(String downloadUrl);

    void deleteByStoreTime(String storeTime);

    void deleteBySended(String sended);

    boolean existsByName(String name);

    boolean existsByDownloadUrl(String downloadUrl);

    boolean existsByStoreTime(String storeTime);

    boolean existsBySended(String sended);
}
