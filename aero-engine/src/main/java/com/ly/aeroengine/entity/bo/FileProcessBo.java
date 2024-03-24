package com.ly.aeroengine.entity.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author 徐晨晨
 * @since 2024-03-24
 */
@Data
@Builder
public class FileProcessBo {
    /**
     * 处理结果
     */
    private String msg;
}
