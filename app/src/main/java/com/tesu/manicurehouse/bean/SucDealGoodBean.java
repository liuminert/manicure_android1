package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class SucDealGoodBean {
    private String order_id;//	订单的id
    private String tel	;//购买商品人的手机号码，要打马赛卡
    private String goods_attr;//	色卡值
    private String add_time	;//订单生成时间
    private String goods_number	;//商品的购买数量

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAttr_value() {
        return goods_attr;
    }

    public void setAttr_value(String attr_value) {
        this.goods_attr = attr_value;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(String goods_number) {
        this.goods_number = goods_number;
    }
}
