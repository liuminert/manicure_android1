package com.tesu.manicurehouse.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class GoodDescBean implements Serializable{
    private int goods_id	;//商品的id
    private String goods_sn;//	商品的唯一货号
    private String goods_name	;//商品的名称
    private int goods_number;	//商品库存数量
    private String goods_brief;//	商品的简短描述
    private String goods_desc;//	商品的详细描述
    private String goods_thumb	;//商品在前台显示的微缩图片，如在分类筛选时显示的小图片
    private String goods_img	;//商品的实际大小图片，如进入该商品页时介绍商品属性所显示的大图片
    private String original_img	;//应该是上传的商品的原始图片
    private String market_price	;//市场售价
    private String shop_price	;//本店售价
    private String promote_price	;//促销价格
    private String milliliters	;//规格
    private List<GoodsAttrBean> goodAttrList;//色卡组
    private int integral;

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public List<GoodsAttrBean> getGoodAttrList() {
        return goodAttrList;
    }

    public void setGoodAttrList(List<GoodsAttrBean> goodAttrList) {
        this.goodAttrList = goodAttrList;
    }

    public String getGoods_brief() {
        return goods_brief;
    }

    public void setGoods_brief(String goods_brief) {
        this.goods_brief = goods_brief;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_sn() {
        return goods_sn;
    }

    public void setGoods_sn(String goods_sn) {
        this.goods_sn = goods_sn;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public int getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(int goods_number) {
        this.goods_number = goods_number;
    }

    public String getGoods_desc() {
        return goods_desc;
    }

    public void setGoods_desc(String goods_desc) {
        this.goods_desc = goods_desc;
    }

    public String getGoods_thumb() {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb) {
        this.goods_thumb = goods_thumb;
    }

    public String getGoods_img() {
        return goods_img;
    }

    public void setGoods_img(String goods_img) {
        this.goods_img = goods_img;
    }

    public String getOriginal_img() {
        return original_img;
    }

    public void setOriginal_img(String original_img) {
        this.original_img = original_img;
    }

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }

    public String getShop_price() {
        return shop_price;
    }

    public void setShop_price(String shop_price) {
        this.shop_price = shop_price;
    }

    public String getPromote_price() {
        return promote_price;
    }

    public void setPromote_price(String promote_price) {
        this.promote_price = promote_price;
    }

    public String getMilliliters() {
        return milliliters;
    }

    public void setMilliliters(String milliliters) {
        this.milliliters = milliliters;
    }
}
