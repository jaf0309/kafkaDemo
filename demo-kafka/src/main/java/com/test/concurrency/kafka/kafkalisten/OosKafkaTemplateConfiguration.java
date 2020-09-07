package com.test.concurrency.kafka.kafkalisten;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConfigurationProperties(value = "spring.oos.kafka")
public class OosKafkaTemplateConfiguration  implements KafkaListenerConfigurer {

    @Value("${spring.oos.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.oos.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.oos.kafka.consumer.enable-auto-commit}")
    private Boolean enableAutoCommit;
    @Value("${spring.oos.kafka.consumer.auto-commit-interval-ms}")
    private String autoCommitIntervalMs;
    @Value("${spring.oos.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${spring.oos.kafka.consumer.auto-offset-reset}")
    private String autoffsetReset;
    @Value("${spring.oos.kafka.consumer.key-deserializer}")
    private String keyDeserializer;
    @Value("${spring.oos.kafka.consumer.value-deserializer}")
    private String valueDeserializer;
    @Value("${spring.oos.kafka.producer.key-serializer}")
    private String keySerializer;
    @Value("${spring.oos.kafka.producer.value-serializer}")
    private String valueSerializer;

    /**
     * 发送消息的template
     * @return
     */
    @Bean(name="oosKafkaTemplate")
    public KafkaTemplate<String, String> oosKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 生产KafkaTemplate工厂
     * @return
     */
    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(oosKafkaProducerProperties());
    }

    /**
     * 生产KafkaTemplate工厂
     * @return
     */
    private Map<String, Object> oosKafkaProducerProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        return props;
    }

    /**
     * 消费工厂
     * @return
     */
    //@Bean(name = "oosKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> oosKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        //initialize the specified container.
        factory.setConsumerFactory(consumerFactory());
        //The maximum number of concurrent {@link KafkaMessageListenerContainer}s running   默认是1，建议和副本数一致.
        factory.setConcurrency(3);
        //factory.setReplyTemplate(oosKafkaTemplate());
        //factory.setRetryTemplate();
        //kafka poll拉取的超时时间
        factory.getContainerProperties().setPollTimeout(60000);
        return factory;
    }

    /**
     *  生产KafkaTemplate工厂
     * @return
     */
    private ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(oosConsumerConfigs());
    }

    /**
     * kafka消费配置
     * @return
     */
    private Map<String, Object> oosConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", enableAutoCommit);
        props.put("auto.commit.interval.ms", autoCommitIntervalMs);
        props.put("max.poll.records", maxPollRecords);
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);
        props.put("auto.offset.reset",autoffsetReset);
        return props;
    }

   /* @Bean
    @Primary
    public AdminClient adminClient(){
         Map<String, Object> config = new HashMap<>();
         List<String> bootstrapServerList = new ArrayList();
         if(bootstrapServers.contains(",")){
             bootstrapServerList = Arrays.asList(bootstrapServers.split(",")) ;
         }else {
             bootstrapServerList.add(bootstrapServers);
         }
        config.put("bootstrap.servers", bootstrapServerList);
        return AdminClient.create(config);
    }*/

//创建一个kafka管理类，相当于rabbitMQ的管理类rabbitAdmin,没有此bean无法自定义的使用adminClient创建topic
    @Bean
    @Primary
    public KafkaAdmin oosKafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        //配置Kafka实例的连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(props);
        //admin.setAutoCreate(true);
        return admin;
    }

    //kafka客户端，在spring中创建这个bean之后可以注入并且创建topic,用于集群环境，创建对个副本
    @Bean
    @Primary
    public AdminClient oosAdminClient() {
        return AdminClient.create(oosKafkaAdmin().getConfig());
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setContainerFactory(oosKafkaListenerContainerFactory());
        registrar.setContainerFactoryBeanName("oosKafkaListenerContainerFactory");
    }
}
