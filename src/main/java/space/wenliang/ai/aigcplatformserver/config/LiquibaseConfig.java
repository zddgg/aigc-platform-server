package space.wenliang.ai.aigcplatformserver.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    @Profile("mysql")
    public SpringLiquibase mysqlLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:liquibase/mysql/master.xml");
        liquibase.setClearCheckSums(true);
        return liquibase;
    }

    @Bean
    @Profile("sqlite")
    public SpringLiquibase sqliteLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:liquibase/sqlite/master.xml");
        liquibase.setClearCheckSums(true);
        return liquibase;
    }
}
