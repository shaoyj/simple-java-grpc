package com.mylomen.demo.config;

//@Configuration
//@EnableTransactionManagement
public class FbLocalDataSourceConfig {

//    private static final Logger log = LoggerFactory.getLogger(FbLocalDataSourceConfig.class);
//
//
//    @Primary
//    @Bean("sqlSessionFactory")
//    @Autowired
//    public static SqlSessionFactory sqlSessionFactoryBrioData(DataSource dataSource) throws Exception {
//        log.info("初始化Mybatis sqlSessionFactoryBaseData . . .");
//        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//        sessionFactoryBean.setDataSource(dataSource);
//        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/brio/*.xml");
//        sessionFactoryBean.setMapperLocations(resources);
//
//        //优化参数
//        sessionFactoryBean.setConfigLocation(XxqMybatisDbConfigUtils.getConfigLocation("classpath:mybatis/config/mybatis-config.xml"));
//        //需要增加的插件
////        sessionFactoryBean.setPlugins(new Interceptor[]{new CatMybatisPlugin()});
//        return sessionFactoryBean.getObject();
//    }
//
//    @Bean("mapper1")
//    public static MapperScannerConfigurer mapperScannerConfigure1() {
//        log.info("初始化Mybatis Mapper扫描配置 brio . . .");
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//        mapperScannerConfigurer.setBasePackage("com.mylomen.demo.mapper");
//
//        return mapperScannerConfigurer;
//    }
//
//
//    @Bean(name = "transactionManager")
//    public static DataSourceTransactionManager transactionManager(DataSource dataSource) {
//        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
//        transactionManager.setDataSource(dataSource);
//        return transactionManager;
//    }
//
//    @Bean
//    public static TransactionTemplate transactionTemplate(@Qualifier(value = "transactionManager") DataSourceTransactionManager transactionManager) {
//        TransactionTemplate template = new TransactionTemplate();
//        template.setTransactionManager(transactionManager);
//        return template;
//    }
//
//
//    @Autowired
//    private RestTemplateBuilder restTemplateBuilder;
//
//    @Bean(name = "restTemplate")
//    public RestTemplate restTemplate() {
//        RestTemplate restTemplate = restTemplateBuilder
//                //连接超时为5秒
//                .setConnectTimeout(Duration.ofSeconds(5))
//                //请求超时为5秒
//                .setReadTimeout(Duration.ofSeconds(5))
//                .build();
//        return restTemplate;
//    }

}
