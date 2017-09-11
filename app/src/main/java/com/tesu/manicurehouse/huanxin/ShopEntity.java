package com.tesu.manicurehouse.huanxin;

/**
 * Created by lzan13 on 2015/11/25.
 * 商品实体类，模拟商城软件的一些数据，根据传入的参数，获取模拟的某一个商品信息
 * 在多处有调用
 * {@link com.easemob.easeui.customer.activity.DetailActivity}
 * {@link com.easemob.easeui.customer.fragment.ChatFragment} sendTrackMessage中
 */
public class ShopEntity {
    private int item;
    private String title;
    private String desc;
    private String price;
    private String imageUrl;
    private String url;

    public ShopEntity(int item) {
        setItem(item);
        switch (item) {
            case 1:
                setTitle("XXX吸尘器");
                setDesc("质量首选，全国销量第一品牌，累计1000用户选择！");
                setPrice("698￥");
                setImageUrl("http://tools.melove.net/test/image/shop_01.png");
                setUrl("http://lzan13.melove.net/shop/shop_01.html");
                break;
            case 2:
                setTitle("笨精灵惠系列喜庆装无芯卷纸");
                setDesc("原浆纯品 细腻柔韧 温柔呵护 放心安心");
                setPrice("22.22￥");
                setImageUrl("http://tools.melove.net/test/image/shop_02.jpg");
                setUrl("http://lzan13.melove.net/shop/shop_02.html");
                break;
            case 3:
                setTitle("四川安岳柠檬");
                setDesc("四川安岳，柠檬之乡，充足阳光带来的新鲜美味");
                setPrice("88.9￥");
                setImageUrl("http://tools.melove.net/test/image/shop_03.jpg");
                setUrl("http://lzan13.melove.net/shop/shop_03.html");
                break;
            case 4:
                setTitle("精准版电子圆称");
                setDesc("美丽是永恒的主题，体重管理，轻而易举；环保省点 大屏显示 精准传感 感应开关");
                setPrice("24.9￥");
                setImageUrl("http://tools.melove.net/test/image/shop_04.jpg");
                setUrl("http://lzan13.melove.net/shop/shop_04.html");
                break;
            case 5:
                setTitle("璐迪尔温馨加长枕头");
                setDesc("宝宝的优质睡眠，从璐迪尔开始，特别加长");
                setPrice("19.9￥");
                setImageUrl("http://tools.melove.net/test/image/shop_05.jpg");
                setUrl("http://lzan13.melove.net/shop/shop_05.html");
                break;
            case 6:
                setTitle("正宗阳澄湖大闸蟹");
                setDesc("自然的恩赐，豪华礼盒包装，送礼首选");
                setPrice("188.9￥");
                setImageUrl("http://tools.melove.net/test/image/shop_06.jpg");
                setUrl("http://lzan13.melove.net/shop/shop_06.html");
                break;
        }
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
