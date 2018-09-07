package com.example.upload_to_qiniu.dao;


import javax.persistence.*;

@Entity
@Table(name="file_info")
public class FileInfo {

    public FileInfo() {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @PrimaryKeyJoinColumn
    @Column(name = "id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="original_name")
    private String originalName;

    @Column(name="download_url")
    private String downloadUrl;

    @Column(name="store_time")
    private String storeTime;

    @Column(name="sended")
    private String sended;

    public String getSended() {
        return sended;
    }

    public void setSended(String sended) {
        this.sended = sended;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(String storeTime) {
        this.storeTime = storeTime;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String toString() {
        return "{" +
                "\n    id:" + id +
                ",\n    name:" + name +
                ",\n    originalName:" + originalName +
                ",\n    downloadUrl:" + downloadUrl +
                ",\n    storeTime:" + storeTime +
                ",\n    sended:" + String.valueOf(sended) +
                "\n}";
    }
}
