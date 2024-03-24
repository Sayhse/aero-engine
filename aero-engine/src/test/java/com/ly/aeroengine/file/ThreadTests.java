package com.ly.aeroengine.file;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.util.Records;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
/**
 * @Author ly
 * @Date 2024 03 15 22 53
 **/
public class ThreadTests {
        public static void main(String[] args) {
            String preAppId = "application_1634567890000_0000012345";
            String[] split = preAppId.split("_");
            String s = split[split.length - 1];
            // 正则表达式匹配非数字字符
            String[] parts = s.split("[^\\d]");
            // 假设字符串末尾有数字
            int lastIndex = parts.length - 1;
            // 将数字字符串转换成整数并加一
            int numericSuffix = Integer.parseInt(parts[lastIndex]) + 1;
            // 移除末尾数字，并将新的数字加入
            parts[lastIndex] = String.valueOf(numericSuffix);
            // 重新拼接字符串
            split[split.length - 1] = String.join("", parts);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                stringBuilder.append(split[i]);
                if (i != (split.length - 1)){
                    stringBuilder.append("_");
                }
            }
            String realAppId = stringBuilder.toString();

            ApplicationId applicationId = ApplicationId.fromString(realAppId);
            System.out.println(applicationId);
        }
}
