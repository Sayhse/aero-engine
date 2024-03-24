package com.ly.aeroengine.hdfs;

import org.apache.catalina.connector.Connector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Author ly
 * @Date 2024 03 10 22 56
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class HdfsClientTests {
    private static Configuration conf = null;
    private static FileSystem fs = null;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileSystem fileSystem;

    @Test
    public void fsMkdirTest() throws IOException {
        //首先判断文件夹是否存在 如果不存在再创建
        if (!fileSystem.exists(new Path("/teeeest"))){
            //创建文件夹
            fileSystem.mkdirs(new Path("/teeeest"));
        }
    }
    /**
     * 初始化方法 用于和hdfs建立连接
     * @throws IOException
     */
    @Before
    public  void connect2HDFS() throws IOException {
        //设置客户端身份 以具备权限在hdfs上进行操作
        System.setProperty("HADOOP_USER_NAME","root");

        //创建配置对象实例
        conf = new Configuration();
        //设置操作的文件系统是HDFS 并且指定HDFS操作地址
        conf.set("fs.defaultFS","hdfs://192.168.88.62:8020");
        //创建FileSystem对象实例
        fs = FileSystem.get(conf);
    }

    /**
     * 创建文件夹
     */
    @Test
    public void mkdir() throws IOException {
        //首先判断文件夹是否存在 如果不存在再创建
        if (!fs.exists(new Path("/teeeest"))){
            //创建文件夹
            fs.mkdirs(new Path("/teeeest"));
        }
    }

    /**
     * 上传文件
     */
    @Test
    public void putFile2HDFS() throws IOException {
        //创建本地文件路径 hdfs上传路径
        Path src = new Path("F:\\SanXiao\\SanXiaoProData\\1714_0_01.11.2022_12.58.34.csv");
        Path dst = new Path("/lyTest/1714_0_01.11.2022_12.58.34.csv");
        //文件上传动作
        fs.copyFromLocalFile(src,dst);
    }

    /**
     * 关闭客户端与hdfs的连接
     * @throws IOException
     */
    @After
    public void close(){
        //首先判断文件系统实例是否为null 如果不为null 进行关闭
        if (fs != null){
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRedisConnection() {
        // 设置测试键和值
        String key = "testKey";
        String value = "testValue";

        // 将键值对存储到Redis
        redisTemplate.opsForValue().set(key, value);

        // 从Redis获取值
        String retrievedValue = redisTemplate.opsForValue().get(key);

        // 检查获取的值是否与预期值相等
        System.out.println(retrievedValue);
    }
}
