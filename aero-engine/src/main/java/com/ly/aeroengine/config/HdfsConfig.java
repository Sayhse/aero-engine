package com.ly.aeroengine.config;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author ly
 * @Date 2024 03 13 16 00
 **/
@Configuration
public class HdfsConfig {
    /**
     * 配置hdfs地址
     */
    @Value("${hadoop.fs.defaultFS}")
    private String hdfsUri;

    /**
     * hadoop客户端身份
     */
    @Value("${hadoop.hdfs.identity}")
    private String clientIdentity;

    /**
     * 获取hadoop连接
     * @return
     */
    @Bean
    public org.apache.hadoop.conf.Configuration getConfiguration(){
        //设置客户端身份 以具备权限在hdfs上进行操作
        System.setProperty("HADOOP_USER_NAME",clientIdentity);

        //创建配置对象实例
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        //设置操作的文件系统是HDFS 并且指定HDFS操作地址
        configuration.set("fs.defaultFS",hdfsUri);
        return configuration;
    }

    /**
     * 获取FileSystem对象操作文件
     * @return
     * @throws IOException
     */
    @Bean
    public FileSystem getFileSystem() throws IOException {
        FileSystem fs = FileSystem.get(getConfiguration());
        return fs;
    }
}
