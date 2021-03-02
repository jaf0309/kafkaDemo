package com.demo.serviceone.kafka.monitor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.*;


@RestController
public class AppMonitorPushController {
    private static final Logger LOGGER= LoggerFactory.getLogger(AppMonitorPushController.class);

    @Value("${service.kafka.ordercenter.oos.push.app.current}")
    private String topic;
    @Value("${service.kafka.ordercenter.oos.start.time:1800000}")
    private Long consumerTime;
    @Value("${service.kafka.ordercenter.oos.repush.cancel:false}")
    private Boolean cancel;
    @Autowired
    private KafkaProperties kafkaProperties;
    @GetMapping(value = "/app")
    public void appMonitorPush(){
        System.out.println("start**************************");
        KafkaConsumer kafkaConsumer = getKafkaConsumer();
        // 获取topic的partition信息
        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(topic);
        List<TopicPartition> topicPartitions = new ArrayList<>();
        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();


        Map<TopicPartition, Long> map = new HashMap<>();
        //从半小时前开始消费
        long fetchDataTime = System.currentTimeMillis() - consumerTime;

        for (PartitionInfo par : partitionInfos) {

            topicPartitions.add(new TopicPartition(par.topic(), par.partition()));
            timestampsToSearch.put(new TopicPartition(par.topic(), par.partition()), fetchDataTime);

            //map.put(new TopicPartition(topic, par.partition()), fetchDataTime);
        }
        kafkaConsumer.assign(topicPartitions);
        // 获取每个partition规定时间之前的偏移量
        Map<TopicPartition, OffsetAndTimestamp> parMap = kafkaConsumer.offsetsForTimes(timestampsToSearch);
        //遍历
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : parMap.entrySet()) {
            TopicPartition key = entry.getKey();
            OffsetAndTimestamp value = entry.getValue();
            long offset = 0;
            if(!ObjectUtils.isEmpty(value)){
                offset= value.offset();
            }
            LOGGER.info("消费到的partition为{}",key.partition());
            LOGGER.info("消费到的offset为{}",offset== 0L?"":offset);
            //根据消费里的timestamp确定offset
            if (value != null) {
                //没有这行代码会导致下面的报错信息
                //kafkaConsumer.assign(Arrays.asList(key));
                kafkaConsumer.seek(key, offset);
            }
        }
        while(cancel) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(50));
            for (ConsumerRecord<String, String> record : records) {
                if(record.timestamp() >= consumerTime){
                    System.out.println("partition = " + record.partition() + ", offset = " + record.offset()+ ", value = " + record.value());
                }
            }
        }
    }

    private KafkaConsumer getKafkaConsumer(){
        Properties props = new Properties();
        props.put("bootstrap.servers", String.join(",",kafkaProperties.getBootstrapServers()));
        props.put("group.id", kafkaProperties.getConsumer().getGroupId());
        props.put("key.deserializer", kafkaProperties.getConsumer().getKeyDeserializer());
        props.put("value.deserializer",  kafkaProperties.getConsumer().getValueDeserializer());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }
}
