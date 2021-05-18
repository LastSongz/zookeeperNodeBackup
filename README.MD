该备份工具用于备份以及恢复zookeeper的节点以及数据

# 使用方法

1、部署到linux后，查看当前机器的/etc/profile文件中jdk环境变量，编辑bin/start.sh,修改jdk环境变量配置为当前机器的jdk环境变量，vi 启动脚本后用‘set ff=unix’设置脚本格式

2、按照conf/sampleConfig.json的规范配置待备份集群信息

```json
[
    {
      "connectPath":"ip:port",    //zk连接地址，仅支持连接一个zk节点
      "saveFilePath":"/path/to/save/znode",  //备份时此处填存储备份的文件夹目录，恢复znode时此处填写备份文件的路径
      "acl": [                           //acl认证信息列表，若无则将此键值对删除
        {
            "acl": "schema:user:passwd"  
          },
          {
            "acl": "digest:acl2:222222"
          },
          {
            "acl": "digest:acl3:zzzxxx123"
          }
      ],
      "sslClientCfg": "/path/to/sslClientCfg"   //ssl连接配置文件，若无则将此键值对删除
    },
    {
        "connectPath": "172.28.222.4:2181",
        "saveFilePath": "C:\\Users\\KaiXuan\\Work\\smallTools\\zookeeperNodeBackup\\src\\main\\resources\\znodeBackup111.json",
        "acl": [
          {
            "acl": "digest:acl:111111"
          },
          {
            "acl": "digest:acl2:222222"
          },
          {
            "acl": "digest:acl3:zzzxxx123"
          }
        ]
    }

]
```

3、ssl连接配置文件规范

```properties
zookeeper.clientCnxnSocket=org.apache.zookeeper.ClientCnxnSocketNetty
zookeeper.client.secure=true
zookeeper.ssl.keyStore.location=src\main\resources\aaaaaaaa\testsslkeystore.jks
zookeeper.ssl.keyStore.password=d40gJi53
zookeeper.ssl.trustStore.location=src\main\resources\aaaaaaaa\testssltruststore.jks
zookeeper.ssl.trustStore.password=d40gJi53
```

4、根据需求修改conf/application.yaml文件，将zk集群信息文件位置填入

5、使用bin/start.sh启动备份服务