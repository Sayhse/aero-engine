package com.ly.aeroengine.entity.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.time.LocalDateTime;

/**
 * @Author ly
 * @Date 2024 03 14 14 38
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MultipartFileParam {
    /**
     * 是否分片
     */
    @NotNull(message = "是否分片不能为空")
    private boolean chunkFlag;

    /**
     * 当前为第几块分片
     */
    private int chunk;

    /**
     * 文件总大小, 单位是byte
     */
    private long totalSize;

    /**
     * 当前分片文件起始地址索引，单位是byte
     */
    private long beginByteIndex;

    /**
     *当前分片文件结束地址索引
     */
    private long endByteIndex;

    /**
     * 第几次for并发上传
     */
    private int forCount;

    /**
     * 是否为最后分片文件 0-不是，1-是
     */
    private int lastChunkFlag;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 本次分块大小
     */
    private long shardSize;

    /**
     * 本次并发量
     */
    private int concurrency;

    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能为空")
    private String name;

    /**
     * 文件
     */
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    /**
     * md5值
     */
    @NotBlank(message = "文件md5值不能为空")
    private String md5;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String loginName;
}
