package com.ly.aeroengine.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author ly
 * @Date 2024 03 14 16 55
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FileChunkRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String md5;
    private Integer chunk;
    /**
     * 0-fail, 1-okay
     */
    private String uploadStatus;
    /**
     * 0-fail, 1-okay
     */
    private String isDeleted;

}
