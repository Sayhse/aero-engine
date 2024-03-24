package com.ly.aeroengine.pythonTests;

import java.io.IOException;

/**
 * @Author ly
 * @Date 2024 03 23 17 50
 **/
public class Test01 {
    public static void main(String[] args) throws IOException, InterruptedException {
        String pythonScriptPath = "src/main/resources/preprocess.py";
        String file_path_from = "F:\\SanXiao\\SanXiaoProData\\ABD_300rpm_r12.csv";

        String file_path_to = "F:\\SanXiao\\SanXiaoProData\\processed_file.csv";

        Process process = Runtime.getRuntime().exec("python " + pythonScriptPath + " " + file_path_from + " " + file_path_to);

        int wait = process.waitFor();

        System.exit(wait);
    }
}
