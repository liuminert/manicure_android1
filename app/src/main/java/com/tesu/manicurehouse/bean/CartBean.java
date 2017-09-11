package com.tesu.manicurehouse.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class CartBean implements Serializable{
    private String rec_id;
    private int goods_id;//	商品的id
    private String add_time;//加入时间（考虑是否要增加字段）
    private String goods_name;//商品名称
    private String goods_img;//商品图片
    private String goods_attr_id;//色卡id
    private String attr_value;//色卡值
    private String market_price;//市场价
    private String goods_price;//	本店价
    private String goods_number;//	购买数量
    private String goods_attr;//	商品的属性，中括号里是该属性特有的价格
    private int product_number;
    private int integral;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getProduct_number() {
        return product_number;
    }

    public void setProduct_number(int product_number) {
        this.product_number = product_number;
    }

    public int getAttr_number() {
        return product_number;
    }

    public void setAttr_number(int product_number) {
        this.product_number = product_number;
    }

    public String getGoods_img() {
        return goods_img;
    }

    public void setGoods_img(String goods_img) {
        this.goods_img = goods_img;
    }

    public String getRec_id() {
        return rec_id;
    }

    public void setRec_id(String rec_id) {
        this.rec_id = rec_id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_attr_id() {
        return goods_attr_id;
    }

    public void setGoods_attr_id(String goods_attr_id) {
        this.goods_attr_id = goods_attr_id;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(String goods_number) {
        this.goods_number = goods_number;
    }

    public String getGoods_attr() {
        return goods_attr;
    }

    public void setGoods_attr(String goods_attr) {
        this.goods_attr = goods_attr;
    }
}
