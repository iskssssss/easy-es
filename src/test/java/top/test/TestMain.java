package top.test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import org.junit.Test;
import top.kongsheng.common.es.core.SearchQuery;
import top.kongsheng.common.es.builder.LambdaSelectBuilder;
import top.kongsheng.common.es.builder.SelectBuilder;
import top.kongsheng.common.es.builder.SelectBuilderUtil;
import top.test.module.TestModel;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2024 KONG SHENG
 * @date 2023/4/13 14:33
 */
public class TestMain {

    @Test
    public void main() {
        LambdaSelectBuilder<TestModel> lambdaSelectBuilder = SelectBuilderUtil.lambdaSelect(TestModel.class);
        SearchQuery searchQuery = lambdaSelectBuilder
                .select(TestModel::getName)
                .match(TestModel::getName, "张三")
                .not(c -> c.eq(TestModel::getAge, 15))
                .list();
        System.out.println(JSONUtil.toJsonStr(searchQuery));
    }

    @Test
    public void test2() {
        DateTime date = DateUtil.date();
        SelectBuilder<TestModel> selectBuilder = SelectBuilderUtil.select(TestModel.class);
        SearchQuery searchQuery = selectBuilder
                .select("name")
                .match("name", "张三")
                .not(c -> c.eq("age", 15))
                .list();
        System.out.println(JSONUtil.toJsonStr(searchQuery));
    }
}






























