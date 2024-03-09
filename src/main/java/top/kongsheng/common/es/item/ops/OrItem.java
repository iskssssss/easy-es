package top.kongsheng.common.es.item.ops;

import top.kongsheng.common.es.enums.OpEnum;

/**
 * or 条件项
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/11 14:39
 */
public class OrItem extends OpItem {
    private static final OrItem OR_ITEM = new OrItem();

    public OrItem() {
        super(OpEnum.OR);
    }

    public static OrItem getInstance() {
        return OR_ITEM;
    }
}
