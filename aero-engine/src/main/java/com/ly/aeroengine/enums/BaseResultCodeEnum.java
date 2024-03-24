package com.ly.aeroengine.enums;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 结果码枚举
 * @author ly
 */
public enum BaseResultCodeEnum implements IResultCodeEnum {

    /** 执行成功：0，执行失败：非0 */
    SUCCESS("SUCCESS123", "执行成功", 0),

    /** 系统异常*/
    SYSTEM_ERROR("SYSTEM_ERROR", "系统异常", 10000001),

    /** 外部接口调用异常*/
    INTERFACE_SYSTEM_ERROR("INTERFACE_SYSTEM_ERROR", "外部接口调用异常，请联系管理员！", 10000003),

    /** 业务连接处理超时 */
    CONNECT_TIME_OUT("CONNECT_TIME_OUT", "系统超时，请稍后再试!", 10000004),

    /** 系统错误 */
    SYSTEM_FAILURE("SYSTEM_FAILURE", "系统错误", 10000005),

    /** 参数为空 */
    NULL_ARGUMENT("NULL_ARGUMENT", "参数为空", 10000006),

    /** 非法参数 */
    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "非法参数", 10000007),

    /** 非法配置 */
    ILLEGAL_CONFIGURATION("ILLEGAL_CONFIGURATION", "非法配置" , 10000008),

    /** 非法状态 */
    ILLEGAL_STATE("ILLEGAL_STATE", "非法状态", 10000009),

    /** 错误的枚举编码 */
    ENUM_CODE_ERROR("ENUM_CODE_ERROR", "错误的枚举编码", 10000010),

    /** 逻辑错误 */
    LOGIC_ERROR("LOGIC_ERROR", "逻辑错误", 10000011),

    /**并发异常*/
    CONCURRENT_ERROR("CONCURRENT_ERROR", "并发异常", 10000012),

    /** 非法操作 */
    ILLEGAL_OPERATION("ILLEGAL_OPERATION", "非法操作", 10000013),

    /** 重复操作 */
    REPETITIVE_OPERATION("REPETITIVE_OPERATION", "重复操作", 10000014),

    /** 无操作权限 */
    NO_OPERATE_PERMISSION("NO_OPERATE_PERMISSION", "无操作权限", 10000015),

    /** 数据异常 */
    DATA_ERROR("DATA_ERROR", "数据异常", 10000016),

    /** 链接异常 */
    CONN_ERROR("CONN_ERROR", "连接异常", 10000017),

    /** 类型不匹配 */
    TYPE_MISMATCH("TYPE_MISMATCH", "类型不匹配", 10000018),

    /** 对象类型不匹配 */
    CLASS_MISMATCH("CLASS_MISMATCH", "对象类型不匹配", 10000019),

    /** 文件不存在 */
    FILE_NOT_EXIST("FILE_NOT_EXIST", "文件不存在", 10000020),

    /** 被限制的调用 */
    INVOKE_IS_REJECT("INVOKE_IS_REJECT", "被限制的调用[{0}]", 10000021);

    /** 枚举标识name */
    @Getter
    private final String name;

    /** 枚举详情 */
    private final String message;

    /** 枚举编号code */
    private final Integer code;

    /**
     * 构造方法
     *
     * @param name         枚举标识
     * @param message      枚举信息
     * @param code        枚举编号（编码）
     */
    BaseResultCodeEnum(String name, String message, int code) {
        this.name = name;
        this.message = message;
        this.code = code;
    }

    /**
     * 通过枚举<name>name</name>获得枚举。
     *
     * @param code         枚举标识
     */
    public static BaseResultCodeEnum getResultCodeEnumByCode(String code) {
        if (StringUtils.isNotBlank(code)) {
            for (BaseResultCodeEnum param : values()) {
                if (param.name.equals(code)) {
                    return param;
                }
            }
        }
        return null;
    }

    /**
     * @see IResultCodeEnum#message()
     */
    @Override
    public String message() { return message; }

    /**
     * @see IResultCodeEnum#code()
     */
    @Override
    public Integer code() { return code; }

}