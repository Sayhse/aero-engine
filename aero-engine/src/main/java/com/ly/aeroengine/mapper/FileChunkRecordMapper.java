package com.ly.aeroengine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.aeroengine.entity.FileChunkRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author ly
 * @Date 2024 03 14 16 55
 **/
@Mapper
public interface FileChunkRecordMapper extends BaseMapper<FileChunkRecord> {
    @Delete("update file_chunk_record set is_deleted = '1' where md5 = #{md5}")
    int deleteByMd5(@Param("md5") String md5);

    List<FileChunkRecord> queryByMd5(String md5);
}
