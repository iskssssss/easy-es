package top.kongsheng.common.es.item.ops;

import top.kongsheng.common.es.enums.OpEnum;

/**
 * and 条件项
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/11 14:39
 */
public class AndItem extends OpItem {
    private static final AndItem AND_ITEM = new AndItem();

    public AndItem() {
        super(OpEnum.AND);
    }

    public static AndItem getInstance() {
        return AND_ITEM;
    }
}
