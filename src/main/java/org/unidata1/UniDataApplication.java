package org.unidata1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.unidata1.repository")
@EntityScan(basePackages = "org.unidata1.model")
public class UniDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniDataApplication.class, args);
    }

    @Bean
    public ApplicationListener<WebServerInitializedEvent> webServerInitializedEventApplicationListener() {
        return event -> {
            try {
                int port = event.getWebServer().getPort();
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                String hostname = InetAddress.getLocalHost().getHostName();

                System.out.println("\n----------------------------------------------------------");
                System.out.println("Міне сілтеме (локальный доступ): http://localhost:" + port);
                System.out.println("Міне сілтеме (по IP): http://" + hostAddress + ":" + port);
                System.out.println("Міне сілтеме (по имени): http://" + hostname + ":" + port);
                System.out.println("----------------------------------------------------------\n");
            } catch (UnknownHostException e) {
                System.out.println("Не удалось определить адрес хоста: " + e.getMessage());
            }
        };
    }
}