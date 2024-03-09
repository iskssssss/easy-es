package top.kongsheng.common.es.item.ops;

import top.kongsheng.common.es.enums.OpEnum;

/**
 * not 条件项
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/11 14:39
 */
public class NotItem extends OpItem {
    private static final NotItem NOT_ITEM = new NotItem();

    public NotItem() {
        super(OpEnum.NOT);
    }

    public static NotItem getInstance() {
        return NOT_ITEM;
    }
}
