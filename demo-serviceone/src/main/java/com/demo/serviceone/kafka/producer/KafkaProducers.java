package com.demo.serviceone.kafka.producer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 不同kafka系统发送消息
 */
@Component
public class KafkaProducers {

    private static final Logger Logger = LoggerFactory.getLogger(KafkaProducers.class);

   /* @Autowired
    @Resource(name ="oosKafkaTemplate")
    private KafkaTemplate oosKafkaTemplate;

    @Autowired
    @Resource(name ="uniComeKafkaTemplate")
    private KafkaTemplate uniComeKafkaTemplate;*/

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 发送kafka消息
     * oos
     * @param topic
     * @param message
     */
    public void sendKafkaMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    /**
     * 发送消息
     * oos
     * @param key
     * @param data
     * @return
     */
    public Boolean sendMessage(String topic, String key, String data) {
        try {
            kafkaTemplate.send(topic, key, data);
            return true;
        } catch (Exception e) {
            Logger.error("调用sendMessage异常", e);
            return false;
        }
    }

    /**
     * 发送kafka消息
     * unicome
     * @param topic
     * @param message
     */
    public void sendUniComeKafkaMessage(String topic, String message) {
      // uniComeKafkaTemplate.send(topic, message);
    }


}
