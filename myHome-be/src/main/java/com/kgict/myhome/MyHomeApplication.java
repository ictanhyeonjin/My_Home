package com.kgict.myhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication(exclude = {
		WebSocketServletAutoConfiguration.class
})
public class MyHomeApplication {

	public static void main(String[] args) {
		if (System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME) == null) {
			System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "local");
		}

		SpringApplication.run(MyHomeApplication.class, args);
	}
}
