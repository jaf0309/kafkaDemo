package com.test.job.task;


import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author
 *  揽收中台需求 查出订单信息（队列创建） 提供给APP
 */
@Component
public class PushJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushJob.class);

    /**
     * 入口
     * @param param
     * @return
     */
    @XxlJob("demo-job")
    public ReturnT<String> pushApps(String param) {

        System.out.println("执行job**********");
        return ReturnT.SUCCESS;
    }



}
