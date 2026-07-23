package com.kgict.myhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.env.AbstractEnvironment;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;

@SpringBootApplication(exclude = {
		WebSocketServletAutoConfiguration.class,
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		MybatisAutoConfiguration.class
})
@ConfigurationPropertiesScan
public class MyHomeApplication {

	public static void main(String[] args) {
		if (System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME) == null) {
			System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "local");
		}

		SpringApplication.run(MyHomeApplication.class, args);
	}
}
