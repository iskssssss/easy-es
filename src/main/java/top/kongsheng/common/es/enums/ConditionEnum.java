package top.kongsheng.common.es.enums;

/**
 * 条件枚举
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/3/7 13:51
 */
public enum ConditionEnum {
    // 1.等于 2.不等于 3.大于 4.大于等于 5.小于 6.小于等于 7.全模糊 8.左模糊 9.右模糊 10.范围
    /**
     * 等于
     */
    EQ(1, "="),
    /**
     * 不等于
     */
    NE(2, "!="),
    /**
     * 大于
     */
    GT(3, ">"),
    /**
     * 大于等于
     */
    GE(4, ">="),
    /**
     * 小于
     */
    LT(5, "<"),
    /**
     * 小于等于
     */
    LE(6, "<="),
    /**
     * 模糊
     */
    LIKE(7, "LIKE"),
    /**
     * 左模糊
     */
    LIKE_LEFT(8, "LIKE"),
    /**
     * 右模糊
     */
    LIKE_RIGHT(9, "LIKE"),
    /**
     * 包含
     */
    IN(10, "IN"),
    /**
     * 不包含
     */
    NOT_IN(11, "NOT IN"),
    /**
     * 范围
     */
    BETWEEN(12, "BETWEEN"),
    /**
     * 是空值
     */
    IS_EMPTY(13, "IS_EMPTY"),
    /**
     * 不是空值
     */
    IS_NOT_EMPTY(14, "IS_NOT_EMPTY"),
    /**
     * 分词查询
     */
    MATCH(50, "MATCH"),
    /**
     * 分词查询（分词必须相邻）
     */
    MATCH_PHRASE(51, "MATCH_PHRASE");

    private final int code;
    private final String conditionChar;

    ConditionEnum(int code, String opChar) {
        this.code = code;
        this.conditionChar = opChar;
    }

    public int getCode() {
        return code;
    }

    public String getConditionChar() {
        return conditionChar;
    }

    public String getConditionLink() {
        return " " + conditionChar + " ";
    }

    public static ConditionEnum to(int code) {
        for (ConditionEnum value : ConditionEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return EQ;
    }
}
