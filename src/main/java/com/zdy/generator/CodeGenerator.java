package com.zdy.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {
    public static void main(String[] args) {
        //1.获取代码生成器的对象
        AutoGenerator autoGenerator = new AutoGenerator();

        //设置数据库和相关配置
        DataSourceConfig dataSource = new DataSourceConfig();
        dataSource.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/reggie?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        autoGenerator.setDataSource(dataSource);

        //设置全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(System.getProperty("user.dir")+"/src/main/java"); //设定文件生成的位置
        globalConfig.setOpen(false);          //   设置生成完毕后是否打开生成的代码所在的目录
        globalConfig.setAuthor("迷糊小丸子");            //  设置作者
        globalConfig.setFileOverride(true);     //  设置是否覆盖原始生成的文件
        globalConfig.setMapperName("%sDao");       // 设置数据层接口名，%s为占位符，指定代码块名称
        globalConfig.setIdType(IdType.ASSIGN_ID);       // 设置id生成策略
        autoGenerator.setGlobalConfig(globalConfig);


        //设置包名相关配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.zdy");        //设置生成的包名
        packageConfig.setEntity("domain");         // 设置实体类包名
        packageConfig.setMapper("dao");            // 设置数据层包名
        autoGenerator.setPackageInfo(packageConfig);

        //策略设置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setInclude("orders","order_detail");               //设置当前参与生成的表名，参数为可变参数
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);  //生成类属性为驼峰命名格式
//        strategyConfig.setTablePrefix("tb_");                // 设置数据库表的前缀名称  模块名 = 数据库名 - 前缀名
        strategyConfig.setRestControllerStyle(true);            //  设置是否采用Rest风格
//        strategyConfig.setVersionFieldName("version");          //  设置乐观锁字段名
        strategyConfig.setLogicDeleteFieldName("deleted");      //   设置逻辑删除字段名
        strategyConfig.setEntityLombokModel(true);               //    设置是否启用lombok
        autoGenerator.setStrategy(strategyConfig);


        //2.执行生成操作
        autoGenerator.execute();

    }
}
