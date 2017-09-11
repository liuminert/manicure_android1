package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class UploadGoodsBean {
    private int goods_id;
    private int goods_attr_id;

    @Override
    public String toString() {
        return "UploadGoodsBean{" +
                "goods_id=" + goods_id +
                ", goods_attr_id=" + goods_attr_id +
                '}';
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public int getGoods_attr_id() {
        return goods_attr_id;
    }

    public void setGoods_attr_id(int goods_attr_id) {
        this.goods_attr_id = goods_attr_id;
    }
}
