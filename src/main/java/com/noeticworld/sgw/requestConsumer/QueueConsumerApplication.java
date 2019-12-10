package com.noeticworld.sgw.requestConsumer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@EnableEurekaClient
@SpringBootApplication
@EnableRabbit
//@EnableBinding(ProducerConsumerBinding.class)
public class QueueConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueConsumerApplication.class, args);
	}

	@Bean
	public Queue queue() {
		return new Queue("hello", false);
	}

//    @Configuration
//    public class AsynchronousSpringEventsConfig {
        @Bean(name = "applicationEventMulticaster")
        public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
            SimpleApplicationEventMulticaster eventMulticaster =
                    new SimpleApplicationEventMulticaster();

            eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
            return eventMulticaster;
        }
//    }
}
