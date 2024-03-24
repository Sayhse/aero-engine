package com.ly.aeroengine.yarn;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Records;

import java.io.IOException;
import java.util.Collections;

/**
 * @Author ly
 * @Date 2024 03 20 19 26
 **/
@Slf4j
public class YarnTests {
    public static void main(String[] args) {
        System.setProperty("HADOOP_USER_NAME","root");
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.set("yarn.resourcemanager.hostname","192.168.88.62");
        yarnConfiguration.set("yarn.nodemanager.hostname","192.168.88.62");
        yarnConfiguration.set("yarn.nodemanager.hostname","192.168.88.63");
        yarnConfiguration.set("yarn.nodemanager.hostname","192.168.88.64");
        yarnConfiguration.set("yarn.nodemanager.aux-services","mapreduce_shuffle");

        //创建yarnClient实例
        try (YarnClient yarnClient = YarnClient.createYarnClient()) {
            yarnClient.init(yarnConfiguration);
            //启动yarnClient
            yarnClient.start();
            //创建YarnClientApplication实例
            YarnClientApplication application = yarnClient.createApplication();
            GetNewApplicationResponse newApplicationResponse = application.getNewApplicationResponse();
            // 设置应用程序名称和队列
            ApplicationSubmissionContext appContext = application.getApplicationSubmissionContext();
            ApplicationId appId = appContext.getApplicationId();
            appContext.setApplicationName("MyYarnApplication");
            appContext.setQueue("default");
            appContext.setApplicationType("MAPREDUCE");

            ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);
            amContainer.setCommands(Collections.singletonList("$HADOOP_HOME/bin/hadoop jar /export/servers/jar_test/mapreduce-1.0-SNAPSHOT.jar com.ly.mapreduce.JobMain"));

            appContext.setAMContainerSpec(amContainer);

            // 设置资源请求
            Resource resource = Records.newRecord(Resource.class);
            resource.setMemorySize(1024); // 设置内存资源大小
            resource.setVirtualCores(1); // 设置虚拟核心数

            appContext.setResource(resource);
            yarnClient.submitApplication(appContext);
            // 打印应用程序ID
            System.out.println("Submitted YARN application. ApplicationId: " + appId);
            ApplicationReport report = yarnClient.getApplicationReport(appId);
            log.info("Got application report " +
                    ", clientToAMToken=" + report.getClientToAMToken()
                    + ", appDiagnostics=" + report.getDiagnostics()
                    + ", appMasterHost=" + report.getHost()
                    + ", appQueue=" + report.getQueue()
                    + ", appMasterRpcPort=" + report.getRpcPort()
                    + ", appStartTime=" + report.getStartTime()
                    + ", yarnAppState=" + report.getYarnApplicationState().toString()
                    + ", distributedFinalState=" + report.getFinalApplicationStatus().toString()
                    + ", appTrackingUrl=" + report.getTrackingUrl()
                    + ", appUser=" + report.getUser());
            yarnClient.stop();
        } catch (YarnException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
