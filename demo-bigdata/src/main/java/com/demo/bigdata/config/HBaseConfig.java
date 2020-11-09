package com.demo.bigdata.config;

import com.yundasys.ordercenter.hbase.service.HBaseSevice;
import com.yundasys.ordercenter.hbase.service.enums.OrderTableEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;
import java.io.IOException;

/**
 * 初始化连接
 * @Author maolixian
 * @Date 2020/9/15 15:39
 **/
@Slf4j
@Configuration
@DependsOn("apolloConfig")
public class HBaseConfig implements InitializingBean {
    @Value("${hbase.client.pause:200}")
    private String clientPause;
    @Value("${hbase.client.write.buffer:10485760}")
    private String writeBuffer;
    @Value("${hbase.client.retries.number:5}")
    private String retriesNumber;
    @Value("${hbase.zookeeper.property.clientPort:2181}")
    private String clientPort;
    @Value("${hbase.client.scanner.timeout.period:100000}")
    private String scannerTimeout;
    @Value("${hbase.rpc.timeout:40000}")
    private String rpcTimeout;
    @Value("${hbase.client.ipc.pool.type:RoundRobinPool}")
    private String poolType;
    @Value("${hbase.client.ipc.pool.size:5}")
    private String poolSize;
    @Value("${hbase.zookeeper.quorum:}")
    private String zkQuorum;
    @Value("${hbase.keytab.file.name:}")
    private String keytabFileName;
    @Value("${hbase.keytab.user.name:}")
    private String keytabUserName;
    @Value("${hbase.krb5.file.name:}")
    private String krb5FileName;
    @Value("${hbase.kerberos.switch:false}")
    private boolean isOpenKerberos;

    @Bean("hbaseConnection")
    public Connection createHBaseConnection() throws Exception {
        if (StringUtils.isEmpty(zkQuorum)) {
            if (StringUtils.isEmpty(zkQuorum)) {
                log.error("hbase.zookeeper.quorum配置不能为空");
                throw new Exception("hbase.zookeeper.quorum配置不能为空");
            }
        }
        if (isOpenKerberos) {
            System.setProperty("java.security.krb5.conf", krb5FileName);
        }
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, zkQuorum);
        if (isOpenKerberos) {
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(keytabUserName, keytabFileName);
        }
        conf.set(HConstants.HBASE_CLIENT_PAUSE, clientPause);
        conf.set("hbase.client.write.buffer", writeBuffer);
        conf.set(HConstants.HBASE_CLIENT_RETRIES_NUMBER, retriesNumber);
        conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, clientPort);
        conf.set(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, scannerTimeout);
        conf.set(HConstants.HBASE_RPC_TIMEOUT_KEY, rpcTimeout);
        conf.set(HConstants.HBASE_CLIENT_IPC_POOL_TYPE, poolType);
        conf.set(HConstants.HBASE_CLIENT_IPC_POOL_SIZE, poolSize);
        conf.set(HConstants.ZOOKEEPER_QUORUM, zkQuorum);
        return ConnectionFactory.createConnection(conf);
    }

    /**
     * 初始化
     * @author maolixian
     * date 2020/9/17 1:43
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        Connection hbaseConnection = createHBaseConnection();

        //如果初始化数据开关没有打开，则不初始化数
        if (!ApolloConfig.isIsInitData()) {
            return;
        }
        try (Admin admin = hbaseConnection.getAdmin()) {
            //检查表空间是否存在，如果不存在则创建
            boolean isExistNamespace = false;
            try {
                NamespaceDescriptor nsDesc = admin.getNamespaceDescriptor(ApolloConfig.getNamespace());
                //检查是否存在
                isExistNamespace = !StringUtils.isEmpty(nsDesc.getName());
            } catch (IOException e) {
                isExistNamespace = false;
            }
            //如果不存在则创建
            if (!isExistNamespace) {
                try {
                    admin.createNamespace(NamespaceDescriptor.create(ApolloConfig.getNamespace()).build());
                    isExistNamespace = true;
                } catch (IOException e) {
                    isExistNamespace = false;
                }
            }
            //如果表空间存在或者创建成功，继续进行表创建工作
            if (isExistNamespace) {
                for (OrderTableEnum orderTable : OrderTableEnum.values()) {
                    if (!StringUtils.isEmpty(orderTable.getTableName())) {
                        TableName table = HBaseSevice.getTableName(ApolloConfig.getNamespace(), orderTable.getTableName());
                        //如果表不存在
                        if (!admin.tableExists(table)) {
                            // 通过HTableDescriptor 类描述一张表
                            HTableDescriptor tableDescriptor = new HTableDescriptor(table);
                            //创建一个主列族
                            HColumnDescriptor mainFamily = new HColumnDescriptor(orderTable.getMainFamily());
                            mainFamily.setMaxVersions(ApolloConfig.getMaxVersions());
                            mainFamily.setTimeToLive(ApolloConfig.getTimeToLive());
                            tableDescriptor.addFamily(mainFamily);
                            //创建一个扩展列族
                            if (!StringUtils.isEmpty(orderTable.getExtendFamily1())) {
                                HColumnDescriptor extendFamily1 = new HColumnDescriptor(orderTable.getExtendFamily1());
                                extendFamily1.setMaxVersions(ApolloConfig.getMaxVersions());
                                extendFamily1.setTimeToLive(ApolloConfig.getTimeToLive());
                                tableDescriptor.addFamily(extendFamily1);
                            }

                            admin.createTable(tableDescriptor, ApolloConfig.getSplitKeys());
                        }
                    }
                }
            }
        } catch (Exception e) {
            HBaseSevice.sendErrorLog("调用init方法异常", e);
        }
    }
}
