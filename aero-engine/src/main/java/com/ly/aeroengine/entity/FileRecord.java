package com.ly.aeroengine.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author ly
 * @Date 2024 03 14 15 11
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FileRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String fileName;
    private String fileMd5;
    private String filePath;
    private String fileSize;
    /**
     * 0-fail, 1-okay
     */
    private Integer uploadStatus;
    private Date createTime;
    private Date updateTime;

}
