package com.yingf.domain.filter;

import java.io.Serializable;
import java.util.Map;

/**
 * 过滤任务的实体类
 * 用于展示每一次添加的过滤任务信息
 *
 * @author yingf Fangjunjin
 */

public class FilterTaskArgs implements Serializable {

    /**
     * 任务信息 - 任务头
     */
    private String header;

    /**
     * 任务信息 - 任务信息简要
     */
    private String content;

    /**
     * 任务信息 - 任务创建时间
     */
    private String dateTime;

    /**
     * 任务信息 - 任务基本信息
     */
    private Map<String, Object> hidden;

    public FilterTaskArgs() {

    }

    public FilterTaskArgs(String header, String content, String dateTime, Map<String, Object> hidden) {
        this.header = header;
        this.content = content;
        this.dateTime = dateTime;
        this.hidden = hidden;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Map<String, Object> getHidden() {
        return hidden;
    }

    public void setHidden(Map<String, Object> hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "FilterTaskArgs{" +
                "header='" + header + '\'' +
                ", content='" + content + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", hidden=" + hidden.toString() +
                '}';
    }
}
