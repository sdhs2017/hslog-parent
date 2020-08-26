package com.jz.bigdata.business.logAnalysis.collector.kafka;

import com.jz.bigdata.util.ConfigProperty;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: hsc
 * @date: 2018/6/21 15:58
 * @description kafka 消费者配置
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    public KafkaConfig(){
        System.out.println("---------kafka消费者配置加载-------");
    }
    private static Logger logger = Logger.getLogger(KafkaConfig.class);
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);//消费线程数，跟partation一致
        factory.setBatchListener(true); // 设置批量消费方式
        factory.getContainerProperties().setPollTimeout(3000);
        //配置手动提交offset
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL);
        // 禁止自动启动
        factory.setAutoStartup(false);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory(consumerProperties());
    }

    /**
     * kafka消费基本配置信息
     * @return
     */
    @Bean
    public Map<String, Object> consumerProperties() {
        Map<String, Object> props= new HashMap<String, Object>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "jd-group");// 监听的队列的组
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 提交方式
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");// 消费的offset
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configProperty.getKafka_path()); // kafka的地址和端口
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, configProperty.getKafka_max_poll_records());// 一次消费的最大数据量   
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);//会话超时
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15000);//请求超时  必须>会话超时时间
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    public KafkaBeatsListener KafkaBeatsListener(){
        return new KafkaBeatsListener();
    }
    @Bean
    public KafkaAllListener KafkaAllListener(){
        return new KafkaAllListener();
    }

}