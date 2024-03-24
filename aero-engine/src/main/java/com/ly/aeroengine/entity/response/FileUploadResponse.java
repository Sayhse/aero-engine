package com.ly.aeroengine.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.hadoop.fs.Path;

/**
 * @Author ly
 * @Date 2024 03 20 12 05
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String msg;
    private Path hdfsPath;
}
