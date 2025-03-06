package org.example.crm.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 企业项目开发的常用做法
 *
 * 采用R类封装后端返回的结果（不管后端返回什么数据，都用R类来封装）
 *
 * 这样可以使后端的返回数据结构是统一的
 */
@Slf4j //日志注解
@Data
@Builder //生成构建器模式的代码,这样可以让我们可以采用链式编程创建对象
@NoArgsConstructor //无参构造器
@AllArgsConstructor //所有参数的构造器
public class R {
    private int code;//结果码（如200/500）
    private String msg;// 结果信息（成功了？还是失败了？）
    public Object info;//结果数据，结果类型可能是String ，也可能是一个User对象，也可能是一个List
    public static R OK(Object data) {
        return R.builder().code(200).msg("成功").info(data).build();
    }
    public static R FAIL(Object data) {
        return R.builder().code(500).msg("失败").info(data).build();
    }
}