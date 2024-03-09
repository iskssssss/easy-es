package top.kongsheng.common.es.builder;

import top.kongsheng.common.es.core.OrderInfo;
import top.kongsheng.common.es.core.SearchQuery;
import top.kongsheng.common.es.enums.ConditionEnum;
import top.kongsheng.common.es.enums.OpEnum;
import top.kongsheng.common.es.item.BaseQueryItem;
import top.kongsheng.common.es.item.ops.AndItem;
import top.kongsheng.common.es.item.ops.NotItem;
import top.kongsheng.common.es.item.ops.OrItem;
import top.kongsheng.common.es.item.values.ValueItem;
import top.kongsheng.common.es.item.values.ValueItemBuilder;

import java.util.List;
import java.util.function.Consumer;

import static top.kongsheng.common.es.enums.OpEnum.NOT;

/**
 * elasticsearch 查询构建器
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2024/2/21 17:13
 */
public class SelectBuilder<T> extends AbsSelectBuilder<T, SelectBuilder<T>, String> {

    public SelectBuilder(Class<? extends T> modelClass, SearchQuery searchQuery) {
        super(modelClass, searchQuery);
    }

    @Override
    public SelectBuilder<T> select(boolean condition, boolean exclude, boolean clean, String... columns) {
        if (!condition || columns == null || columns.length < 1) {
            return this;
        }
        List<String> target = exclude ? this.excludes : this.includes;
        if (clean) {
            target.clear();
        }
        for (String column : columns) {
            if (target.contains(column)) {
                continue;
            }
            target.add(column);
        }
        return this;
    }

    @Override
    public SelectBuilder<T> order(String columnName, boolean asc) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setName(columnName);
        orderInfo.setAsc(asc);
        this.orders.add(orderInfo);
        return this;
    }

    @Override
    public SelectBuilder<T> valueItem(String column, ConditionEnum conditionEnum, Object... values) {
        ValueItem valueItem = ValueItemBuilder.start().name(column).condition(conditionEnum.getCode()).end();
        if (values != null && values.length > 0) {
            valueItem.setValues(values);
        }
        int size = this.queryItemList.size();
        int lastIndex = size - 1;
        BaseQueryItem lastItem = size > 0 ? this.queryItemList.get(lastIndex) : null;
        if (null != lastItem && lastItem.isValue()) {
            queryItemList.add(AndItem.getInstance());
        }
        queryItemList.add(valueItem);
        return this;
    }

    @Override
    public SelectBuilder<T> opItem(OpEnum opEnum, Consumer<SelectBuilder<T>> consumer) {
        int size = this.queryItemList.size();
        int lastIndex = size - 1;
        BaseQueryItem lastItem = size > 0 ? this.queryItemList.get(lastIndex) : null;
        switch (opEnum) {
            case OR:
                if (null != lastItem && lastItem.isOp()) {
                    this.queryItemList.remove(lastIndex);
                }
                this.queryItemList.add(OrItem.getInstance());
                break;
            case NOT:
                if (null != lastItem && lastItem.isValue()) {
                    this.queryItemList.add(AndItem.getInstance());
                }
                break;
            case AND:
            default:
                if (null != lastItem && lastItem.isOp()) {
                    this.queryItemList.remove(lastIndex);
                }
                this.queryItemList.add(AndItem.getInstance());
        }
        if (consumer != null) {
            BaseQueryItem valueItem;
            if (opEnum == NOT) {
                valueItem = new NotItem();
            } else {
                valueItem = new ValueItem();
            }
            SelectBuilder<T> lambdaSelectBuilder = new SelectBuilder<>(modelClass, new SearchQuery());
            consumer.accept(lambdaSelectBuilder);
            valueItem.getChildren().addAll(lambdaSelectBuilder.queryItemList);
            this.queryItemList.add(valueItem);
        }
        return this;
    }
}
