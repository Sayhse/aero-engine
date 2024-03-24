package com.ly.aeroengine.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ly
 * @Date 2024 03 14 19 46
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShardMetadataResponse {
    private long shardSize;
    private int concurrency;
}
