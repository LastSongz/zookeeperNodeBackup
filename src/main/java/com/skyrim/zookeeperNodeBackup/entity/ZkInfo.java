package com.skyrim.zookeeperNodeBackup.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZkInfo implements Serializable {
    private String connectPath;
    private String saveFilePath;
    private String scheme;
    private String auth_pwd;
    private String sslClientCfg;
    private boolean isHaveAuth;
    private boolean isSslClient;

    ZkInfo(){}

    public ZkInfo(String connectPath, String saveFilePath, String scheme, String auth_pwd, String sslClientCfg){
        setConnectPath(connectPath);
        setSaveFilePath(saveFilePath);
        setScheme(scheme);
        setAuth_pwd(auth_pwd);
        setSslClientCfg(sslClientCfg);
        setHaveAuth(true);
        setSslClient(true);
    }

    public ZkInfo(String connectPath, String saveFilePath, String scheme, String auth_pwd){
        setConnectPath(connectPath);
        setSaveFilePath(saveFilePath);
        setScheme(scheme);
        setAuth_pwd(auth_pwd);
        setHaveAuth(true);
        setSslClient(false);
    }

    public ZkInfo(String connectPath, String saveFilePath, String sslClientCfg){
        setConnectPath(connectPath);
        setSaveFilePath(saveFilePath);
        setSslClientCfg(sslClientCfg);
        setHaveAuth(false);
        setSslClient(true);
    }

    public ZkInfo(String connectPath, String saveFilePath){
        setConnectPath(connectPath);
        setSaveFilePath(saveFilePath);
        setHaveAuth(false);
        setSslClient(false);
    }

    public String getConnectPath() {
        return connectPath;
    }

    public void setConnectPath(String connectPath) {
        this.connectPath = connectPath;
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getAuth_pwd() {
        return auth_pwd;
    }

    public void setAuth_pwd(String auth_pwd) {
        this.auth_pwd = auth_pwd;
    }

    public String getSslClientCfg() {
        return sslClientCfg;
    }

    public void setSslClientCfg(String sslClientCfg) {
        this.sslClientCfg = sslClientCfg;
    }

    public boolean isHaveAuth() {
        return isHaveAuth;
    }

    public void setHaveAuth(boolean haveAuth) {
        isHaveAuth = haveAuth;
    }

    public boolean isSslClient() {
        return isSslClient;
    }

    public void setSslClient(boolean sslClient) {
        isSslClient = sslClient;
    }
}
