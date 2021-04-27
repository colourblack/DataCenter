package com.yingf.domain.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yingf Fangjunjin
 */
public class PageResultVO implements Serializable {

    private Integer totalCount;
    private Integer pageSize;
    private Integer totalPage;
    private Integer currPage;
    private List<?> list;

    public PageResultVO(int totalCount, int pageSize, int totalPage, int currPage, List<?> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.currPage = currPage;
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageResultVO{" +
                "totalCount=" + totalCount +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", currPage=" + currPage +
                ", list=" + list +
                '}';
    }

    public Map<String, Object> convertMap(){
        Map<String ,Object> map = new HashMap<>(5);
        map.put("totalCount", this.totalCount);
        map.put("pageSize", this.pageSize);
        map.put("totalPage", this.totalPage);
        map.put("currPage", this.currPage);
        map.put("list", this.list);
        return map;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
