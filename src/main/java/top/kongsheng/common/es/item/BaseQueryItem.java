package top.kongsheng.common.es.item;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础查询项
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/11 14:39
 */
public class BaseQueryItem implements Serializable {
    static final long serialVersionUID = 42L;
    private static final Map<String, String> SQL_NAME_MAP = new ConcurrentHashMap<>();

    /**
     * 操作(1.与 2.或 3.非)
     */
    protected int op;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 值
     */
    private Object[] values;

    /**
     * 查询关键词
     */
    private int condition = 1;

    /**
     * 是否追加{.keyword}字符串
     */
    private boolean keyword = false;

    protected final List<BaseQueryItem> children = new LinkedList<>();

    public boolean isOp() {
        return !this.isValue();
    }

    public boolean isValue() {
        return StrUtil.isNotEmpty(name) || !this.children.isEmpty();
    }

    public String getName(boolean sqlName) {
        if (sqlName) {
            String result = SQL_NAME_MAP.computeIfAbsent(this.name, key -> {
                int length = key.length();
                StringBuilder resultBuilder = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    char c = key.charAt(i);
                    if (c >= 'A' && c <= 'Z') {
                        if (i > 0 && key.charAt(i - 1) != '_') {
                            resultBuilder.append("_");
                        }
                        resultBuilder.append((char) (c + 32));
                        continue;
                    }
                    resultBuilder.append(c);
                }
                return resultBuilder.toString();
            });
            return result;
        }
        return this.name;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public List<BaseQueryItem> getChildren() {
        return children;
    }

    public boolean isKeyword() {
        return keyword;
    }

    public void setKeyword(boolean keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "BaseQueryItem{" +
                "op=" + op +
                ", name='" + name + '\'' +
                ", values=" + Arrays.toString(values) +
                ", condition=" + condition +
                ", keyword=" + keyword +
                ", children=" + children +
                '}';
    }
}
