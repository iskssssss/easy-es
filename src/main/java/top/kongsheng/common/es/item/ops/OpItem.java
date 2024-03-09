package top.kongsheng.common.es.item.ops;

import top.kongsheng.common.es.enums.OpEnum;
import top.kongsheng.common.es.item.BaseQueryItem;

import java.util.List;

/**
 * 基础 条件项
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/11 14:59
 */
public abstract class OpItem extends BaseQueryItem {

    public OpItem(OpEnum opEnum) {
        super.setOp(opEnum.getCode());
    }

    public OpItem addItem(BaseQueryItem queryItem) {
        if (queryItem == null) {
            throw new IllegalArgumentException("条件项不可为空。");
        }
        this.children.add(queryItem);
        return this;
    }

    public OpItem addItem(List<BaseQueryItem> valueItemList) {
        if (valueItemList == null || valueItemList.isEmpty()) {
            throw new IllegalArgumentException("条件项不可为空。");
        }
        this.children.addAll(valueItemList);
        return this;
    }
}
