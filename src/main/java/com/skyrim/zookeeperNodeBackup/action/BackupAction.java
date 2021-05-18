package com.skyrim.zookeeperNodeBackup.action;

import com.skyrim.zookeeperNodeBackup.entity.ZkInfo;
import com.skyrim.zookeeperNodeBackup.entity.Znode;
import com.skyrim.zookeeperNodeBackup.service.ZnodeBackup;
import com.skyrim.zookeeperNodeBackup.service.ZnodeRecover;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Created by Skyrim on 2021/5/10 15:49
 */
@Component
@EnableScheduling
public class BackupAction implements ApplicationRunner {

    private  final  static Logger logger = LoggerFactory.getLogger(BackupAction.class);

    @Autowired
    private ZnodeBackup znodeBackup;

    @Autowired
    private ZnodeRecover znodeRecover;

    @Value("${backup}")
    private int backup;

    @Value("${recover}")
    private int recover;

    private int i = 0;

    public void runBackup() {
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
            logger.info(info.toString());
            try {
                znodeBackup.backup(info);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void runRecover() {
        List<ZkInfo> zkInfo = znodeRecover.getZkInfo();
        for (ZkInfo info : zkInfo) {
            logger.info(info.toString());
            ZooKeeper zooKeeper = znodeRecover.connect(info);
            List<Znode> znodes = null;
            try {
                znodes = znodeRecover.readJsonData(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            znodeRecover.recover(zooKeeper,znodes,info);
            try {
                logger.info("备份完成，断开zk连接");
                zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        logger.info(trustStorePath);
        if (backup == 1) {
            logger.info("=======================开始备份=========================");
            logger.info("=======================开始备份=========================");
            logger.info("=======================开始备份=========================");
            runBackup();
        } else {
            if (recover == 1){
                logger.info("===========================================================================");
                logger.info("==============================backup == 0，不执行备份=========================");
                logger.info("==============================backup == 0，不执行备份=========================");
                logger.info("==============================backup == 0，不执行备份=========================");
                logger.info("==============================backup == 0，不执行备份=========================");
                logger.info("==============================backup == 0，不执行备份=========================");
                logger.info("===========================================================================");
                runRecover();
            }
        }
    }


}
