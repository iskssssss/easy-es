package top.kongsheng.common.es.core;

import java.io.Serializable;

/**
 * 分页信息
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/21 14:12
 */
public class PageInfo implements Serializable {
    private static final long serialVersionUID = 42L;

    private int page = 0;
    private int size = 10;

    public PageInfo() {
    }

    public PageInfo(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void notPage() {
        this.size = -1;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
