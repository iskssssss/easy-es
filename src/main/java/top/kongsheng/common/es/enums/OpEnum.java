package top.kongsheng.common.es.enums;

/**
 * 操作枚举
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/3/7 13:51
 */
public enum OpEnum {
    /**
     * 与
     */
    AND(1, "AND"),
    /**
     * 或
     */
    OR(2, "OR"),
    /**
     * 非
     */
    NOT(3, "NOT"),
    /**
     * 空
     */
    DEFAULT(99, "");

    private final int code;
    private final String opChar;

    OpEnum(int code, String opChar) {
        this.code = code;
        this.opChar = opChar;
    }

    public int getCode() {
        return code;
    }

    public String getOpChar() {
        return opChar;
    }

    public String getOpLink() {
        if ("".equals(opChar)) {
            return "";
        }
        return " " + opChar + " ";
    }

    public static OpEnum to(int code) {
        for (OpEnum value : OpEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return AND;
    }
}
