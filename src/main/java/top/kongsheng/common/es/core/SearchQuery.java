package top.kongsheng.common.es.core;


import top.kongsheng.common.es.item.BaseQueryItem;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 多条件搜索
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/3/7 17:24
 */
public class SearchQuery implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * 条件列表
     */
    private List<BaseQueryItem> items;

    /**
     * 查询字段过滤器
     */
    private SourceFilter sourceFilter;

    /**
     * 排序信息
     */
    private Collection<OrderInfo> orders;

    /**
     * 分页信息
     */
    private PageInfo page;

    public void init() {
        if (this.items == null) {
            this.items = new LinkedList<>();
        }
        if (this.sourceFilter == null) {
            this.sourceFilter = new SourceFilter();
        }
        this.sourceFilter.init();
        if (this.orders == null) {
            this.orders = new LinkedList<>();
        }
        if (this.page == null) {
            this.page = new PageInfo(0, -1);
        }
    }

    public List<BaseQueryItem> getItems() {
        if (this.items == null) {
            this.items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<BaseQueryItem> items) {
        this.items = items;
    }

    public SourceFilter getSourceFilter() {
        if (this.sourceFilter == null) {
            this.sourceFilter = new SourceFilter();
        }
        return this.sourceFilter;
    }

    public void setSourceFilter(SourceFilter sourceFilter) {
        this.sourceFilter = sourceFilter;
    }

    public Collection<OrderInfo> getOrders() {
        return orders;
    }

    public void setOrders(Collection<OrderInfo> orders) {
        this.orders = orders;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "SearchQuery{" +
                "items=" + items +
                ", sourceFilter=" + sourceFilter +
                ", orders=" + orders +
                ", page=" + page +
                '}';
    }
}
