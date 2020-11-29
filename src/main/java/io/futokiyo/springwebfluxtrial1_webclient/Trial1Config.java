package io.futokiyo.springwebfluxtrial1_webclient;

import javax.naming.NamingException;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.jndi.JndiTemplate;
import java.util.concurrent.ExecutorService;

@Configuration
@EnableScheduling
@EnableAsync
public class Trial1Config implements AsyncConfigurer {
	
	@Bean(name = "jbossAsyncExecutorService")
    public ExecutorService jbossAsyncExecutorService() {
		String asyncExecutorJNDIName = "java:jboss/ee/concurrency/executor/default";
        JndiTemplate jndiTemplate = new JndiTemplate();

        ExecutorService executorService;
        try {
        	executorService = (ExecutorService) jndiTemplate.lookup(asyncExecutorJNDIName);
        } catch (NamingException e) {
        	e.printStackTrace();
            System.err.println("Unable to find async executor {} in jndi" + asyncExecutorJNDIName + e.getMessage());
            // TODO what will be the fallback when JNDI is not found?
            executorService = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("ae-fallback"));
        }
        return executorService;
		
	}
	
    @Bean
    protected WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setTaskExecutor(getTaskExecutor());
                // TODO timeout
                long timeout = 61000L;// timeout example 61sec
                configurer.setDefaultTimeout(timeout);
            }
        };
    }

    @Bean
    protected ConcurrentTaskExecutor getTaskExecutor() {
        return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(2));
    }

}
