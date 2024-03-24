package com.ly.aeroengine.yarn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Records;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @Author ly
 * @Date 2024 03 22 17 04
 **/
public class YarnTest01 {
    public static void main(String[] args) throws IOException, YarnException {
        System.setProperty("HADOOP_USER_NAME","root");
        Configuration conf = new Configuration();
        conf.set("yarn.resourcemanager.hostname","192.168.88.62");
        conf.set("yarn.nodemanager.hostname","192.168.88.62");
        conf.set("yarn.nodemanager.hostname","192.168.88.63");
        conf.set("yarn.nodemanager.hostname","192.168.88.64");
        conf.set("yarn.nodemanager.aux-services","mapreduce_shuffle");
        YarnClient yarnClient = YarnClient.createYarnClient();

        yarnClient.init(conf);
        yarnClient.start();

        // 提交应用程序
        ApplicationId appId = yarnClient.createApplication().getApplicationSubmissionContext().getApplicationId();
        yarnClient.submitApplication(createApplicationSubmissionContext(appId));

        // 获取应用程序报告
        List<ApplicationReport> appReports = yarnClient.getApplications(EnumSet.of(YarnApplicationState.RUNNING));
        for (ApplicationReport report : appReports) {
            System.out.println("Application ID: " + report.getApplicationId());
            System.out.println("Application State: " + report.getYarnApplicationState());
            // 可以获取更多应用程序报告的信息
        }

        // 关闭 YarnClient
        yarnClient.stop();
    }
    private static ApplicationSubmissionContext createApplicationSubmissionContext(ApplicationId appId) {

        ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);
        amContainer.setCommands(Collections.singletonList("$HADOOP_HOME/bin/hadoop jar /export/servers/jar_test/mapreduce-1.0-SNAPSHOT.jar org.ly.mapreduce.JobMain"));


        // 创建一个 ResourceRequest 对象
        ResourceRequest resourceRequest = ResourceRequest.newInstance(Priority.newInstance(1), "hadoop102", Resource.newInstance(1024, 1), 3);
        ApplicationSubmissionContext appSubmissionContext = ApplicationSubmissionContext.newInstance(appId, "MyApplication", "default",amContainer,true,true,2,"MAPREDUCE",false,"test",resourceRequest);

        // 设置应用程序所需的资源（例如，内存和 vCPU）
        appSubmissionContext.setResource(Resource.newInstance(1024, 1));

        // 设置应用程序的主类
        appSubmissionContext.setApplicationName("com.example.MyApplication");

        // 可以设置更多应用程序的配置和参数

        return appSubmissionContext;
    }
}
