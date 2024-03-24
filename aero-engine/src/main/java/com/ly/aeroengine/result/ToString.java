package com.ly.aeroengine.result;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * ToString基类
 * <pre>
 *  如果作为入参对象继承该类时，请重写toString方法，打印摘要日志会调用
 * </pre>
 *
 * @author ly
 */
public class ToString implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7277110490057966382L;

    /**
     * 重写toString方法
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
