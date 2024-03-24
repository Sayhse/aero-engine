package com.ly.aeroengine.entity.request;

import lombok.Data;

/**
 * @author 徐晨晨
 * @since 2024-03-24
 */
@Data
public class FileProcessParam {
    /**
     * 文件在hdfs中的存储路径
     */
    private String hdfsPath;
}
