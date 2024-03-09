package top.kongsheng.common.es.core;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import top.kongsheng.common.es.enums.ConditionEnum;
import top.kongsheng.common.es.enums.OpEnum;
import top.kongsheng.common.es.item.BaseQueryItem;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import static top.kongsheng.common.es.enums.ConditionEnum.IS_EMPTY;
import static top.kongsheng.common.es.enums.ConditionEnum.IS_NOT_EMPTY;

/**
 * sql 工具类
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/17 16:29
 */
public class SqlUtil {

    public static String sql(SearchQuery searchQuery) {
        return sql(searchQuery, true);
    }

    public static String sql(SearchQuery searchQuery, boolean sqlName) {
        List<BaseQueryItem> items = searchQuery.getItems();
        return batch2Sql(items, sqlName);
    }

    private static String batch2Sql(List<BaseQueryItem> valueItemList, boolean sqlName) {
        if (null == valueItemList || valueItemList.isEmpty()) {
            return null;
        }
        // 主题=1 OR (主题=2 AND 正文=4) OR 主题=5
        StringBuilder resultStr = new StringBuilder();
        for (BaseQueryItem queryItem : valueItemList) {
            OpEnum opEnum = OpEnum.to(queryItem.getOp());
            if (queryItem.isOp()) {
                resultStr.append(opEnum.getOpLink());
                continue;
            }
            ConditionEnum conditionEnum = ConditionEnum.to(queryItem.getCondition());
            List<BaseQueryItem> children = queryItem.getChildren();
            boolean childrenEmpty = children.isEmpty();
            if (childrenEmpty) {
                Object[] values = queryItem.getValues();
                String conditionHandleResult = conditionHandle(queryItem.getName(sqlName), values, conditionEnum);
                resultStr.append(conditionHandleResult);
                continue;
            }
            String batch2SqlResult = batch2Sql(children, sqlName);
            if (StrUtil.isNotEmpty(batch2SqlResult)) {
                if (opEnum == OpEnum.NOT) {
                    resultStr.append(opEnum.getOpLink());
                }
                resultStr.append("(").append(batch2SqlResult).append(")");
            }
        }
        return resultStr.toString();
    }

    private static String conditionHandle(String name, Object[] values, ConditionEnum conditionEnum) {
        boolean valuesEmpty = values == null || values.length < 1;
        if (conditionEnum != IS_EMPTY && conditionEnum != IS_NOT_EMPTY && valuesEmpty) {
            throw new IllegalArgumentException("参数错误，(" + name + ")参数的值不可为空。");
        }
        Object firstValue = valuesEmpty ? null : values[0];
        StringBuilder resultStr = new StringBuilder();
        switch (conditionEnum) {
            case EQ:
            case NE:
                resultStr
                        .append(name)
                        .append(conditionEnum.getConditionLink())
                        .append(valueHandle(firstValue));
                break;
            case IN:
            case NOT_IN:
                resultStr
                        .append(name)
                        .append(conditionEnum.getConditionLink())
                        .append("(").append(valueHandle(values)).append(")");
                break;
            case MATCH:
            case MATCH_PHRASE:
                conditionEnum = ConditionEnum.LIKE;
            case LIKE:
                resultStr
                        .append(name)
                        .append(conditionEnum.getConditionLink())
                        .append(valueHandle(firstValue, "%", "%"));
                break;
            case LIKE_LEFT:
                resultStr
                        .append(name)
                        .append(conditionEnum.getConditionLink())
                        .append(valueHandle(firstValue, "%", null));
                break;
            case LIKE_RIGHT:
                resultStr
                        .append(name)
                        .append(conditionEnum.getConditionLink())
                        .append(valueHandle(firstValue, null, "%"));
                break;
            case BETWEEN:
                Object endValue = values[1];
                if (ObjUtil.isEmpty(firstValue) || ObjUtil.isEmpty(endValue)) {
                    throw new IllegalArgumentException("参数错误。");
                }
                resultStr.append("(")
                        .append(name)
                        .append(conditionEnum.getConditionLink())
                        .append(valueHandle(firstValue))
                        .append(OpEnum.AND.getOpLink())
                        .append(valueHandle(endValue))
                        .append(")");
                break;
            default:
                break;
        }
        return resultStr.toString();
    }

    private static String valueHandle(Object[] values) {
        StringJoiner resultJoiner = new StringJoiner(", ");
        for (Object value : values) {
            String valueHandle = valueHandle(value, null, null);
            resultJoiner.add(valueHandle);
        }
        return resultJoiner.toString();
    }

    private static String valueHandle(Object value) {
        return valueHandle(value, null, null);
    }

    private static String valueHandle(Object value, String prefix, String suffix) {
        if (ObjUtil.isEmpty(value)) {
            throw new IllegalArgumentException("参数错误。");
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof LocalDateTime || value instanceof Date) {
            value = dateValueHandle(value);
        }
        StringBuilder resultStr = new StringBuilder();
        resultStr.append("'");
        if (StrUtil.isNotEmpty(prefix)) {
            resultStr.append(prefix);
        }
        resultStr.append(value);
        if (StrUtil.isNotEmpty(suffix)) {
            resultStr.append(suffix);
        }
        resultStr.append("'");
        return resultStr.toString();
    }

    private static String dateValueHandle(Object value) {
        StringBuilder resultStr = new StringBuilder();
        if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            resultStr.append(LocalDateTimeUtil.format(localDateTime, "yyyy-MM-dd HH:mm:ss"));
        } else if (value instanceof Date) {
            Date date = (Date) value;
            resultStr.append(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
        } else {
            resultStr.append(value);
        }
        return resultStr.toString();
    }
}
