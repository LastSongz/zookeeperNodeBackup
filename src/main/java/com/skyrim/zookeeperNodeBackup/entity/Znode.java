package com.skyrim.zookeeperNodeBackup.entity;

import lombok.Data;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by Skyrim on 2021/5/14 14:52
 */
@Data
public class Znode {
    String path;
    Stat Stat;
    String data;
    List<ACL> acl;

}
