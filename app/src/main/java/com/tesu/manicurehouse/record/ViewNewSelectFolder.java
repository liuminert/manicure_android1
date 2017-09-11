package com.tesu.manicurehouse.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2 0002.
 */
public class ViewNewSelectFolder implements Serializable{
    private List<ViewNewSelectBean> viewNewSelectBeanList;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ViewNewSelectBean> getViewNewSelectBeanList() {
        return viewNewSelectBeanList;
    }

    public void setViewNewSelectBeanList(List<ViewNewSelectBean> viewNewSelectBeanList) {
        this.viewNewSelectBeanList = viewNewSelectBeanList;
    }

    @Override
    public String toString() {
        return "ViewNewSelectFolder{" +
                "viewNewSelectBeanList=" + viewNewSelectBeanList +
                ", path='" + path + '\'' +
                '}';
    }
}

