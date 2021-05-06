package com.yingf.kafka;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;


/**
 * @author yingf Fangjunjin
 */
public class DataModelWsPublisher {

    final static Logger log = LoggerFactory.getLogger(DataModelWsPublisher.class);

    private Session session;

    private Integer userId;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void publishMessage(Integer userId, JSONObject storeMsg) {
        log.debug("用户: {} 成功收到表结构信息文件的执行结果", userId);
        String msg = storeMsg.toJSONString();
        log.debug("执行结果: {}", msg);
        if (null != this.session && this.session.isOpen()) {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
