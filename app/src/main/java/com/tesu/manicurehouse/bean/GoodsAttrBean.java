package com.tesu.manicurehouse.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class GoodsAttrBean implements Serializable{
    private int goods_attr_id;
    private String attr_value;
    private String attr_img;
    private boolean isSelected;
    private int goods_id;
    //选择数量
    private int num;
    //库存
    private int product_number;

    private String goods_name;
    private String goods_thumb;
    private float shop_price;
    private int integral;

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    @Override
    public String toString() {
        return "GoodsAttrBean{" +
                "goods_attr_id=" + goods_attr_id +
                ", attr_value='" + attr_value + '\'' +
                ", attr_img='" + attr_img + '\'' +
                ", isSelected=" + isSelected +
                ", goods_id=" + goods_id +
                ", num=" + num +
                ", product_number=" + product_number +
                ", goods_name='" + goods_name + '\'' +
                ", goods_thumb='" + goods_thumb + '\'' +
                ", shop_price=" + shop_price +
                ", integral=" + integral +
                '}';
    }

    public int getGoods_attr_id() {
        return goods_attr_id;
    }

    public void setGoods_attr_id(int goods_attr_id) {
        this.goods_attr_id = goods_attr_id;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public String getAttr_img() {
        return attr_img;
    }

    public void setAttr_img(String attr_img) {
        this.attr_img = attr_img;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getProduct_number() {
        return product_number;
    }

    public void setProduct_number(int product_number) {
        this.product_number = product_number;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_thumb() {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb) {
        this.goods_thumb = goods_thumb;
    }

    public float getShop_price() {
        return shop_price;
    }

    public void setShop_price(float shop_price) {
        this.shop_price = shop_price;
    }
}
