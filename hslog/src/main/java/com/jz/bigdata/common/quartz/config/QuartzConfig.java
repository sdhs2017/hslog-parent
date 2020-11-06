package com.jz.bigdata.common.quartz.config;

import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {
    public static final String QUARTZ_PROPERTIES_PATH = "/quartz.properties";
    @Resource
    private ApplicationContext applicationContext;
    @Bean(name = "jobFactory")
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
    @Bean (name = "schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory(applicationContext));
        factory.setQuartzProperties(quartzProperties());//加载配置文件
        factory.setOverwriteExistingJobs(true); //每次启动都更新job信息
        factory.setStartupDelay(30);// QuartzScheduler 延时启动，应用启动完30秒后 QuartzScheduler 再启动
        factory.setAutoStartup(true);
        return factory;
    }

    /**
     * quartz 初始化监听器
     * @return
     */
    @Bean
    public QuartzInitializerListener initializerListener(){
        return new QuartzInitializerListener();
    }
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTIES_PATH));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
            ApplicationContextAware {

        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }
}