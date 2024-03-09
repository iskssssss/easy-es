## easy-es

基于lambda/字符串格式的elasticsearch查询工具，简化了elasticsearch查询的编写和调试过程。提供了丰富的查询操作符和功能，支持复杂查询需求的实现。



### 示例

#### lambda

```java
LambdaSelectBuilder<TestModel> lambdaSelectBuilder = SelectBuilderUtil.lambdaSelect(TestModel.class);
SearchQuery searchQuery = lambdaSelectBuilder
        .select(TestModel::getName)
        .match(TestModel::getName, "张三")
        .not(c -> c.eq(TestModel::getAge, 15))
        .list();
```

#### 字符串

```java
SelectBuilder<TestModel> selectBuilder = SelectBuilderUtil.select(TestModel.class);
SearchQuery searchQuery = selectBuilder
        .select("name")
        .match("name", "张三")
        .not(c -> c.eq("age", 15))
        .list();
```