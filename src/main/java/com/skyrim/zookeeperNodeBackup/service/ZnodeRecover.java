package com.skyrim.zookeeperNodeBackup.service;

import com.alibaba.fastjson.JSONObject;
import com.skyrim.zookeeperNodeBackup.entity.ZkInfo;
import com.skyrim.zookeeperNodeBackup.entity.Znode;
import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Skyrim on 2021/5/12 16:39
 */
@Service
public class ZnodeRecover {

    @Value("${recoverInfo}")
    private String recoverInfo;


    public void recover(ZooKeeper zooKeeper, List<Znode> znodeList, ZkInfo zkInfo) {
        for (Znode znode : znodeList) {
            String path = znode.getPath();
            try {
                //如果待恢复节点不存在zk中，则对该节点进行恢复
                System.out.println("正在还原{"+"znode："+path+"}");
                if (zooKeeper.exists(path, null) == null) {
                    //拉取待备份节点的acl信息
                    List<ACL> acls = znode.getAcl();
                    //比对节点的所有acl信息与zkInfo中的信息，将其密码替换成zkInfo中确定的密码，不然可能会造成节点不可用
                    for (ACL a : acls) {
                        //获取节点acl用户密码
                        Id id = a.getId();
                        String id1 = id.getId();
                        String[] recAuth = id1.split(":");
                        Map<String, String>[] infoAcls = zkInfo.getAcl();
                        //比对用户名并将zkInfo中的密码填入znode
                        for (Map<String, String> m : infoAcls) {
                            String acl = m.get("acl");
                            String[] infoAuth = acl.split(":");
                            if (recAuth[0].equals(infoAuth[1])) {
                                try {
                                    //将密码按照zk需求格式加密再重新导入
                                    id.setId(DigestAuthenticationProvider.generateDigest(recAuth[0] + ":" + infoAuth[2]));
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                                a.setId(id);
                            }
                        }
                    }
                    CreateMode createMode;
                    if (znode.getStat().getEphemeralOwner() == 0){
                        createMode = CreateMode.PERSISTENT;
                    }else {
                        createMode = CreateMode.EPHEMERAL;
                    }
                    String s = zooKeeper.create(path, znode.getData().getBytes(), acls, createMode, znode.getStat());
                    System.out.println("已还原{"+"znode："+path+"结果："+s+"}");
                }else {
                    System.out.println("该节点{"+"znode:"+path+"}，已存在！");
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 读取文件数据加入到map缓存中
     *
     * @throws IOException
     */
    public List<Znode> readJsonData(ZkInfo zkInfo) throws IOException {
        File file = new File(zkInfo.getSaveFilePath());
        String jsonString = FileUtils.readFileToString(file, "UTF-8");

        List<Znode> znodeList = JSONObject.parseArray(jsonString, Znode.class);
        return znodeList;
    }

    /**
     * 连接zk实例
     *
     * @param info
     * @return
     */
    public ZooKeeper connect(ZkInfo info) {
        ZooKeeper zk = null;
        try {
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("==========DefaultWatcher start==============");

                    System.out.println("DefaultWatcher state: " + event.getState().name());

                    System.out.println("DefaultWatcher type: " + event.getType().name());

                    System.out.println("DefaultWatcher path: " + event.getPath());

                    System.out.println("==========DefaultWatcher end==============");
                }
            };
            //是否使用ssl认证
            if (info.isSslClient()) {

                BufferedReader sslbr = null;
                List<String> ssl = new ArrayList<>();
                try {
                    sslbr = new BufferedReader(new FileReader(info.getSslClientCfg()));//读取配置文件
//            br = new BufferedReader(new FileReader("src\\main\\resources\\zkInfo.json"));//读取配置文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    for (int i = 0; i < 6; i++) {
                        ssl.add(sslbr.readLine().split("=")[1]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //添加zkssl认证信息
                ZKClientConfig config = new ZKClientConfig();
                config.setProperty("zookeeper.clientCnxnSocket", "org.apache.zookeeper.ClientCnxnSocketNetty");
                config.setProperty("zookeeper.client.secure", "true");
                config.setProperty("zookeeper.ssl.keyStore.location", ssl.get(2));
                config.setProperty("zookeeper.ssl.keyStore.password", ssl.get(3));
                config.setProperty("zookeeper.ssl.trustStore.location", ssl.get(4));
                config.setProperty("zookeeper.ssl.trustStore.password", ssl.get(5));
                config.setProperty("zookeeper.ssl.hostnameVerification", "false");

                System.out.println("ssl链接地址！！！！！！！！！！！！！！！！！！！");
                System.out.println(info.getConnectPath());
                zk = new ZooKeeper(info.getConnectPath(), 30000, watcher, config);

            } else {
                System.out.println("链接地址！！！！！！！！！！！！！！！！！！！");
                System.out.println(info.getConnectPath());
                zk = new ZooKeeper(info.getConnectPath(), 30000, watcher);
            }
//            try {
//                //等待创建完毕
//                latch.await();
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (info.isHaveAuth()) {//如果目标zk采用了acl认证，在zkInfo.cfg文件中添加对应信息即可
            Map<String, String>[] acls = info.getAcl();
            for (int i = 0; i < acls.length; i++) {
                String acl = acls[i].get("acl");
                String[] split = acl.split(":");
                zk.addAuthInfo(split[0], (split[1] + ":" + split[2]).getBytes());
            }
        }
        return zk;
    }

    /**
     * 读取配置文件
     */
    public List<ZkInfo> getZkInfo() {
//        ClassPathResource resource = new ClassPathResource(zkInfo);
        File file = new File(recoverInfo);
        String jsonString = null;
        try {
//            file = resource.getFile();
            jsonString = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ZkInfo> zkInfoList = JSONObject.parseArray(jsonString, ZkInfo.class);
        return zkInfoList;
    }

}
