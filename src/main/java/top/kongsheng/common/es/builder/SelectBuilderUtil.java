package top.kongsheng.common.es.builder;

import top.kongsheng.common.es.core.SearchQuery;

/**
 * 查询构建器工具类
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2024/2/23 14:33
 */
public class SelectBuilderUtil {

    /**
     * lambda方式查询构建
     *
     * @param modelClass 类
     * @param <T>        类型
     * @return 构建器
     */
    public static <T> LambdaSelectBuilder<T> lambdaSelect(Class<? extends T> modelClass) {
        return lambdaSelect(modelClass, new SearchQuery());
    }

    /**
     * lambda方式查询构建
     *
     * @param modelClass  类
     * @param searchQuery 多条件搜索
     * @param <T>         类型
     * @return 构建器
     */
    public static <T> LambdaSelectBuilder<T> lambdaSelect(Class<? extends T> modelClass, SearchQuery searchQuery) {
        return new LambdaSelectBuilder<>(modelClass, searchQuery);
    }

    /**
     * 查询构建
     *
     * @param modelClass 类
     * @param <T>        类型
     * @return 构建器
     */
    public static <T> SelectBuilder<T> select(Class<? extends T> modelClass) {
        return select(modelClass, new SearchQuery());
    }

    /**
     * 查询构建
     *
     * @param modelClass  类
     * @param searchQuery 多条件搜索
     * @param <T>         类型
     * @return 构建器
     */
    public static <T> SelectBuilder<T> select(Class<? extends T> modelClass, SearchQuery searchQuery) {
        return new SelectBuilder<>(modelClass, searchQuery);
    }
}
