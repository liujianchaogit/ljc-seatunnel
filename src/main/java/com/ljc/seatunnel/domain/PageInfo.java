package com.ljc.seatunnel.domain;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("MagicNumber")
public class PageInfo<T> {
    private List<T> data = Collections.emptyList();
    private Integer totalCount = 0;
    private Integer totalPage = 0;
    private Integer pageNo = 1;
    private Integer pageSize = 20;
    /** current page */
    private Integer currentPage = 0;

    public PageInfo() {};

    public PageInfo(Integer currentPage, Integer pageSize) {
        if (currentPage == null) {
            currentPage = 1;
        }
        this.pageNo = (currentPage - 1) * pageSize;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;

        if (pageSize == null || pageSize == 0) {
            pageSize = 20;
        }
        if (this.totalCount % this.pageSize == 0) {
            this.totalPage =
                    this.totalCount / this.pageSize == 0 ? 1 : this.totalCount / this.pageSize;
            return;
        }
        this.totalPage = this.totalCount / this.pageSize + 1;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
