package com.ly.aeroengine.rest;

import com.ly.aeroengine.entity.FileChunkRecord;
import com.ly.aeroengine.entity.bo.ShardMetadataBo;
import com.ly.aeroengine.entity.request.MultipartFileParam;
import com.ly.aeroengine.entity.response.FileUploadResponse;
import com.ly.aeroengine.entity.response.ShardMetadataResponse;
import com.ly.aeroengine.result.Result;
import com.ly.aeroengine.service.FileService;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author ly
 * @Date 2024 03 13 16 15
 **/
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(MultipartFileParam file) throws IOException {
        LocalDateTime arriveTime = LocalDateTime.now();
        boolean fileUpload2Local = fileService.fileUpload2Local(file, arriveTime);
        FileUploadResponse response = null;
        if (fileUpload2Local){
            boolean process = fileService.preProcess(file);
            if (process){
                Path path = fileService.mkdir(file.getLoginName());
                String result = fileService.fileUpload2HDFS(file, path);
                response = new FileUploadResponse(result, path);
            }
        }
        return Result.ok(response);
    }

    /**
     * 预处理 拿取分块大小和并发量
     * @param userId
     * @return
     */
    @PostMapping("/shard/metadata")
    public Result<ShardMetadataResponse> shardMetadata(@RequestParam("userId") String userId) {
        ShardMetadataBo shardMetadata = fileService.getShardMetadata(userId);
        ShardMetadataResponse response = new ShardMetadataResponse(
                shardMetadata.getShardSize(), shardMetadata.getConcurrency());
        return Result.ok(response);
    }
    /**
     * 秒传检测
     * @param md5
     * @return
     */
    @PostMapping("/check")
    public Result<List> check(@RequestParam("md5") String md5) {
        List<FileChunkRecord> check = fileService.check(md5);
        return Result.ok(check);
    }
}
