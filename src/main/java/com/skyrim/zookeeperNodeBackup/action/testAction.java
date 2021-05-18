//package com.skyrim.zookeeperNodeBackup.action;
//
//import com.alibaba.fastjson.JSONObject;
//import com.skyrim.zookeeperNodeBackup.entity.ZkInfo;
//import com.skyrim.zookeeperNodeBackup.entity.Znode;
//import com.skyrim.zookeeperNodeBackup.service.ZnodeBackup;
//import lombok.Data;
//import org.apache.commons.io.FileUtils;
//import org.apache.zookeeper.*;
//import org.apache.zookeeper.data.ACL;
//import org.apache.zookeeper.data.Id;
//import org.apache.zookeeper.data.Stat;
//import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.*;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Skyrim on 2021/5/10 16:06
// */
//@Component
//@Data
//@RunWith(SpringRunner.class)
//public class testAction {
//    //    @Value("${ssl.keyStore.path}")
////    private String trustStorePath;
////
////    @Value("${ssl.keyStore.passwd}")
////    private String keyStorePasswd;
////
////    @Value("${ssl.trustStore.path}")
////    private String keyStorePath;
////
////    @Value("${ssl.trustStore.passwd}")
////    private String trustStorePasswd;
//
//
//
//    @Test
//    public void test11() {
////        System.out.println(trustStorePasswd);
////        System.out.println(keyStorePasswd);
//        try {
//            Watcher watcher = new Watcher() {
//
//                @Override
//                public void process(WatchedEvent event) {
//                    System.out.println("==========DefaultWatcher start==============");
//
//                    System.out.println("DefaultWatcher state: " + event.getState().name());
//
//                    System.out.println("DefaultWatcher type: " + event.getType().name());
//
//                    System.out.println("DefaultWatcher path: " + event.getPath());
//
//                    System.out.println("==========DefaultWatcher end==============");
//
//                }
//            };
////            Watcher childrenWatcher = new Watcher() {
////
////                @Override
////                public void process(WatchedEvent event) {
////                    System.out.println("==========ChildrenWatcher start==============");
////
////                    System.out.println("ChildrenWatcher state: " + event.getState().name());
////
////                    System.out.println("ChildrenWatcher type: " + event.getType().name());
////
////                    System.out.println("ChildrenWatcher path: " + event.getPath());
////
////                    System.out.println("==========ChildrenWatcher end==============");
////                }
////            };
////            Watcher dataWatcher = new Watcher() {
////
////                @Override
////                public void process(WatchedEvent event) {
////                    System.out.println("==========dataWatcher start==============");
////
////                    System.out.println("dataWatcher state: " + event.getState().name());
////
////                    System.out.println("dataWatcher type: " + event.getType().name());
////
////                    System.out.println("dataWatcher path: " + event.getPath());
////
////                    System.out.println("==========dataWatcher end==============");
////                }
////            };
////            System.out.println("创建zk客户端，并注册默认watcher……………………………………………………………………………………");
////            // 创建zk客户端，并注册默认watcher
//            ZooKeeper zooKeeper = new ZooKeeper("172.27.187.211:2181", 50000, watcher);
////            System.out.println("让childrenWatcher监听 /GetChildren 节点的子节点变化(默认watcher不再监听该节点子节点变化)…………………………………………………………………………………………");
////            List<String> children = zooKeeper.getChildren("/qqqq", childrenWatcher);
////            System.out.println("让dataWatcher监听 /GetChildren 节点本省的变化(默认watcher不再监听该节点变化)^^^^^^^^^^^^^^^^^^^^^");
////            byte[] data = zooKeeper.getData("/qqqq", dataWatcher, null);
//
//            List<ACL> acls = new ArrayList<>();
//
//            try {
//                ACL acl = new ACL(11, new Id("digest", DigestAuthenticationProvider.generateDigest("acl3:zzzxxx123")));
//                acls.add(acl);
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//
////            zooKeeper.create("/aaa/javaaclt","javat".getBytes(),acls,CreateMode.PERSISTENT);
//            zooKeeper.addAuthInfo("digest", "acl3:zzzxxx123".getBytes());
//            zooKeeper.create("/ccc/javaaclt", "javat".getBytes(), acls, CreateMode.CONTAINER);
//            zooKeeper.create("/ccc/javaaclt2", "javat".getBytes(), acls, CreateMode.CONTAINER);
//            zooKeeper.create("/ccc/javaaclt3", "javat".getBytes(), acls, CreateMode.CONTAINER);
//            List<ACL> acl = zooKeeper.getACL("/aaa/javaaclt", null);
//            System.out.println("acl列表=======================");
//            acl.forEach(System.out::println);
////            new ACL(ZooDefs.Perms.)
////            zooKeeper.setACL("/qqqq",)
////            children.forEach(System.out::println);
////            System.out.println("创建zk节点…………………………………………………………………………");
////            zooKeeper.create("/qqqq", "111111".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );
//
////            TimeUnit.SECONDS.sleep(1000000);
//        } catch (IOException | KeeperException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    /**
//     * 读取文件数据加入到map缓存中
//     *
//     * @throws IOException
//     */
//    @Test
//    public void readJsonData() throws IOException {
//        ClassPathResource resource = new ClassPathResource("znodeBackup.json");
//        File file = resource.getFile();
//        String jsonString = FileUtils.readFileToString(file, "UTF-8");
//
//        List<Znode> znodeList = JSONObject.parseArray(jsonString, Znode.class);
//        znodeList.forEach(System.out::println);
//
//
////        znodeList.forEach(System.out::println);
//
////        Set<String> keySet = jsonObject.keySet();
////        keySet.forEach(System.out::println);
////        for (String s : keySet) {
////
////            String stringArray = jsonObject.getJSONArray(s).toJSONString();
////            List<KeyValue> keyValues = JSONArray.parseArray(stringArray, KeyValue.class);
////            map.put(s, keyValues);
////        }
//    }
//
//    @Test
//    public void existZnode() {
//        Watcher watcher = new Watcher() {
//
//            @Override
//            public void process(WatchedEvent event) {
//                System.out.println("==========DefaultWatcher start==============");
//
//                System.out.println("DefaultWatcher state: " + event.getState().name());
//
//                System.out.println("DefaultWatcher type: " + event.getType().name());
//
//                System.out.println("DefaultWatcher path: " + event.getPath());
//
//                System.out.println("==========DefaultWatcher end==============");
//
//            }
//        };
//        try {
//            ZooKeeper zooKeeper = new ZooKeeper("172.17.250.22:2181", 50000, watcher);
//            Stat exists = zooKeeper.exists("/aaa", null);
//            System.out.println("exists111?????========" + JSONObject.toJSONString(exists));
//            Stat exists1 = zooKeeper.exists("/zzz", null);
//            if (exists1 != null) {
//                System.out.println("exists222?????========" + JSONObject.toJSONString(exists1));
//
//            } else {
//                System.out.println("查无此节点");
//            }
//        } catch (IOException | KeeperException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Test
//    public void createAClnode() {
//        Watcher watcher = new Watcher() {
//
//            @Override
//            public void process(WatchedEvent event) {
//                System.out.println("==========DefaultWatcher start==============");
//
//                System.out.println("DefaultWatcher state: " + event.getState().name());
//
//                System.out.println("DefaultWatcher type: " + event.getType().name());
//
//                System.out.println("DefaultWatcher path: " + event.getPath());
//
//                System.out.println("==========DefaultWatcher end==============");
//
//            }
//        };
//        List<ACL> aclList = new ArrayList<>();
//        try {
//            ACL acl = new ACL(11, new Id("digest", DigestAuthenticationProvider.generateDigest("acl:111111")));
//            aclList.add(acl);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        System.out.println(aclList.toString());
////        try {
////            ZooKeeper zooKeeper = new ZooKeeper("172.17.250.22:2181", 50000, watcher);
////            List<ACL> aclList = new ArrayList<>();
////            ACL acl = new ACL(11, new Id("digest", "acl:x");
////            aclList.add(acl);
////            zooKeeper.create("/zzz", "123".getBytes(), aclList,CreateMode.PERSISTENT)
////        } catch (IOException | KeeperException | InterruptedException e) {
////            e.printStackTrace();
////        }
//    }
//
//
//}
