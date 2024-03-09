package top.kongsheng.common.es.core;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import top.kongsheng.common.es.enums.ConditionEnum;
import top.kongsheng.common.es.enums.OpEnum;
import top.kongsheng.common.es.item.BaseQueryItem;
import top.kongsheng.common.es.utils.type.ClassType;
import top.kongsheng.common.es.utils.type.ClassTypeUtil;
import org.elasticsearch.index.query.*;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static top.kongsheng.common.es.enums.ConditionEnum.IS_EMPTY;
import static top.kongsheng.common.es.enums.ConditionEnum.IS_NOT_EMPTY;
import static top.kongsheng.common.es.enums.OpEnum.*;

/**
 * es查询工具类
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/13 15:42
 */
public class ElasticsearchCore extends SqlUtil {

    /**
     * 创建布尔查询构建器
     *
     * @return 布尔查询构建器
     */
    public static BoolQueryBuilder createQueryBuilder() {
        return new BoolQueryBuilder();
    }

    /**
     * 处理查询信息
     *
     * @param searchQuery 搜索条件
     * @return es查询构建器
     */
    public static QueryBuilder handleSearchQuery(SearchQuery searchQuery) {
        if (null == searchQuery) {
            return null;
        }
        List<BaseQueryItem> items = searchQuery.getItems();
        return handleSearchQuery(items, true);
    }

    /**
     * 处理查询信息
     *
     * @param items               条件项列表
     * @param addBoolQueryBuilder 是否在结果外包一层布尔构建器
     * @return es查询构建器
     */
    private static QueryBuilder handleSearchQuery(List<BaseQueryItem> items, boolean addBoolQueryBuilder) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        Collection<QueryBuilder> mustQueryBuilderList = new LinkedList<>();
        int size = items.size();
        BoolQueryBuilder must = createQueryBuilder();
        for (int i = 0; i < size; i++) {
            BaseQueryItem current = items.get(i);
            if (!current.isValue()) {
                continue;
            }
            OpEnum nextOpEnum = AND, currentOpEnum = OpEnum.to(current.getOp());
            boolean last = i == size - 1;
            if (!last) {
                BaseQueryItem nextQueryItem = items.get(i + 1);
                nextOpEnum = OpEnum.to(nextQueryItem.getOp());
            }
            List<BaseQueryItem> children = current.getChildren();
            boolean childrenEmpty = children == null || children.isEmpty();
            QueryBuilder queryBuilder = childrenEmpty ? handleCondition(current, ConditionEnum.to(current.getCondition())) : handleSearchQuery(children, false);
            queryBuilder = currentOpEnum == NOT ? createQueryBuilder().mustNot(queryBuilder) : queryBuilder;
            // 是否初始化{must}数据
            boolean init = nextOpEnum == OR || last;
            // 当不存在条件并且需初始化时直接返回其本身，反之添加至{must}中。
            QueryBuilder addQueryBuilder = must.must().isEmpty() && init ? queryBuilder : must.must(queryBuilder);
            if (!init) {
                continue;
            }
            mustQueryBuilderList.add(addQueryBuilder);
            must = createQueryBuilder();
        }
        if (mustQueryBuilderList.isEmpty()) {
            return null;
        }
        if (mustQueryBuilderList.size() > 1) {
            BoolQueryBuilder result = createQueryBuilder();
            mustQueryBuilderList.forEach(result::should);
            return result;
        }
        QueryBuilder firstQueryBuilder = mustQueryBuilderList.iterator().next();
        if (firstQueryBuilder instanceof BoolQueryBuilder || !addBoolQueryBuilder) {
            return firstQueryBuilder;
        }
        return createQueryBuilder().must(firstQueryBuilder);
    }

    /**
     * 处理查询关键字
     *
     * @param queryItem     条件项
     * @param conditionEnum 查询关键字
     * @return 查询构建器
     */
    private static QueryBuilder handleCondition(BaseQueryItem queryItem, ConditionEnum conditionEnum) {
        String name = queryItem.getName();
        Object[] values = queryItem.getValues();
        boolean valuesEmpty = values == null || values.length < 1;
        if (conditionEnum != IS_EMPTY && conditionEnum != IS_NOT_EMPTY && valuesEmpty) {
            throw new IllegalArgumentException("参数错误，(" + name + ")参数的值不可为空。");
        }
        Object firstValue = valuesEmpty ? null : values[0];
        int length = valuesEmpty ? 0 : values.length;
        name = appendKeyword(queryItem);
        switch (conditionEnum) {
            case GT:
            case GE:
            case LT:
            case LE:
                return rangeHandle(conditionEnum, name, values);
            case LIKE:
                return likeHandle(name, values, "*", "*");
            case LIKE_LEFT:
                return likeHandle(name, values, "*", "");
            case LIKE_RIGHT:
                return likeHandle(name, values, "", "*");
            case BETWEEN:
                BoolQueryBuilder betweenQueryBuilder = createQueryBuilder();
                addBetweenDate(betweenQueryBuilder, name, values);
                return betweenQueryBuilder;
            case IS_EMPTY:
                BoolQueryBuilder notExistsQuery = createQueryBuilder();
                notExistsQuery.should(createQueryBuilder().mustNot(QueryBuilders.existsQuery(name)));
                notExistsQuery.should(createQueryBuilder().must(QueryBuilders.termQuery(name, "")));
                return createQueryBuilder().must(notExistsQuery);
            case IS_NOT_EMPTY:
                BoolQueryBuilder existsQuery = createQueryBuilder();
                existsQuery.must(QueryBuilders.existsQuery(name));
                existsQuery.mustNot(QueryBuilders.termQuery(name, ""));
                return createQueryBuilder().must(existsQuery);
            case MATCH:
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(name, firstValue);
                matchQueryBuilder.analyzer("hanlp_index");
                return matchQueryBuilder;
            case MATCH_PHRASE:
                MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(name, firstValue);
                matchPhraseQueryBuilder.analyzer("hanlp_index");
                return matchPhraseQueryBuilder;
            case IN:
                if (length > 1) {
                    BoolQueryBuilder flgBool = ElasticsearchCore.createQueryBuilder();
                    for (Object value : values) {
                        flgBool.should(QueryBuilders.termQuery(name, value));
                    }
                    return createQueryBuilder().must(flgBool);
                }
                return QueryBuilders.termQuery(name, firstValue);
            case NOT_IN:
                if (length > 1) {
                    BoolQueryBuilder flgBool = createQueryBuilder();
                    for (Object value : values) {
                        flgBool.should(QueryBuilders.termQuery(name, value));
                    }
                    return createQueryBuilder().mustNot(flgBool);
                }
                return QueryBuilders.termQuery(name, firstValue);
            case NE:
                if (length > 1) {
                    return createQueryBuilder().mustNot(QueryBuilders.termQuery(name, values));
                }
                return createQueryBuilder().mustNot(QueryBuilders.termQuery(name, firstValue));
            case EQ:
            default:
                if (length > 1) {
                    return QueryBuilders.termQuery(name, values);
                }
                return QueryBuilders.termQuery(name, firstValue);
        }
    }

    /**
     * 处理范围查询关键字
     *
     * @param conditionEnum 查询关键字
     * @param name          名字
     * @param values        值列表
     * @return 结果
     */
    private static BoolQueryBuilder rangeHandle(ConditionEnum conditionEnum, String name, Object[] values) {
        if (values == null || values.length < 1) {
            return null;
        }
        Object value = values[0];
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        Object dateTime = value;
        if (!(value instanceof Date)) {
            dateTime = DateUtil.parse(value.toString());
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
        switch (conditionEnum) {
            case GT:
                rangeQueryBuilder.gt(dateTime);
                break;
            case GE:
                rangeQueryBuilder.gte(dateTime);
                break;
            case LT:
                rangeQueryBuilder.lt(dateTime);
                break;
            case LE:
                rangeQueryBuilder.lte(dateTime);
                break;
            default:
                return null;
        }
        return createQueryBuilder().must(rangeQueryBuilder);
    }

    /**
     * 处理模糊查询关键字
     *
     * @param name   名字
     * @param values 值列表
     * @param prefix 开始
     * @param suffix 后缀
     * @return 结果
     */
    private static QueryBuilder likeHandle(String name, Object[] values, String prefix, String suffix) {
        BoolQueryBuilder likeQueryBuilder = createQueryBuilder();
        if (values.length > 1) {
            for (Object value : values) {
                likeQueryBuilder.must(QueryBuilders.wildcardQuery(name, prefix + toValueStr(name, value) + suffix));
            }
            return likeQueryBuilder;
        }
        return QueryBuilders.wildcardQuery(name, prefix + toValueStr(name, values[0]) + suffix);
    }

    /**
     * 添加时间范围条件
     *
     * @param booleanQueryBuilder 条件构建器
     * @param name                名称
     * @param values              值
     */
    public static void addBetweenDate(BoolQueryBuilder booleanQueryBuilder, String name, Object... values) {
        if (values == null || values.length < 1) {
            return;
        }
        Object beginTime = values[0], endTime = values.length > 1 ? values[1] : null;
        boolean beginTimeEmpty = ObjUtil.isEmpty(beginTime);
        boolean endTimeEmpty = ObjUtil.isEmpty(endTime);
        if (beginTimeEmpty && endTimeEmpty) {
            return;
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
        if (!beginTimeEmpty) {
            Object dateTime = beginTime;
            if (!(beginTime instanceof Date)) {
                dateTime = DateUtil.parse(beginTime.toString());
            }
            rangeQueryBuilder.gte(dateTime);
        }
        if (!endTimeEmpty) {
            Object dateTime = endTime;
            if (!(beginTime instanceof Date)) {
                dateTime = DateUtil.parse(endTime.toString());
            }
            rangeQueryBuilder.lte(dateTime);
        }
        booleanQueryBuilder.must(rangeQueryBuilder);
    }

    /**
     * 将值转换成字符串
     *
     * @param name  名字
     * @param value 值
     * @return 值
     */
    private static String toValueStr(String name, Object value) {
        if (ObjUtil.isEmpty(value)) {
            throw new IllegalArgumentException("参数(" + name + ")的内容不可为空。");
        }
        String valueStr = StrUtil.toStringOrNull(value);
        return valueStr;
    }

    private static String appendKeyword(BaseQueryItem queryItem) {
        String name = queryItem.getName();
        if (name.lastIndexOf(".keyword") == -1 && queryItem.isKeyword()) {
            return name + ".keyword";
        }
        return name;
    }

    private static final Map<String, Map<String, Map<ConditionEnum, Boolean>>> APPEND_SUFFIX_MAP = new ConcurrentHashMap<>();

    public static void checkKeyword(Class<?> aClass, List<BaseQueryItem> items) {
        String className = aClass.getName();
        Map<String, Map<ConditionEnum, Boolean>> classFieldMap = APPEND_SUFFIX_MAP.computeIfAbsent(className, key -> new ConcurrentHashMap<>());
        for (BaseQueryItem item : items) {
            List<BaseQueryItem> children = item.getChildren();
            if (null != children && !children.isEmpty()) {
                ElasticsearchCore.checkKeyword(aClass, children);
                return;
            }
            String fieldName = item.getName();
            if (StrUtil.isEmpty(fieldName)) {
                continue;
            }
            Map<ConditionEnum, Boolean> map = classFieldMap.computeIfAbsent(fieldName, key -> new ConcurrentHashMap<>());
            ConditionEnum conditionEnum = ConditionEnum.to(item.getCondition());
            Boolean append = map.computeIfAbsent(conditionEnum, key -> {
                switch (key) {
                    case EQ:
                    case IN:
                    case NE:
                    case NOT_IN:
                    case LIKE:
                    case LIKE_LEFT:
                    case LIKE_RIGHT:
                        String[] split = fieldName.split("\\.");
                        Field declaredField = null;
                        Class<?> tClass = aClass;
                        try {
                            for (String tName : split) {
                                declaredField = tClass.getDeclaredField(tName);
                                ClassType signatureValue = ClassTypeUtil.getSignatureValue(declaredField, false, true);
                                if (signatureValue == null) {
                                    continue;
                                }
                                ClassType vType = signatureValue.getVType();
                                if (vType != null) {
                                    tClass = vType.getTType();
                                    continue;
                                }
                                tClass = signatureValue.getTType();
                            }
                        } catch (NoSuchFieldException ignored) {
                            return false;
                        }
                        if (null == declaredField) {
                            return false;
                        }
                        org.springframework.data.elasticsearch.annotations.Field fieldAnnotation = declaredField.getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class);
                        if (fieldAnnotation == null || fieldAnnotation.type() != FieldType.Text) {
                            return false;
                        }
                        return declaredField.getType().isAssignableFrom(String.class);
                    default:
                        return false;
                }
            });
            item.setKeyword(append);
        }
    }
}
