package com.skyrim.zookeeperNodeBackup.service;

import com.alibaba.fastjson.JSON;
import com.skyrim.zookeeperNodeBackup.entity.ZkInfo;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author kaixuan
 */
@Service
public class ZnodeBackup implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);

    /**
     * 备份znode
     *
     * @param info
     */
    public  void backup(ZkInfo info) throws InterruptedException {
        List<HashMap> znodeList = new ArrayList<>();
        ZooKeeper zk = null;
        BufferedReader br = null;
        try {
            //是否使用ssl认证
            if (info.isSslClient()) {

                BufferedReader sslbr = null;
                List<String> ssl = new ArrayList<>();
                try {
                    sslbr = new BufferedReader(new FileReader(info.getSslClientCfg()));//读取配置文件
//            br = new BufferedReader(new FileReader("src\\main\\resources\\zkInfo.cfg"));//读取配置文件
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


                zk = new ZooKeeper(info.getConnectPath(), 5000, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getState() == Event.KeeperState.SyncConnected){
                            //创建完毕之后叫醒等待线程返回结果
                            latch.countDown();
                        }
                    }
//                }, new ZKClientConfig(info.getSslClientCfg()));
                },config);

            } else {
                zk = new ZooKeeper(info.getConnectPath(), 5000, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getState() == Event.KeeperState.SyncConnected){
                            //创建完毕之后叫醒等待线程返回结果
                            latch.countDown();
                        }
                    }
                });
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
            String scheme = info.getScheme();
            zk.addAuthInfo(scheme, info.getAuth_pwd().getBytes());
        }


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");//设置日期格式
        String currentTime = df.format(new Date());// new Date()为获取当前系统时间

        try {
            listZnode(zk, "/", currentTime, info.getSaveFilePath(), znodeList);//开始备份并打印结果
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        zk.close();

        /**
         * 将znode备份信息输出到文件中
         */
        String jsonString = JSON.toJSONString(znodeList, true);
        try {
            /**
             * 这一段是以json格式将znode备份信息输出到文件中，方便查看
             */
            File znodeBackup = new File(info.getSaveFilePath() + "/znodeBackup" + currentTime + ".txt");
//            File znodeBackup = new File(info.getSaveFilePath() + "/znodeBackup" + ".txt");

            File f = new File(info.getSaveFilePath());
            if (!f.exists()) {
                f.mkdirs();
            }

            // true = append file
            FileWriter fileWritter = new FileWriter(znodeBackup, false);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(jsonString);
            bufferWritter.flush();
            bufferWritter.close();

            /**
             * 这一段代码，把备份的znode信息以zkClient指令的格式输出
             * 后面有导入znode需求时，只需要将znodeRecover文件里的相应内容复制粘贴到zk命令行
             * 即可以直接把之前备份的znode恢复
             */
            File znodeRecover = new File(info.getSaveFilePath() + "/znodeRecover" + currentTime + ".txt");
//            File znodeRecover = new File(info.getSaveFilePath() + "/znodeRecover" + ".txt");
            FileWriter recoverWriter = new FileWriter(znodeRecover, false);
            BufferedWriter recoverBw = new BufferedWriter(recoverWriter);
            //有acl认证的zk集群要把acl认证加进去
            if (info.isHaveAuth()){
                recoverBw.write("addauth " + info.getScheme() + " " + info.getAuth_pwd());
                recoverBw.newLine();
            }
            //遍历znodeList
            for (Map<String, Map> m : znodeList) {
                Iterator<String> iterator = m.keySet().iterator();
                while (iterator.hasNext()){
                    String  key = iterator.next();
                    Map<String, Object> znode = m.get(key);
                    String zdata = (String) znode.get("data");
                    //打印一条节点信息
                    recoverBw.write("create  " + key + " '" + zdata + "'");

                    //检查是否需有acl认证,有的要加进去
                    List<ACL> acl = (List<ACL>) znode.get("acl");
                    String scheme = acl.get(0).getId().getScheme();
                    if (scheme.equals(info.getScheme())){
                        recoverBw.newLine();
                        recoverBw.write("setAcl " + key + " auth:" + info.getAuth_pwd() + ":crwda");
                    }

                }
                recoverBw.newLine();
                recoverBw.flush();
            }
            recoverBw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n" + "备份完成！\n" + "zk地址：" + info.getConnectPath() + "\n" + "备份位置：" + info.getSaveFilePath() + "/znodeBackup" + currentTime + ".txt");
    }

    /**
     * 递归扫描所有znode并输出到txt中
     *
     * @param path
     * @param znodeList
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void listZnode(ZooKeeper zk, String path, String currentTime, String saveFilePath, List<HashMap> znodeList) throws KeeperException, InterruptedException, IOException {
        //获取节点统计信息
        Stat stat = zk. exists(path, null);

        //获取节点ACL信息
        List<ACL> acl = zk.getACL(path, stat);

        //尝试获取节点数值
        byte[] zkData = zk.getData(path, null, stat);

        HashMap node = new HashMap();

        if (zkData != null) {
            String zkDatas = new String(zkData);
            Map nodeInfo = new HashMap();
            nodeInfo.put("data", zkDatas);
            nodeInfo.put("acl", acl);
            nodeInfo.put("stat", stat);
            node.put(path, nodeInfo);

            System.out.println(path + "=" + zkDatas + "=" + acl.toString() + "=" + stat.toString());
        } else {
            Map nodeInfo = new HashMap();
            nodeInfo.put("acl", acl);
            nodeInfo.put("stat", stat);
            node.put(path, nodeInfo);

            System.out.println(path + "=" + acl.toString() + "=" + stat.toString());
        }
        znodeList.add(node);
        //获取子节点
        List<String> childrens = zk.getChildren(path, null);

        //判断是否有子节点，如果，结束当前方法
        if (childrens.isEmpty() || childrens == null) {
            System.out.println();
            return;
        }

        //如有，继续扫描子节点
        for (String s : childrens) {
            //判断是否为根目录
            if (path.equals("/")) {
                listZnode(zk, path + s, currentTime, saveFilePath, znodeList);
            } else {
                listZnode(zk, path + "/" + s, currentTime, saveFilePath, znodeList);
            }
        }
    }

    /**
     * 读取配置文件
     */
    public List<ZkInfo> getZkInfo() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("zkInfo.cfg"));//读取配置文件
//            br = new BufferedReader(new FileReader("src\\main\\resources\\zkInfo.cfg"));//读取配置文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<ZkInfo> zkInfoList = new ArrayList<>();
        String str;
        try {
            ZkInfo zkInfo = null;
            while ((str = br.readLine()) != null) {
                String[] info = str.split(",");
                if (info.length == 5) {
                    zkInfo = new ZkInfo(info[0], info[1], info[2], info[3], info[4]);
                } else if (info.length == 4) {
                    zkInfo = new ZkInfo(info[0], info[1], info[2], info[3]);
                } else if (info.length == 3) {
                    zkInfo = new ZkInfo(info[0], info[1], info[2]);
                } else if (info.length == 2) {
                    zkInfo = new ZkInfo(info[0], info[1]);
                }
                zkInfoList.add(zkInfo);

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zkInfoList;
    }


    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected){
            //创建完毕之后叫醒等待线程返回结果
            latch.countDown();
        }
    }
}
