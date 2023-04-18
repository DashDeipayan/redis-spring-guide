package com.guide.redisguide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class RedisguideApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisguideApplication.class);

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory,
	                                        MessageListenerAdapter listenerAdapter){
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("chat"));

		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Reciever reciever){
		return new MessageListenerAdapter(reciever, "receiveMessage");
	}

	@Bean
	Reciever reciever(){
		return new Reciever();
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory){
		return new StringRedisTemplate(connectionFactory);
	}


	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx =  SpringApplication.run(RedisguideApplication.class, args);
		StringRedisTemplate redisTemplate = ctx.getBean(StringRedisTemplate.class);
		Reciever reciever = ctx.getBean(Reciever.class);

		while (reciever.getCount()==0){
			LOGGER.info("Sending message....");
			redisTemplate.convertAndSend("chat", "Hello there");
			Thread.sleep(500L);
		}

		System.exit(0);
	}

}
