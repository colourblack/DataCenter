package com.yingf.kafka;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yingf fangjunjin
 * @Description 用于保存kafka的监听器
 */
@Component
public class DataModelKafkaPublisherContainer {

    private static final ConcurrentHashMap<Integer, DataModelWsPublisher> container = new ConcurrentHashMap<>();

    public DataModelWsPublisher put(Integer userId, DataModelWsPublisher listener) {
        return container.put(userId, listener);
    }

    public DataModelWsPublisher get(Integer userId) {
        return container.get(userId);
    }

    public DataModelWsPublisher remove(Integer userId){
        return container.remove(userId);
    }

    public boolean contains(Integer userId){
        return container.containsKey(userId);
    }

}
