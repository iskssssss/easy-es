package top.kongsheng.common.es.core;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化的Function
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/12 17:15
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
}
