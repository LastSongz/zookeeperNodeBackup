package com.skyrim.zookeeperNodeBackup.action;

import com.skyrim.zookeeperNodeBackup.entity.ZkInfo;
import com.skyrim.zookeeperNodeBackup.service.ZnodeBackup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Skyrim on 2021/5/10 15:49
 */
@Component
@EnableScheduling
public class BackupAction implements ApplicationRunner {
    @Autowired
    private ZnodeBackup znodeBackup;

    public void runBackup(){
        /**
         * 读取zkInfo.cfg配置文件
         * 配置文件说明：
         * 一台机器上有多个zk集群的，在配置文件每行配置一个zk集群的信息即可
         * 格式：
         * ip:port,备份文件存放位置,digest,auth:password,ssl配置文件位置
         * 每个信息以逗号隔开，严格按照上一行顺序填写，前两项为必填项，后面没有哪一项则不写，注意不要在末尾多加逗号
         */
        List<ZkInfo> zkInfoList = znodeBackup.getZkInfo();
        for (ZkInfo info : zkInfoList) {
            try {
                znodeBackup.backup(info);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println(trustStorePath);
        runBackup();
    }
}
