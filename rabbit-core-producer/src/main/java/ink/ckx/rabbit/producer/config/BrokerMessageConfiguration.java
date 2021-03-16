package ink.ckx.rabbit.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * 进行数据库表结构的创建
 */
@Slf4j
@Configuration
public class BrokerMessageConfiguration {

    private final DataSource rabbitProducerDataSource;

    @Value("classpath:rabbit_reliability.sql")
    private Resource schemaScript;

    public BrokerMessageConfiguration(DataSource rabbitProducerDataSource) {
        this.rabbitProducerDataSource = rabbitProducerDataSource;
    }

    @Bean
    public DataSourceInitializer initDataSourceInitializer() {
        log.info("--------------rabbitProducerDataSource----------- : {}", rabbitProducerDataSource);
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(rabbitProducerDataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(schemaScript);
        return resourceDatabasePopulator;
    }
}