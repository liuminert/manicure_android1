package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class ProductBean {
    private int goods_id;
    private String goods_name;
    private String shop_price;
    private String goods_thumb;
    private boolean isSelected;

    @Override
    public String toString() {
        return "ProductBean{" +
                "goods_id=" + goods_id +
                ", goods_name='" + goods_name + '\'' +
                ", shop_price='" + shop_price + '\'' +
                ", goods_thumb='" + goods_thumb + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    public String getShop_price() {
        return shop_price;
    }

    public void setShop_price(String shop_price) {
        this.shop_price = shop_price;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
