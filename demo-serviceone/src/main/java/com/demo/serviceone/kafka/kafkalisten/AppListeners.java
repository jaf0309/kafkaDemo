package com.demo.serviceone.kafka.kafkalisten;



import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author zhujianfeng
 *  发送给小哥门户
 */
@Component
public class AppListeners {

    private static final Logger LOGGER= LoggerFactory.getLogger(AppListeners.class);



    /**
     * 发送给小哥门户
     * @param record
     */
    @KafkaListener(topics = "centerorder.oos.app.topic.test", groupId = "one")
    public void receiveData(ConsumerRecord<String, String> record) {
        String traceId = "";
        System.out.println(record.value());
        String phone = "";

     /*   //转化数据
        try {
            JSONObject jsonObject = JSON.parseObject(record.value());
            phone = jsonObject.getString("phone");
            LOGGER.info("消费接受到的电话号码是：{}",phone);
            try{
                traceId = jsonObject.getString("traceId");
            } catch (Exception e){
                LOGGER.error("转换traceId异常：{}",e.getMessage());
                throw new UnknownException(e);
            }
        } catch (Exception e) {
            LOGGER.error("转换手机号码推送给揽收系统发生异常：{}, traceId:{}",e,traceId);
            dingDingService.sendMessage(dingDingService.buildDingDingMessage("转换手机号码推送给揽收系统发生异常", JSON.toJSONString(record.value()), e));
            //phone = JSON.parseObject(record.value(), String.class);
        }
        OosOrderInfo oosOrderInfo = new OosOrderInfo();
        if(StringUtils.isEmpty(phone)){
            oosOrderInfo.setInfoContent("业务员电话为空");
        }
        LOGGER.info("monitor发送的电话号码为【{}】",phone);
        appService.sendToApp(phone);*/
    }
}
