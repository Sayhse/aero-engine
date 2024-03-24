package com.ly.aeroengine.enums;

import lombok.Getter;

/**
 * @Author ly
 * @Date 2024 03 13 16 51
 **/
public enum FileResultCodeEnum implements IResultCodeEnum{
    FOLDER_CREATE_FAILED("FOLDER_CREATE_FAILED","文件夹创建失败",4000000),
    FILE_HAS_BEEN_UPLOADED("FILE_HAS_BEEN_UPLOADED","文件已经上传过！",4000001),
    FILE_IS_EMPTY("FILE_IS_EMPTY","上传文件为空！",4000002),
    FILE_PREPROCESS_ERROR("FILE_PREPROCESS_ERROR","文件预处理失败！请重试！",4000003),
    FILE_PROCESS_ERROR("FILE_PROCESS_ERROR","文件处理出错！请检查文件格式！",4000004),
    FILE_PROGRAM_RUDE_ABORT("FILE_PROGRAM_RUDE_ABORT","文件处理程序被强制中止！",4000004),
    ;

    /** 枚举标识name */
    @Getter
    private final String name;

    /** 枚举详情 */
    private final String message;

    /** 枚举编号code */
    private final Integer code;


    /**
     * 构造方法
     * @param name
     * @param message
     * @param code
     */
    FileResultCodeEnum(String name, String message, Integer code) {
        this.name = name;
        this.message = message;
        this.code = code;
    }

    @Override
    public Integer code() {
        return null;
    }

    @Override
    public String message() {
        return null;
    }
}
