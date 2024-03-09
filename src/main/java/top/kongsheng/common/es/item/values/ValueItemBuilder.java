package top.kongsheng.common.es.item.values;

import top.kongsheng.common.es.enums.OpEnum;

/**
 * 查询单项 构建器
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/3/7 13:51
 */
public class ValueItemBuilder<T extends ValueItem> {
    private final T item;

    public ValueItemBuilder(T item) {
        this.item = item;
    }

    public static ValueItemBuilder<ValueItem> start() {
        return new ValueItemBuilder<>(new ValueItem());
    }

    public ValueItemBuilder<T> name(String name) {
        this.item.setName(name);
        return this;
    }

    public ValueItemBuilder<T> values(Object[] values) {
        this.item.setValues(values);
        return this;
    }

    public ValueItemBuilder<T> not() {
        this.item.setOp(OpEnum.NOT.getCode());
        return this;
    }

    public ValueItemBuilder<T> condition(int condition) {
        this.item.setCondition(condition);
        return this;
    }

    public ValueItem end() {
        return this.item;
    }
}
