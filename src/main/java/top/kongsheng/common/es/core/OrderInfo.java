package top.kongsheng.common.es.core;

import java.io.Serializable;

/**
 * 排序信息
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/21 11:47
 */
public class OrderInfo implements Serializable {
    private static final long serialVersionUID = 42L;
    /**
     * 名称
     */
    private String name;

    /**
     * 是否升序
     */
    private boolean asc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "name='" + name + '\'' +
                ", asc=" + asc +
                '}';
    }
}
