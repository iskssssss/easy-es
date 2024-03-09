package top.kongsheng.common.es.builder;

import top.kongsheng.common.es.core.OrderInfo;
import top.kongsheng.common.es.core.SearchQuery;
import top.kongsheng.common.es.core.SerializableFunction;
import top.kongsheng.common.es.enums.ConditionEnum;
import top.kongsheng.common.es.enums.OpEnum;
import top.kongsheng.common.es.item.BaseQueryItem;
import top.kongsheng.common.es.item.ops.AndItem;
import top.kongsheng.common.es.item.ops.NotItem;
import top.kongsheng.common.es.item.ops.OrItem;
import top.kongsheng.common.es.item.values.ValueItem;
import top.kongsheng.common.es.item.values.ValueItemBuilder;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import static top.kongsheng.common.es.enums.OpEnum.NOT;

/**
 * elasticsearch 查询构建器
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/3/7 13:51
 */
public class LambdaSelectBuilder<T> extends AbsSelectBuilder<T, LambdaSelectBuilder<T>, SerializableFunction<T, ?>> {

    public LambdaSelectBuilder(Class<? extends T> modelClass, SearchQuery searchQuery) {
        super(modelClass, searchQuery);
    }

    @SafeVarargs
    @Override
    public final LambdaSelectBuilder<T> select(SerializableFunction<T, ?>... columns) {
        return super.select(columns);
    }

    @SafeVarargs
    @Override
    public final LambdaSelectBuilder<T> select(boolean exclude, SerializableFunction<T, ?>... columns) {
        return super.select(exclude, columns);
    }

    @SafeVarargs
    @Override
    public final LambdaSelectBuilder<T> addIncludes(boolean condition, SerializableFunction<T, ?>... columns) {
        return super.addIncludes(condition, columns);
    }

    @SafeVarargs
    @Override
    public final LambdaSelectBuilder<T> addExcludes(boolean condition, SerializableFunction<T, ?>... columns) {
        return super.addExcludes(condition, columns);
    }

    @SafeVarargs
    @Override
    public final LambdaSelectBuilder<T> select(boolean condition, boolean exclude, boolean clean, SerializableFunction<T, ?>... columns) {
        if (!condition || columns == null || columns.length < 1) {
            return this;
        }
        List<String> target = exclude ? this.excludes : this.includes;
        if (clean) {
            target.clear();
        }
        for (SerializableFunction<T, ?> column : columns) {
            Method columnMethod = this.getColumnMethod(column);
            String columnName = this.methodToProperty(columnMethod.getName());
            if (target.contains(columnName)) {
                continue;
            }
            target.add(columnName);
        }
        return this;
    }

    public LambdaSelectBuilder<T> order(String columnName, boolean asc) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setName(columnName);
        orderInfo.setAsc(asc);
        this.orders.add(orderInfo);
        return this;
    }

    @Override
    public LambdaSelectBuilder<T> order(SerializableFunction<T, ?> column, boolean asc) {
        Method columnMethod = this.getColumnMethod(column);
        String columnName = this.methodToProperty(columnMethod.getName());
        return order(columnName, asc);
    }

    @Override
    public LambdaSelectBuilder<T> valueItem(SerializableFunction<T, ?> column, ConditionEnum conditionEnum, Object... values) {
        Method columnMethod = this.getColumnMethod(column);
        String columnName = this.methodToProperty(columnMethod.getName());
        ValueItem valueItem = ValueItemBuilder.start().name(columnName).condition(conditionEnum.getCode()).end();
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
    public LambdaSelectBuilder<T> opItem(OpEnum opEnum, Consumer<LambdaSelectBuilder<T>> consumer) {
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
            LambdaSelectBuilder<T> lambdaSelectBuilder = new LambdaSelectBuilder<>(modelClass, new SearchQuery());
            consumer.accept(lambdaSelectBuilder);
            valueItem.getChildren().addAll(lambdaSelectBuilder.queryItemList);
            this.queryItemList.add(valueItem);
        }
        return this;
    }

    @Override
    public Method getColumnMethod(SerializableFunction<T, ?> column) {
        try {
            Method method = column.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(column);
            String implMethodName = serializedLambda.getImplMethodName();
            return this.batchFindMethod(modelClass, implMethodName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Method batchFindMethod(Class<?> modelClass, String implMethodName) {
        try {
            return modelClass.getDeclaredMethod(implMethodName);
        } catch (NoSuchMethodException e) {
            Class<?> superclass = modelClass.getSuperclass();
            if (superclass == null) {
                throw new RuntimeException("未找到该方法(" + implMethodName + ")。");
            }
            return batchFindMethod(superclass, implMethodName);
        }
    }
}
