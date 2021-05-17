package com.skyrim.zookeeperNodeBackup.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ZkInfo implements Serializable {
    private String connectPath;
    private String saveFilePath;
    private Map<String,String>[] acl;
    private String sslClientCfg;
    private boolean isHaveAuth;
    private boolean isSslClient;

    ZkInfo(){}

    public void setSslClientCfg(String sslClientCfg) {
        this.sslClientCfg = sslClientCfg;
        this.isSslClient = true;
    }

    public void setAcl(Map<String, String>[] acl) {
        this.acl = acl;
        this.isHaveAuth = true;
    }


    //
//    public ZkInfo(String connectPath, String saveFilePath, String acl, String sslClientCfg){
//        setConnectPath(connectPath);
//        setSaveFilePath(saveFilePath);
//        setAcl(acl.split(";"));
//        setSslClientCfg(sslClientCfg);
//        setHaveAuth(true);
//        setSslClient(false);
//    }
//
//    public ZkInfo(String connectPath, String saveFilePath, String acl,String isSslClient){
//        setConnectPath(connectPath);
//        setSaveFilePath(saveFilePath);
//        setAcl(acl.split(";"));
//        setHaveAuth(true);
//        setSslClient(false);
//    }
//
//    public ZkInfo(String connectPath, String saveFilePath, String sslClientCfg){
//        setConnectPath(connectPath);
//        setSaveFilePath(saveFilePath);
//        setSslClientCfg(sslClientCfg);
//        setHaveAuth(false);
//        setSslClient(true);
//    }
//
//    public ZkInfo(String connectPath, String saveFilePath){
//        setConnectPath(connectPath);
//        setSaveFilePath(saveFilePath);
//        setHaveAuth(false);
//        setSslClient(false);
//    }


}
