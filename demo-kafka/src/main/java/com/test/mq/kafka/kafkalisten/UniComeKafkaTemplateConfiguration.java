package com.test.kafka.kafkalisten;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("spring.unicome.kafka")
public class UniComeKafkaTemplateConfiguration {


    @Value("${spring.unicome.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.unicome.kafka.producer.key-serializer}")
    String keySerializer;
    @Value("${spring.unicome.kafka.producer.value-serializer}")
    String valueSerializer;

    /**
     * 发送消息的template
     * @return
     *
     */
    @Bean(name="uniComeKafkaTemplate")
   // @Primary
    public KafkaTemplate<String, String> uniComeKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 生产KafkaTemplate工厂
     * @return
     */
    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * 属性配置
     * @return
     */
    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        // 不能写成 1
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
  /*  @Bean
    public KafkaAdmin uniComeKafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        //配置Kafka实例的连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(props);
        admin.setAutoCreate(true);
        return admin;
    }

    //kafka客户端，在spring中创建这个bean之后可以注入并且创建topic,用于集群环境，创建对个副本
    @Bean(name = "uniComeAdminClient")
    public AdminClient uniComeAdminClient() {
        return AdminClient.create(uniComeKafkaAdmin().getConfig());
    }*/
}
