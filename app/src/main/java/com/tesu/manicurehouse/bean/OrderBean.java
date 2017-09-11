package com.tesu.manicurehouse.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class OrderBean implements Serializable{
    private String order_id;//订单id
    private String order_sn;//	订单号
    private String user_id;    //会员id
    private int order_status;    //订单状态。0，未确认；1，已确认；2，已取消；3，无效；4，退货
    private int shipping_status;//	商品配送情况，0，未发货；1，已发货；2，已收货；3，备货中
    private int pay_status;//	支付状态；0，未付款；1，付款中；2，已付款
    private String consignee;//	收货人的姓名
    private String address;//	收货人的详细地址
    private String tel;//	收货人的电话
    private String shipping_fee;//	配送费用
    private double order_amount;//订单总金额
    private String goods_amount;//商品总金额
    private String add_time;//	订单生成时间
    private List<GoodsDataBean> goodsDataList;//	订单对应的商品列表，见下面的描述
    private String logistics;
    private String shipping_name;
    //是否已经评论0否1是
    private int is_comment;

    public int getIs_comment() {
        return is_comment;
    }

    public void setIs_comment(int is_comment) {
        this.is_comment = is_comment;
    }

    public double getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(double order_amount) {
        this.order_amount = order_amount;
    }

    public String getShipping_name() {
        return shipping_name;
    }

    public void setShipping_name(String shipping_name) {
        this.shipping_name = shipping_name;
    }

    public String getLogistics() {
        return logistics;
    }

    public void setLogistics(String logistics) {
        this.logistics = logistics;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public int getShipping_status() {
        return shipping_status;
    }

    public void setShipping_status(int shipping_status) {
        this.shipping_status = shipping_status;
    }

    public int getPay_status() {
        return pay_status;
    }

    public void setPay_status(int pay_status) {
        this.pay_status = pay_status;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(String shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public String getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(String goods_amount) {
        this.goods_amount = goods_amount;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public List<GoodsDataBean> getGoodsDataList() {
        return goodsDataList;
    }

    public void setGoodsDataList(List<GoodsDataBean> goodsDataList) {
        this.goodsDataList = goodsDataList;
    }
}
