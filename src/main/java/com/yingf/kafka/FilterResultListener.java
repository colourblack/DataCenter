package com.yingf.kafka;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yingf.constant.DataCenterConstant;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author Sara
 */
@Component
public class FilterResultListener {

    final static Logger log = LoggerFactory.getLogger(FilterResultListener.class);

    final DataModelKafkaPublisherContainer dataModelKafkaPublisherContainer;

    @Autowired
    public FilterResultListener(DataModelKafkaPublisherContainer dataModelKafkaPublisherContainer){
        this.dataModelKafkaPublisherContainer = dataModelKafkaPublisherContainer;
    }

    @KafkaListener(groupId = "Java-Cli", topics = "DATA_MODEL_TASK_RESULT")
    public void listenObj(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Payload String msg) {
        log.info("StoreFile 监听器成功监听消息:{}, 来自分区:{}", msg, partition);
        JSONObject jsonResult = JSON.parseObject(msg);
        Integer userId = jsonResult.getInteger("userId");
        String type = jsonResult.getString("type");
        // 判断消息类型
        if (!Strings.isEmpty(type) && type.equals(DataCenterConstant.STORE_TASK_CHANNEL)) {
            // 如果该消息的发送者仍保持连接 (container contains userId)
            if (dataModelKafkaPublisherContainer.contains(userId)) {
                dataModelKafkaPublisherContainer.get(userId).publishMessage(userId, jsonResult);
            } else {
                log.info("用户{} 已经断开了连接", userId);
            }
        }
        if (!Strings.isEmpty(type) && type.equals(DataCenterConstant.FILTER_TASK_CHANNEL)) {
            // 如果该消息的发送者仍保持连接 (container contains userId)
            if (dataModelKafkaPublisherContainer.contains(userId)) {
                dataModelKafkaPublisherContainer.get(userId).publishMessage(userId, jsonResult);
            } else {
                log.info("用户{} 已经断开了连接", userId);
            }
        }
    }

}
