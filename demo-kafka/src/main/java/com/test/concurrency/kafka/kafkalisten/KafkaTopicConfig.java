package com.test.concurrency.kafka.kafkalisten;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @Description kafka topic 分区数配置
 * @Author tyl
 * @CreateDate 2020/4/10
 */
@Configuration
public class KafkaTopicConfig {
    @Autowired
    private AdminClient oosAdminClient;

    /**
     * topic 分区数
     */
    @Value("${kafka.topic.partitions:5}")
    private int topicPartitions;

    /**
     * topic 副本分区数
     */
    @Value("${kafka.topic.replica.partitions:3}")
    private int topicReplicaPartitions;

   /* @Bean
    public NewTopic smileTopic(@Value("${service.kafka.centerorder-oos-smile.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
    /**
     * 微笑topic
     * @param topicName
     * @return
     */
    @Bean
    public NewTopic smileTopic(@Value("${service.kafka.centerorder-oos-smile.topic}") String topicName) {
        // 这种是手动创建 //10个分区，一个副本
        // 分区多的好处是能快速的处理并发量，但是也要根据机器的配置
        NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }

    /**
     * app topic
     * @param topicName
     * @return
     */
   /* @Bean
    public NewTopic appTopic(@Value("${service.kafka.centerorder-oos-app.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    } */

    @Bean
    public NewTopic appTopic(@Value("${service.kafka.centerorder-oos-app.topic}") String topicName) {
        NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }

    /**
     * vip topic
     * @param topicName
     * @return
     */
   /* @Bean
    public NewTopic vipTopic(@Value("${service.kafka.centerorder-oos-vip.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    } */
    @Bean
    public NewTopic vipTopic(@Value("${service.kafka.centerorder-oos-vip.topic}") String topicName) {
        NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }

    /**
     * vip_cancel topic
     * @param topicName
     * @return
     */
   /* @Bean
    public NewTopic vipCancelTopic(@Value("${service.kafka.oos_push_vip_cancel.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
    @Bean
    public NewTopic vipCancelTopic(@Value("${service.kafka.oos_push_vip_cancel.topic}") String topicName) {
     NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
     return topic;
    }

    /**
     * vip_mailno topic
     * @param topicName
     * @return
     */
    /*@Bean
    public NewTopic vipMailNoTopic(@Value("${service.kafka.oos_push_vip_mailno.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
    @Bean
    public NewTopic vipMailNoTopic(@Value("${service.kafka.oos_push_vip_mailno.topic}") String topicName) {
        NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }

    /**
     * vip_reserve topic
     * @param topicName
     * @return
     */
    /*@Bean
    public NewTopic vipReserveTopic(@Value("${service.kafka.oos_push_vip_reserve.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
   @Bean
    public NewTopic vipReserveTopic(@Value("${service.kafka.oos_push_vip_reserve.topic}") String topicName) {
       NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
       oosAdminClient.createTopics(Arrays.asList(topic));
       return topic;
    }

    /**
     * order_fail_queue topic
     * @param topicName
     * @return
     */
   /* @Bean
    public NewTopic orderFailQueueTopic(@Value("${service.kafka.centerorder-order-fail-queue.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/

   @Bean
    public NewTopic orderFailQueueTopic(@Value("${service.kafka.centerorder-order-fail-queue.topic}") String topicName) {
       NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
       oosAdminClient.createTopics(Arrays.asList(topic));
       return topic;
    }

    /**
     * print_info_queue topic
     * @param topicName
     * @return
     */
   /* @Bean
    public NewTopic printInfoQueueTopic(@Value("${service.kafka.centerorder-print-info-queue.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
  @Bean
    public NewTopic printInfoQueueTopic(@Value("${service.kafka.centerorder-print-info-queue.topic}") String topicName) {
      NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
      oosAdminClient.createTopics(Arrays.asList(topic));
      return topic;
    }

    /**
     * mopay topic
     * @param topicName
     * @return
     */
    /*@Bean
    public NewTopic mopayTopic(@Value("${service.kafka.centerorder-oos-mopay.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
    @Bean
    public NewTopic mopayTopic(@Value("${service.kafka.centerorder-oos-mopay.topic}") String topicName) {
        NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }

    /**
     * auto_ywy topic
     * @param topicName
     * @return
     */
   /* @Bean
    public NewTopic autoYwyTopic(@Value("${service.kafka.centerorder-oos-autoywy.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
    @Bean
    public NewTopic autoYwyTopic(@Value("${service.kafka.centerorder-oos-autoywy.topic}") String topicName) {
        NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }

    /**
     * centerorder.oos.mopay.batch topic
     * @param topicName
     * @return
     */
    /*@Bean
    public NewTopic batchMopayTopic(@Value("${service.kafka.centerorder-oos-mopay-batch.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
     @Bean
    public NewTopic batchMopayTopic(@Value("${service.kafka.centerorder-oos-mopay-batch.topic}") String topicName) {
         NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
         oosAdminClient.createTopics(Arrays.asList(topic));
         return topic;
    }

    /**
     * huiyuan_batch_push topic
     * @param topicName
     * @return
     */
   /* @Bean
    public NewTopic batchVipTopic(@Value("${service.kafka.centerorder-oos-vip-batch.topic}") String topicName) {
        return new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
    }*/
    @Bean
    public NewTopic batchVipTopic(@Value("${service.kafka.centerorder-oos-vip-batch.topic}") String topicName) {
        NewTopic topic = new NewTopic(topicName, topicPartitions, (short) topicReplicaPartitions);
        oosAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }

    @Bean
    public CreateTopicsResult testTopic() {
        NewTopic topic = new NewTopic("centerorder.oos.app.topic.test", topicPartitions, (short) topicReplicaPartitions);
        CreateTopicsResult topics = oosAdminClient.createTopics(Arrays.asList(topic));
        return topics;
    }
/*
    @Bean
    public NewTopic testTopic1() {
        NewTopic topic = new NewTopic("oos.push.got.info.test", topicPartitions, (short) topicReplicaPartitions);
        uniComeAdminClient.createTopics(Arrays.asList(topic));
        return topic;
    }
*/


}
