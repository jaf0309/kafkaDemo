package com.demo.serviceone.rabbitmq;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import java.io.IOException;


/**
 * 监听订单回传的状态
 *
 * @author lizhao
 * @version V1.0
 * @Package com.yundasys.retailexpress.retaildispatchjob.task
 * @date 2020/4/23 18:13
 */
@Component
public class DispatchedOrderListener {


    @Value("${ordercenter.url.appKey}")
    private String appKey;

    @Value("${ordercenter.url.queryByMobile}")
    private String mobileUrl;


    @Value("${rabbitmq.ybx.routingkey6}")
    private String routingkey;

    @Value("${rabbitmq.ybx.exchange6}")
    private String exchange;


    @RabbitListener(queues = "sd_consume_order_state_queue", containerFactory = "ybxListenerFactory")
    public void process(Message message, Channel channel) throws IOException {
        if (ObjectUtils.isEmpty(message.getBody())) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            return;
        }
        //log.info("监听到订单MQ状态数据：{}", new String(message.getBody(), "UTF-8"));

       try{
        } catch (Exception e) {

            //errorHandles(message, channel, outOrderCode);

            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }




    /**
     * 推送事件
     *
     * @param message
     * @param channel
     * @param key
     * @param orderCompleteInfo
     * @param orderCenterId
     * @param orderCode
     * @throws IOException
     */
    private void pushEvents(Message message, Channel channel, String key, OrderCompleteInfo orderCompleteInfo, String orderCenterId, String orderCode) throws IOException {
        String string = "";
        if (redisService.existsKey(Constants.ORDER_CODE_PREFIX + orderCode)) {
            string = redisService.getValue(Constants.ORDER_CODE_PREFIX + orderCode).toString();
        }
        OrderInfoRequest request = JSONObject.parseObject(JSON.parse(string).toString(), OrderInfoRequest.class);
        String channelId = orderCompleteInfo.getPartnerId();
        OrderContext context = OrderContext.builder().orderCode(orderCode).orderInfoRequest(request)
                .orderCenterId(Long.valueOf(orderCenterId)).orderCompleteInfo(orderCompleteInfo).build();
        if (configService.getUnStandardChannelCodes().contains(channelId))
            eventPublisher.publish(new OrderEvent(context, EventType.DISPATCH_COMPLETE, channelId));
        else
            eventPublisher.publish(new OrderEvent(context, EventType.DISPATCH_COMPLETE));
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        redisService.deleteKey(key);
        elkLogger.info(LogData.builder().setClassName(this.getClass().getName()).data("分单监听事件发送成功，orderCode=" + orderCode).build());
        log.info("消息成功" + context.toString());
    }

    /**
     * 查询业务员工号
     *
     * @param orderCompleteInfo
     * @throws Exception
     */
    private void queryEmpMobile(OrderCompleteInfo orderCompleteInfo) throws Exception {
        if (StringUtils.isEmpty(orderCompleteInfo.getEmpMobile())) {
            DispatcherRequest disp = new DispatcherRequest();
            disp.setAppKey(appKey);
            disp.setGs(orderCompleteInfo.getEmpCompanyCode());
//            disp.setGs("100059");
            disp.setBm(orderCompleteInfo.getEmpCode());
            disp.setPageNo("0");
            disp.setPageSize("3");
            DispatcherInfoResponse resp = orderCenterApi.getDispatchInfoList(disp, mobileUrl);
            if (null != resp.getData()) {
                if (null != resp.getData().get(0)) {
                    if (null != resp.getData().get(0).getDataList()) {
                        DispatcherInfoBO bo = resp.getData().get(0).getDataList().get(0);
                        orderCompleteInfo.setEmpMobile(bo.getDh());
                        orderCompleteInfo.setEmbracePeople(bo.getXm());
                    }
                }
            }
        }
    }

    /**
     * 封装数据
     *
     * @param orderCompleteInfo
     * @param mp
     */
    private void plugOrderData(OrderCompleteInfo orderCompleteInfo, JSONObject mp) {
        if (null != mp.get("order_code")) {
            /** 合作方订单号 */
            orderCompleteInfo.setOrderCode(String.valueOf(mp.get("order_code")));
        }
        if (null != mp.get("customerId")) {
            /** 外部商家标识 */
            orderCompleteInfo.setOuterMallId(String.valueOf(mp.get("customerId")));
        }
        if (null != mp.get("partner_id")) {
            /** 合作方 */
            orderCompleteInfo.setPartnerId(String.valueOf(mp.get("partner_id")));
        }
        if (null != mp.get("mailno")) {
            /** 运单号 */
            orderCompleteInfo.setMailno(String.valueOf(mp.get("mailno")));
        }
        if (null != mp.get("isPrinted")) {
            /** 客户标识（sender_id） */
            orderCompleteInfo.setIsPrinted(String.valueOf(mp.get("isPrinted")));
        }
        if (null != mp.get("ywy_mobile")) {
            /** 业务员编码 */
            orderCompleteInfo.setEmpCode(String.valueOf(mp.get("ywy_mobile")));
        }
        if (null != mp.get("trade_code")) {
            /** 业务员手机号 */
            orderCompleteInfo.setEmpMobile(String.valueOf(mp.get("trade_code")));
        }
        if (null != mp.get("status")) {
            /** 状态 */
            orderCompleteInfo.setOrderStatus(String.valueOf(mp.get("status")));
        }
        if (null != mp.get("order_type1")) {
            /** 订单类型:空字符串||pt=普通订单；bland=线下订单；cod=COD订单；limit=限时物流；ensure=快递保障订单；reverse-逆向物流；oto-OTO订单；insured-保价,topay-到付 */
            orderCompleteInfo.setOrderType(String.valueOf(mp.get("order_type1")));
        }
        if (null != mp.get("goods_name")) {
            /**商品名称**/
            orderCompleteInfo.setGoodsName(String.valueOf(mp.get("goods_name")));
        }
        if (null != mp.get("gs")) {
            /** 取件公司 */
            orderCompleteInfo.setEmpCompanyCode(String.valueOf(mp.get("gs")));
        }
        if (null != mp.get("create_time")) {
            /** 创建时间 */
            orderCompleteInfo.setCreateTime(String.valueOf(mp.get("create_time")));
        }
        if (null != (mp.get("idcard"))) {
            /** 实名身份证号 */
            orderCompleteInfo.setIdCard(String.valueOf(mp.get("idcard")));
        }

        if (null != (mp.get("sender_name"))) {
            /** 发件人 */
            orderCompleteInfo.setSenderName(String.valueOf(mp.get("sender_name")));
        }
        if (null != (mp.get("sender_mobile"))) {
            /** 发件人手机 */
            orderCompleteInfo.setSenderMobile(String.valueOf(mp.get("sender_mobile")));
        }
        if (null != (mp.get("sender_phone"))) {
            /** 发件人电话 */
            orderCompleteInfo.setSenderPhone(String.valueOf(mp.get("sender_phone")));
        }
        if (null != (mp.get("sender_city"))) {
            /** 发件人手机 */
            orderCompleteInfo.setSenderCity(String.valueOf(mp.get("sender_city")));
        }
        if (null != (mp.get("sender_address"))) {
            /** 发件人详细地址 */
            orderCompleteInfo.setSenderAddress(String.valueOf(mp.get("sender_address")));
        }
        if (null != (mp.get("receiver_name"))) {
            /** 收件人 */
            orderCompleteInfo.setReceiverName(String.valueOf(mp.get("receiver_name")));
        }
        if (null != (mp.get("receiver_mobile"))) {
            /** 收件人手机 */
            orderCompleteInfo.setSenderMobile(String.valueOf(mp.get("sender_mobile")));
        }
        if (null != (mp.get("receiver_phone"))) {
            /** 收件人电话 */
            orderCompleteInfo.setReceiverPhone(String.valueOf(mp.get("receiver_phone")));
        }
        if (null != (mp.get("receiver_city"))) {
            /** 收件人所在区域 */
            orderCompleteInfo.setReceiverCity(String.valueOf(mp.get("receiver_city")));
        }
        if (null != (mp.get("receiver_address"))) {
            /** 收件人详细地址 */
            orderCompleteInfo.setReceiverAddress(String.valueOf(mp.get("receiver_address")));
        }
        if (null != (mp.get("receiver_company"))) {
            /** 收件人公司名 */
            orderCompleteInfo.setReceiverCompany(String.valueOf(mp.get("receiver_company")));
        }
        if (null != mp.get("weight")) {
            /** 发件人手机 */
            orderCompleteInfo.setWeight(String.valueOf(mp.get("weight")));
        }
        if (null != mp.get("freight")) {
            /** 运费 */
            orderCompleteInfo.setFreight(String.valueOf(mp.get("freight")));
        }
        if (null != mp.get("other_charges")) {
            /** 其他费用 */
            orderCompleteInfo.setWeight(String.valueOf(mp.get("other_charges")));
        }
        if (null != mp.get("other_charges")) {
            /** 其他费用*/
            orderCompleteInfo.setWeight(String.valueOf(mp.get("other_charges")));
        }
        if (null != mp.get("other_charges")) {
            /** 其他费用 */
            orderCompleteInfo.setWeight(String.valueOf(mp.get("other_charges")));
        }
        if (null != mp.get("embrace_people")) {
            /** 揽件的业务员 */
            orderCompleteInfo.setEmbracePeople(String.valueOf(mp.get("embrace_people")));
        }
        if (null != mp.get("withdrawtype")) {
            /*** 取消类型 */
            orderCompleteInfo.setWithdrawType(OrderCancelType.getOrderOperationType(mp.get("withdrawtype").toString()));
        }
        if (null != mp.get("sender_company")) {
            /** 发件人公司名*/
            orderCompleteInfo.setSenderCompany(String.valueOf(mp.get("sender_company")));
        }
        if (null != mp.get("sender_country")) {
            /** 其他费用 */
            orderCompleteInfo.setSenderCountry(String.valueOf(mp.get("sender_country")));
        }
        if (null != mp.get("receiver_country")) {
            /** 收件人国家ID */
            orderCompleteInfo.setReceiverCountry(String.valueOf(mp.get("receiver_country")));
        }
        if (null != mp.get("prefreight")) {
            /** 预估运费 */
            orderCompleteInfo.setPreFreight(String.valueOf(mp.get("prefreight")));
        }
        if (null != mp.get("grabbill_reward")) {
            /** 抢单金额*/
            orderCompleteInfo.setGrabbillReward(String.valueOf(mp.get("grabbill_reward")));
        }
        if (null != mp.get("premium")) {
            /** 保险费 */
            orderCompleteInfo.setPremium(String.valueOf(mp.get("premium")));
        }
        if (null != mp.get("other_charges")) {
            /** COD金额 */
            orderCompleteInfo.setOtherCharges(String.valueOf(mp.get("other_charges")));
        }
        if (null != mp.get("isDispersedBill")) {
            /** 散单标识 */
            orderCompleteInfo.setIsDispersedBill(String.valueOf(mp.get("isDispersedBill")));
        }
        if (null != mp.get("isJdOrder")) {
            /** 门-京东标识(0不是,1是)*/
            orderCompleteInfo.setIsJdOrder(String.valueOf(mp.get("isJdOrder")));
        }
        if (null != mp.get("premium")) {
            /** 保险费 */
            orderCompleteInfo.setPremium(String.valueOf(mp.get("premium")));
        }
        if (null != mp.get("ywy_sms_send")) {
            /** 是否发送短信(0不发送,1发送中,2发送完成) */
            orderCompleteInfo.setYwySmsSend(String.valueOf(mp.get("ywy_sms_send")));
        }
        if (null != mp.get("split")) {
            /** 散单标识 */
            orderCompleteInfo.setSplit(String.valueOf(mp.get("split")));
        }
        if (null != mp.get("isProtectPrivacy")) {
            /** 隐私保护字段(0表示未保护,1表示已保护)*/
            orderCompleteInfo.setIsProtectPrivacy(String.valueOf(mp.get("isProtectPrivacy")));
        }
        if (null != mp.get("isInterParts")) {
            /** 国际件标识(0不是国际件,1是国际件) */
            orderCompleteInfo.setIsInterParts(String.valueOf(mp.get("isInterParts")));
        }
        if (null != mp.get("longitude")) {
            /** 经度 */
            orderCompleteInfo.setLongitude(String.valueOf(mp.get("longitude")));
        }
        if (null != mp.get("latitude")) {
            /** 散单标识 */
            orderCompleteInfo.setLatitude(String.valueOf(mp.get("latitude")));
        }
        if (null != mp.get("cardInfo")) {
            /** 券信息(卡券号|卡券类型|卡券面值)*/
            orderCompleteInfo.setCardInfo(String.valueOf(mp.get("cardInfo")));
        }
        if (null != mp.get("trade_code")) {
            /** 菜鸟交易号 */
            orderCompleteInfo.setTradeCode(String.valueOf(mp.get("trade_code")));
        }
        if (null != mp.get("tagCode")) {
            /** 菜鸟OTO揽件标签号*/
            orderCompleteInfo.setTagCode(String.valueOf(mp.get("tagCode")));
        }
        if (null != mp.get("isPay")) {
            /** 货拉拉支付状态(0未支付,1已支付,2货拉拉,''表示非货拉拉) */
            orderCompleteInfo.setIsPay(String.valueOf(mp.get("isPay")));
        }
        if (null != mp.get("sendStartTime")) {
            /** 取件起始时间*/
            orderCompleteInfo.setSendStartTime(String.valueOf(mp.get("sendStartTime")));
        }
        if (null != mp.get("sendendtime")) {
            /** 取件截至时间 */
            orderCompleteInfo.setSendEndTime(String.valueOf(mp.get("sendendtime")));
        }

        if (null != mp.get("got_time")) {
            /** 菜鸟OTO揽件标签号*/
            orderCompleteInfo.setGotTime(String.valueOf(mp.get("got_time")));
        }
        if (null != mp.get("pick_upload_time")) {
            /** 上传时间 */
            orderCompleteInfo.setPickUploadTime(String.valueOf(mp.get("pick_upload_time")));
        }
        if (null != mp.get("grabbill_time")) {
            /** 抢单时间*/
            orderCompleteInfo.setGrabbillTime(String.valueOf(mp.get("grabbill_time")));
        }
    }


}