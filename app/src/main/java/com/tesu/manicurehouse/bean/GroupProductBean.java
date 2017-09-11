package com.tesu.manicurehouse.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class GroupProductBean {
    private int goods_type_id;
    private String goods_type_name;
    private List<ProductBean> goodsList;

    @Override
    public String toString() {
        return "GroupProductBean{" +
                "goods_type_id=" + goods_type_id +
                ", goods_type_name='" + goods_type_name + '\'' +
                ", goodsList=" + goodsList +
                '}';
    }

    public int getGoods_type_id() {
        return goods_type_id;
    }

    public void setGoods_type_id(int goods_type_id) {
        this.goods_type_id = goods_type_id;
    }

    public String getGoods_type_name() {
        return goods_type_name;
    }

    public void setGoods_type_name(String goods_type_name) {
        this.goods_type_name = goods_type_name;
    }

    public List<ProductBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<ProductBean> goodsList) {
        this.goodsList = goodsList;
    }
}
