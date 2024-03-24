package com.ly.aeroengine.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ly
 * @Date 2024 03 14 19 50
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShardMetadataBo {
    /**
     * 分块大小
     */
    private long shardSize;
    /**
     * 并发数  即前端for循环次数
     */
    private int concurrency;
}
