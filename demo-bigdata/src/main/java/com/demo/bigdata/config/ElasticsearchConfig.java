package com.demo.bigdata.config;

import com.alibaba.fastjson.JSON;
import com.demo.bigdata.util.JsonUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;

/**
 * 初始化es客户端配置
 *
 * @author wuqiong
 */
@Configuration
public class ElasticsearchConfig {
    //读取配置文件中信息
    @Value("${elasticsearch.hosts}")
    public String[] hosts;
    @Value("${elasticsearch.ports}")
    public Integer[] ports;
    @Value("${elasticsearch.schemes}")
    public String[] schemes;
    @Value("${elasticsearch.indexTemplateName}")
    public String indexTemplateName;
    @Value("${elasticsearch.indexTemplateJsonFileName}")
    public String indexTemplateJsonFileName;

    private RestHighLevelClient client;
    //日志
    private static Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    /**
     * 初始化客户端
     */
    @PostConstruct
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        try {
            if (client != null) {
                client.close();
            }
            // 创建HttpHost数组
            if (hosts == null || hosts.length <= 0 || ports == null || ports.length <= 0 || schemes == null || schemes.length <= 0) {
                throw new RuntimeException("es信息有误");
            }
            HttpHost[] httpHosts = new HttpHost[hosts.length];
            if (hosts.length != ports.length || hosts.length != schemes.length) {
                for (int i = 0; i < httpHosts.length; i++) {
                    httpHosts[i] = new HttpHost(hosts[i], ports[0], schemes[0]);
                }
            } else {
                for (int i = 0; i < httpHosts.length; i++) {
                    httpHosts[i] = new HttpHost(hosts[i], ports[i], schemes[i]);
                }
            }
            //初始化客户端
            client = new RestHighLevelClient(RestClient.builder(httpHosts));
            logger.info("初始化es客户端 {}", client);
        } catch (IOException e) {
            logger.error("初始化ES客户端错误", e);
            throw new RuntimeException(e);
        }
        return client;
    }

    /**
     * 初始化索引模板
     *
     * @throws IOException
     */
    @Bean
    public void initTemplate() throws IOException {
        // 判断是否需要创建索引模板
        if (StringUtils.isEmpty(indexTemplateName) || StringUtils.isEmpty(indexTemplateJsonFileName)) {
            logger.info("没有索引模板名或模板文件名");
            return;
        }
        if (indexTemplateExist(indexTemplateName)) {
            logger.info("模板已经存在");
            return;
        }
        // 创建索引模板
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(indexTemplateJsonFileName);
        String indexTemplate = JsonUtil.readJsonFile(inputStream);
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(indexTemplateName);
        request.source(indexTemplate, XContentType.JSON);
        AcknowledgedResponse putTemplateResponse = client.indices().putTemplate(request, RequestOptions.DEFAULT);
        if (!putTemplateResponse.isAcknowledged()) {
            throw new RuntimeException("初始化失败");
        } else {
            logger.info("初始化模板成功");
        }
    }

    /**
     * 释放客户端
     */
    @PreDestroy
    public void destroy() {
        try {
            if (client != null) {
                client.close();
                logger.info("关闭es客户端");
            }
        } catch (Exception e) {
            logger.error("释放es客户端失败", e);
            throw new RuntimeException(e);
        }
    }

    public boolean indexTemplateExist(String indexTemplate) throws IOException {
        IndexTemplatesExistRequest request = new IndexTemplatesExistRequest(indexTemplate);
        //Whether to return local information or retrieve the state from master node
        request.setLocal(true);
        return client.indices().existsTemplate(request, RequestOptions.DEFAULT);
    }
}
