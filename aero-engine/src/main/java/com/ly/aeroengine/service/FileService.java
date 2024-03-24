package com.ly.aeroengine.service;

import com.ly.aeroengine.entity.FileChunkRecord;
import com.ly.aeroengine.entity.bo.FileProcessBo;
import com.ly.aeroengine.entity.bo.ShardMetadataBo;
import com.ly.aeroengine.entity.request.FileProcessParam;
import com.ly.aeroengine.entity.request.MultipartFileParam;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author ly
 * @Date 2024 03 13 16 40
 **/
public interface FileService {

    /**
     * 创建文件目录，返回路径名
     * @param loginName
     * @return
     */
    Path mkdir(String loginName);

    /**
     * 将文件上传至服务器
     *
     * @param file
     * @param path
     * @return
     */
    String fileUpload2HDFS(MultipartFileParam file, Path path) throws IOException;

    ShardMetadataBo getShardMetadata(String userId);

    boolean fileUpload2Local(MultipartFileParam file, LocalDateTime arriveTime) throws IOException;

    List<FileChunkRecord> check(String md5);

    boolean preProcess(MultipartFileParam file);

    FileProcessBo processFile(FileProcessParam fileProcessParam);
}
