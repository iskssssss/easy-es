package top.kongsheng.common.es.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 查询字段过滤器
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/3/15 13:58
 */
public class SourceFilter implements Serializable {
    static final long serialVersionUID = 42L;

    /**
     * 限制字段列表
     */
    private List<String> includes = null;

    /**
     * 排除字段列表
     */
    private List<String> excludes = null;

    public SourceFilter() {
    }

    public SourceFilter(List<String> includes, List<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    public void init() {
        if (this.includes == null) {
            this.includes = new LinkedList<>();
        }
        if (this.excludes == null) {
            this.excludes = new LinkedList<>();
        }
    }

    public SourceFilter addIncludes(String... fields) {
        if (fields == null || fields.length < 1) {
            return this;
        }
        if (this.includes == null) {
            this.includes = new LinkedList<>();
        }
        Collections.addAll(this.includes, fields);
        return this;
    }

    public SourceFilter addExcludes(String... fields) {
        if (fields == null || fields.length < 1) {
            return this;
        }
        if (this.excludes == null) {
            this.excludes = new LinkedList<>();
        }
        Collections.addAll(this.includes, fields);
        return this;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    @Override
    public String toString() {
        return "SourceFilter{" +
                "includes=" + includes +
                ", excludes=" + excludes +
                '}';
    }
}
