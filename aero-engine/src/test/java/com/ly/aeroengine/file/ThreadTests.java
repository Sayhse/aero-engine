package com.ly.aeroengine.file;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
/**
 * @Author ly
 * @Date 2024 03 15 22 53
 **/
public class ThreadTests {
        public static void main(String[] args) {
            // 获取当前线程管理器
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

            // 获取当前计算机上的线程数量
            int threadCount = threadMXBean.getThreadCount();
            System.out.println("活动线程个数：" + threadCount);

            // 获取当前计算机上的守护线程数量
            int daemonThreadCount = threadMXBean.getDaemonThreadCount();
            System.out.println("守护线程个数：" + daemonThreadCount);

            System.out.println("峰值线程个数：" + threadMXBean.getPeakThreadCount());
            System.out.println("总线程数：" + threadMXBean.getTotalStartedThreadCount());

            // 计算空闲线程数量
            int freeThreadCount = threadCount - daemonThreadCount;

            System.out.println("空闲线程数量：" + freeThreadCount);
            System.out.println("活动线程个数：" + Thread.activeCount());
            System.out.println("所有线程个数：" + Thread.getAllStackTraces().keySet().size());


        }
}
