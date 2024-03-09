package top.kongsheng.common.es.builder;

import top.kongsheng.common.es.core.OrderInfo;
import top.kongsheng.common.es.core.PageInfo;
import top.kongsheng.common.es.core.SearchQuery;
import top.kongsheng.common.es.core.SourceFilter;
import top.kongsheng.common.es.enums.ConditionEnum;
import top.kongsheng.common.es.enums.OpEnum;
import top.kongsheng.common.es.item.BaseQueryItem;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * 抽象查询构建器
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/17 14:55
 */
public abstract class AbsSelectBuilder<T, C extends AbsSelectBuilder<T, C, CT>, CT> {
    protected final Class<? extends T> modelClass;
    protected final List<BaseQueryItem> queryItemList;
    protected final List<String> includes;
    protected final List<String> excludes;
    protected final Collection<OrderInfo> orders;
    protected final PageInfo page;
    protected final SearchQuery searchQuery;

    protected AbsSelectBuilder(Class<? extends T> modelClass, SearchQuery searchQuery) {
        this.modelClass = modelClass;
        this.searchQuery = searchQuery;
        this.searchQuery.init();
        this.queryItemList = this.searchQuery.getItems();
        SourceFilter sourceFilter = this.searchQuery.getSourceFilter();
        this.includes = sourceFilter.getIncludes();
        this.excludes = sourceFilter.getExcludes();
        this.orders = this.searchQuery.getOrders();
        this.page = this.searchQuery.getPage();
    }

    /**
     * 查询
     *
     * @param columns 字段
     * @return this
     */
    public C select(CT... columns) {
        return this.select(true, false, true, columns);
    }

    /**
     * 字段过滤
     *
     * @param exclude 是否是排除模式
     * @param columns 字段
     * @return this
     */
    public C select(boolean exclude, CT... columns) {
        return this.select(true, exclude, true, columns);
    }

    /**
     * 限制字段
     *
     * @param condition 是否载入
     * @param columns   字段
     * @return this
     */
    public C addIncludes(boolean condition, CT... columns) {
        return this.select(condition, false, false, columns);
    }

    /**
     * 排除字段
     *
     * @param condition 是否载入
     * @param columns   字段
     * @return this
     */
    public C addExcludes(boolean condition, CT... columns) {
        return this.select(condition, true, false, columns);
    }

    /**
     * 字段过滤
     *
     * @param condition 是否载入
     * @param exclude   是否是排除模式
     * @param clean     是否清楚已有字段
     * @param columns   字段
     * @return this
     */
    public abstract C select(boolean condition, boolean exclude, boolean clean, CT... columns);

    /**
     * 等于
     *
     * @param column 字段
     * @param value  值
     * @return this
     */
    public C eq(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.EQ, value);
    }

    public C eq(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.EQ, value);
    }

    /**
     * 不等于
     *
     * @param column 字段
     * @param value  值
     * @return this
     */
    public C ne(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.NE, value);
    }

    public C ne(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.NE, value);
    }

    /**
     * 大于
     *
     * @param column 字段
     * @param value  值
     * @return this
     */
    public C gt(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.GT, value);
    }

    public C gt(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.GT, value);
    }

    /**
     * 大于等于
     *
     * @param column 字段
     * @param value  值
     * @return this
     */
    public C ge(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.GE, value);
    }

    public C ge(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.GE, value);
    }

    /**
     * 小于
     *
     * @param column 字段
     * @param value  值
     * @return this
     */
    public C lt(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.LT, value);
    }

    public C lt(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.LT, value);
    }

    /**
     * 小于等于
     *
     * @param column 字段
     * @param values 值
     * @return this
     */
    public C le(CT column, Object values) {
        return this.valueItem(column, ConditionEnum.LE, values);
    }

    public C le(boolean condition, CT column, Object values) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.LE, values);
    }

    /**
     * like
     *
     * @param column 字段
     * @return this
     */
    public C like(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.LIKE, value);
    }

    public C like(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.LIKE, value);
    }

    /**
     * 左 like
     *
     * @param column 字段
     * @return this
     */
    public C likeLeft(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.LIKE_LEFT, value);
    }

    public C likeLeft(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.LIKE_LEFT, value);
    }

    /**
     * 右 like
     *
     * @param column 字段
     * @return this
     */
    public C likeRight(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.LIKE_RIGHT, value);
    }

    public C likeRight(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.LIKE_RIGHT, value);
    }

    /**
     * 包含
     *
     * @param column 字段
     * @return this
     */
    public C in(CT column, Object... values) {
        return this.valueItem(column, ConditionEnum.IN, values);
    }

    public C in(boolean condition, CT column, Object... values) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.IN, values);
    }

    /**
     * 不包含
     *
     * @param column 字段
     * @return this
     */
    public C notIn(CT column, Object... values) {
        return this.valueItem(column, ConditionEnum.NOT_IN, values);
    }

    public C notIn(boolean condition, CT column, Object... values) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.NOT_IN, values);
    }

    /**
     * 范围
     *
     * @param column     字段
     * @param firstValue 开始值
     * @param lastValue  结束值
     * @return this
     */
    public C between(CT column, Object firstValue, Object lastValue) {
        return this.valueItem(column, ConditionEnum.BETWEEN, firstValue, lastValue);
    }

    public C between(boolean condition, CT column, Object firstValue, Object lastValue) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.BETWEEN, firstValue, lastValue);
    }

    /**
     * 空
     *
     * @param column 字段
     * @return this
     */
    public C isEmpty(CT column) {
        return this.valueItem(column, ConditionEnum.IS_EMPTY);
    }

    /**
     * 不为空
     *
     * @param column 字段
     * @return this
     */
    public C isNotEmpty(CT column) {
        return this.valueItem(column, ConditionEnum.IS_NOT_EMPTY);
    }

    /**
     * 分词查询
     *
     * @param column 字段
     * @return this
     */
    public C match(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.MATCH, value);
    }

    public C match(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.MATCH, value);
    }

    /**
     * 分词查询（分词必须相邻）
     *
     * @param column 字段
     * @return this
     */
    public C matchPhrase(CT column, Object value) {
        return this.valueItem(column, ConditionEnum.MATCH_PHRASE, value);
    }

    public C matchPhrase(boolean condition, CT column, Object value) {
        if (!condition) {
            return ((C) this);
        }
        return this.valueItem(column, ConditionEnum.MATCH_PHRASE, value);
    }

    /**
     * 并且
     *
     * @param consumer 构建器
     * @return this
     */
    public C and(Consumer<C> consumer) {
        return this.opItem(OpEnum.AND, consumer);
    }

    public C and(boolean condition, Consumer<C> consumer) {
        if (!condition) {
            return ((C) this);
        }
        return this.opItem(OpEnum.AND, consumer);
    }

    /**
     * 或者
     *
     * @return this
     */
    public C or() {
        return this.opItem(OpEnum.OR, null);
    }

    /**
     * 或者
     *
     * @param consumer 构建器
     * @return this
     */
    public C or(Consumer<C> consumer) {
        return this.opItem(OpEnum.OR, consumer);
    }

    public C or(boolean condition, Consumer<C> consumer) {
        if (!condition) {
            return ((C) this);
        }
        return this.opItem(OpEnum.OR, consumer);
    }

    /**
     * 非
     *
     * @param consumer 构建器
     * @return this
     */
    public C not(Consumer<C> consumer) {
        return this.opItem(OpEnum.NOT, consumer);
    }

    public C not(boolean condition, Consumer<C> consumer) {
        if (!condition) {
            return ((C) this);
        }
        return this.opItem(OpEnum.NOT, consumer);
    }

    /**
     * 排序信息
     *
     * @param column 字段
     * @param asc    是否升序
     * @return this
     */
    public abstract C order(CT column, boolean asc);

    /**
     * 升序
     *
     * @param column 字段
     * @return this
     */
    public C orderAsc(CT column) {
        return this.order(column, true);
    }

    /**
     * 降序
     *
     * @param column 字段
     * @return this
     */
    public C orderDesc(CT column) {
        return this.order(column, false);
    }

    public abstract C valueItem(CT column, ConditionEnum conditionEnum, Object... values);

    public abstract C opItem(OpEnum opEnum, Consumer<C> consumer);

    /**
     * 分页信息
     *
     * @param page 页
     * @param size 数量
     * @return this
     */
    public SearchQuery page(int page, int size) {
        this.page.setPage(page);
        this.page.setSize(size);
        return this.end();
    }

    /**
     * 列表查询
     *
     * @return 查询项列表
     */
    public SearchQuery list() {
        this.page.setSize(-1);
        return this.end();
    }

    /**
     * 列表查询
     *
     * @return 查询项列表
     */
    public SearchQuery end() {
        return searchQuery;
    }

    public Method getColumnMethod(CT column) {
        return null;
    }

    public String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new RuntimeException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }
}
