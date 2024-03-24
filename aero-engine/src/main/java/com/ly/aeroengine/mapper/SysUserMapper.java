package com.ly.aeroengine.mapper;

import com.ly.aeroengine.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author ly
 * @since 2024-03-12
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser queryByUserName(String username);

    SysUser queryByPhone(String phone);

    @Insert("insert into sys_user(`user_id`,`user_name`,`name`,`phone`,`password`,`create_time`) values(#{userId},#{userName},#{name},#{phone},#{password},#{createTime})")
    int add(SysUser sysUser);
}
