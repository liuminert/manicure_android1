package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.CommentsAdapter;
import com.tesu.manicurehouse.adapter.DealsAdapter;
import com.tesu.manicurehouse.adapter.ShareAdapter;
import com.tesu.manicurehouse.adapter.ShopAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.GoodCommentBean;
import com.tesu.manicurehouse.bean.GoodDescBean;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.OrderGoodBean;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.bean.SucDealGoodBean;
import com.tesu.manicurehouse.protocol.AddCartProtocol;
import com.tesu.manicurehouse.protocol.GetGoodCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetGoodDescProtocol;
import com.tesu.manicurehouse.protocol.GetGoodListProtocol;
import com.tesu.manicurehouse.protocol.GetSucDealGoodListProtocol;
import com.tesu.manicurehouse.request.AddCartRequest;
import com.tesu.manicurehouse.request.GetGoodCommentListRequest;
import com.tesu.manicurehouse.request.GetGoodDescRequest;
import com.tesu.manicurehouse.request.GetGoodListRequest;
import com.tesu.manicurehouse.request.GetSucDealGoodListRequest;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.GetGoodCommentListResponse;
import com.tesu.manicurehouse.response.GetGoodDescResponse;
import com.tesu.manicurehouse.response.GetGoodListResponse;
import com.tesu.manicurehouse.response.GetGoodsAttrResponse;
import com.tesu.manicurehouse.response.GetSucDealGoodListResponse;
import com.tesu.manicurehouse.share.OnekeyShare;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.tabpager.CommentsPager;
import com.tesu.manicurehouse.tabpager.DealsPager;
import com.tesu.manicurehouse.tabpager.DetailsPager;
import com.tesu.manicurehouse.tabpager.HomeMostNewPager;
import com.tesu.manicurehouse.util.BitmapBlurUtils;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MobileUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyListView;
import com.tesu.manicurehouse.widget.NoScrollViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 商品详情
 */
public class GoodInfoActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {
    private MyListView lv_deal;
    private WebView wv_desc;
    private LinearLayout ll_deals;
    private DealsAdapter dealsAdapter;
    private CommentsAdapter commentsAdapter;
    private static final String TAG = GoodInfoActivity.class.getSimpleName();
    private int SELECTCOLOR = 1;
    private ImageView iv_shopcar;
    private ScrollView sv;
    private Button btn_details;
    private Button btn_comments;
    private Button btn_deal;
    private ImageView iv_share;
    private ImageView iv_top_back;
    private TextView tv_bug;
    private TextView tv_add_shopcar;
    private TextView tv_empty;
    /**
     * 关注的内容viewpager
     */
//    private NoScrollViewPager vpContent;
    //     */
    private List<String> pagerTitles;
    /**
     * 存放商场,品牌,喜欢我，我喜欢页面的集合
     */
    private List<ViewTabBasePager> concernBasePagerList;
    private ShareAdapter sadapter;
    private GridView gv_share;
    private PercentLinearLayout rl_share;
    private PercentRelativeLayout rl_num;
    private TextView tv_cancle;
    private RelativeLayout rl_more_color;
    private View line4;
    private int good_id;
    private String url;
    private Dialog loadingDialog;
    private MyListView lv_comment;
    private GetGoodDescResponse getGoodDescResponse;
    private GetGoodsAttrResponse getGoodsAttrResponse;
    private AddCartResponse addCartResponse;
    //商品名
    private TextView tv_title;
    //商品简介
    private TextView tv_good_describe;
    //规格
    private TextView tv_selected_spec;
    //价格
    private TextView tv_good_price;
    //库存
    private TextView tv_good_storage;
    private ImageView iv_good_image;
    private List<GoodsAttrBean> goodAttrList = new ArrayList<GoodsAttrBean>();
    private TextView tv_reduce;
    private TextView tv_add;
    private EditText et_num;
    //选择的数量
    private String goods_num;
    private int good_storage;
    //商品属性的id
    private String goods_attr_id;
    //该具体属性的值(色卡值)
    private String attr_value;
    private Dialog mDialog;

    private RelativeLayout rl_good_protection;
    private TextView tv_olde_price;
    private View line2;
    private String shareUrl;
    private ShareDateBean shareDateBean;
    //css显示图片样式
    private String CSS_STYPE = "<head><style>img{max-width:340px !important;}</style></head>";
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_good_info, null);
        setContentView(rootView);
        tv_empty=(TextView)rootView.findViewById(R.id.tv_empty);
        line4=(View)rootView.findViewById(R.id.line4);
        rl_num=(PercentRelativeLayout)rootView.findViewById(R.id.rl_num);
        lv_deal=(MyListView)rootView.findViewById(R.id.lv_deal);
        ll_deals=(LinearLayout)rootView.findViewById(R.id.ll_deals);
        wv_desc=(WebView)rootView.findViewById(R.id.wv_desc);
        lv_comment=(MyListView)rootView.findViewById(R.id.lv_comment);
        line2= (View) rootView.findViewById(R.id.line2);
        tv_olde_price= (TextView) rootView.findViewById(R.id.tv_olde_price);
        et_num = (EditText) rootView.findViewById(R.id.et_num);
        tv_add = (TextView) rootView.findViewById(R.id.tv_add);
        tv_reduce = (TextView) rootView.findViewById(R.id.tv_reduce);
        tv_bug = (TextView) rootView.findViewById(R.id.tv_bug);
        tv_add_shopcar = (TextView) rootView.findViewById(R.id.tv_add_shopcar);
        sv = (ScrollView) rootView.findViewById(R.id.sv);
//        vpContent = (NoScrollViewPager) rootView.findViewById(R.id.no_scroll_viewpager);
        iv_share = (ImageView) rootView.findViewById(R.id.iv_share);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        iv_shopcar = (ImageView) rootView.findViewById(R.id.iv_shopcar);
        btn_details = (Button) rootView.findViewById(R.id.btn_details);
        btn_comments = (Button) rootView.findViewById(R.id.btn_comments);
        iv_good_image = (ImageView) rootView.findViewById(R.id.iv_good_image);
        btn_deal = (Button) rootView.findViewById(R.id.btn_deal);
        gv_share = (GridView) rootView.findViewById(R.id.gv_share);
        tv_cancle = (TextView) rootView.findViewById(R.id.tv_cancle);
        rl_share = (PercentLinearLayout) rootView.findViewById(R.id.rl_share);
        rl_more_color = (RelativeLayout) rootView.findViewById(R.id.rl_more_color);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_good_describe = (TextView) rootView.findViewById(R.id.tv_good_describe);
        tv_selected_spec = (TextView) rootView.findViewById(R.id.tv_selected_spec);
        tv_good_price = (TextView) rootView.findViewById(R.id.tv_good_price);
        tv_good_storage = (TextView) rootView.findViewById(R.id.tv_good_storage);
        rl_good_protection = (RelativeLayout) rootView.findViewById(R.id.rl_good_protection);

        btn_details.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    vpContent.setCurrentItem(0);
                    tv_empty.setVisibility(View.GONE);
                    wv_desc.setVisibility(View.VISIBLE);
                    lv_comment.setVisibility(View.GONE);
                    ll_deals.setVisibility(View.GONE);
                }
            }
        });
        btn_comments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    wv_desc.setVisibility(View.GONE);
                    lv_comment.setVisibility(View.VISIBLE);
                    ll_deals.setVisibility(View.GONE);
                    runGetGoodCommentList();
//                    vpContent.setCurrentItem(1);
//                    concernBasePagerList.get(1).initData();
                }
            }
        });
        btn_deal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    vpContent.setCurrentItem(2);
//                    concernBasePagerList.get(2).initData();
                    tv_empty.setVisibility(View.GONE);
                    wv_desc.setVisibility(View.GONE);
                    lv_comment.setVisibility(View.GONE);
                    ll_deals.setVisibility(View.VISIBLE);
                    runSucDealGoodList();
                }
            }
        });
        rl_more_color.setOnClickListener(this);
        tv_bug.setOnClickListener(this);
        tv_add_shopcar.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        iv_shopcar.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_reduce.setOnClickListener(this);
        rl_good_protection.setOnClickListener(this);
        initData();
        return null;
    }

    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        tv_cancle.setOnClickListener(this);
        loadingDialog = DialogUtils.createLoadDialog(GoodInfoActivity.this, true);
        Intent intent = getIntent();
        good_id = intent.getIntExtra("goods_id", -1);
        LogUtils.e("广告后:"+good_id);
        //设置分享数据
        List<ShareInfo> slist = new ArrayList<ShareInfo>();
        ShareInfo share1 = new ShareInfo();
        share1.setShare_photo(R.drawable.img_qq);
        share1.setShare_photo_selected(R.drawable.img_qq);
        share1.setShare_name(QQ.NAME);
        slist.add(share1);

        ShareInfo share3 = new ShareInfo();
        share3.setShare_photo(R.drawable.img_qzone);
        share3.setShare_photo_selected(R.drawable.img_qzone);
        share3.setShare_name(QZone.NAME);
        slist.add(share3);

        ShareInfo share2 = new ShareInfo();
        share2.setShare_photo(R.drawable.img_sina);
        share2.setShare_photo_selected(R.drawable.img_sina);
        share2.setShare_name(SinaWeibo.NAME);
        slist.add(share2);

        ShareInfo share4 = new ShareInfo();
        share4.setShare_photo(R.drawable.icon_share_circle);
        share4.setShare_photo_selected(R.drawable.icon_share_circle);
        share4.setShare_name(WechatMoments.NAME);
        slist.add(share4);

        ShareInfo share5 = new ShareInfo();
        share5.setShare_photo(R.drawable.icon_share_wechat);
        share5.setShare_photo_selected(R.drawable.icon_share_wechat);
        share5.setShare_name(Wechat.NAME);
        slist.add(share5);
        sadapter = new ShareAdapter(slist, rl_share, this, true,this);
        gv_share.setAdapter(sadapter);
        sv.smoothScrollTo(0, 0);
        runAsyncTask();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(GoodInfoActivity.this, "分享成功!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(GoodInfoActivity.this, "分享失败!" + throwable,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(GoodInfoActivity.this, "分享取消!",
                Toast.LENGTH_SHORT).show();
    }

    private class ConcernInfoPagerAdapter extends PagerAdapter {
        @Override
        public CharSequence getPageTitle(int position) {

            return pagerTitles.get(position);
        }

        @Override
        public int getCount() {
            return pagerTitles.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewTabBasePager concernBasePager = concernBasePagerList
                    .get(position);
            View rootView = concernBasePager.getRootView();
//            container.removeView(rootView);
            container.addView(rootView);

            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reduce: {
                int good_num = 1;
                goods_num = et_num.getText().toString();
                if (TextUtils.isEmpty(goods_num)) {
                    et_num.setText("1");
                } else {
                    if(goods_num.equals("0")){
                        et_num.setText("1");
                    }
                    else {
                        good_num = Integer.parseInt(goods_num);
                    }
                }
                good_num = good_num - 1;
                if (good_num == 0) {
                    good_num = 1;
                    et_num.setText(String.valueOf(good_num));

                } else {
                    et_num.setText("" + good_num);
                }
                break;
            }
            case R.id.tv_add: {
                int good_num = 1;
                goods_num = et_num.getText().toString();
                if (TextUtils.isEmpty(goods_num)) {
                    et_num.setText("1");
                } else {
                    good_num = Integer.parseInt(goods_num);
                }

                if (good_num < good_storage) {
                    good_num = good_num + 1;
                    goods_num = String.valueOf(good_num);
                } else {
                    DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            "购买数量不能大于库存" + good_storage + "份");
                    goods_num = "" + good_storage;
                }
                et_num.setText(goods_num);
                break;
            }
            case R.id.rl_more_color: {
                if (SharedPrefrenceUtils.getBoolean(GoodInfoActivity.this, "isLogin")) {
                    Intent intent = new Intent(GoodInfoActivity.this, MoreColorActivity.class);
                    intent.putExtra("integral", getGoodDescResponse.goodJson.getIntegral());
                    intent.putExtra("goods_name", getGoodDescResponse.goodJson.getGoods_name());
                    intent.putExtra("shop_price", getGoodDescResponse.goodJson.getShop_price());
                    intent.putExtra("goods_img", getGoodDescResponse.goodJson.getGoods_img());
                    intent.putExtra("goods_id", String.valueOf(good_id));
                    UIUtils.startActivityForResultNextAnim(intent, SELECTCOLOR);
                }
                else{
                    mDialog =DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(GoodInfoActivity.this,LoginActivity.class);
                                    intent.putExtra("type",1);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_cancle:
                rl_share.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_share:
                if (SharedPrefrenceUtils.getBoolean(GoodInfoActivity.this, "isLogin")) {
                    shareUrl= Constants.SHARE_URL+"share/goods_share.shtml?share_user_id="+SharedPrefrenceUtils.getString(GoodInfoActivity.this,"user_id")+"&goods_id="+good_id;
                    shareDateBean.setUrl(shareUrl);
                    rl_share.setVisibility(View.VISIBLE);
                    // 初始化ShareSDK
                    ShareSDK.initSDK(UIUtils.getContext());
                }
                else{
                    mDialog =DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(GoodInfoActivity.this,LoginActivity.class);
                                    intent.putExtra("type",1);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.tv_bug: {
                if (SharedPrefrenceUtils.getBoolean(GoodInfoActivity.this, "isLogin")) {
                    if(getGoodDescResponse.goodJson.getGoodAttrList().size()>0&&TextUtils.isEmpty(attr_value)){
                        DialogUtils.showAlertDialog(GoodInfoActivity.this,
                                "请先选择色卡!");
                    }
                    else{
                        int goods_num = Integer.parseInt(et_num.getText().toString());

                        if (goods_num <= good_storage) {
                            if(good_storage>0) {
                                Intent intent = new Intent(GoodInfoActivity.this, MakesureOrderActivity.class);
                                List<OrderGoodBean> orderGoodBeanList = new ArrayList<OrderGoodBean>();
                                OrderGoodBean orderGoodBean = new OrderGoodBean();
                                orderGoodBean.setGoods_number(String.valueOf(goods_num));
                                orderGoodBean.setAttr_value(attr_value);
                                orderGoodBean.setShop_price(Double.parseDouble(getGoodDescResponse.goodJson.getShop_price()));
                                orderGoodBean.setTotal_price(Integer.parseInt(et_num.getText().toString()) * Double.parseDouble(getGoodDescResponse.goodJson.getShop_price()));
                                orderGoodBean.setGoods_attr_id(goods_attr_id);
                                orderGoodBean.setGoods_id(String.valueOf(good_id));
                                orderGoodBean.setGoods_img(getGoodDescResponse.goodJson.getGoods_img());
                                orderGoodBean.setGoods_name(getGoodDescResponse.goodJson.getGoods_name());
                                orderGoodBean.setIntegral(getGoodDescResponse.goodJson.getIntegral()  );
                                orderGoodBeanList.add(orderGoodBean);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("orderGoodBeanList", (Serializable) orderGoodBeanList);
                                intent.putExtras(bundle);
                                intent.putExtra("fee", 0);
                                intent.putExtra("is_normal",0);
                                UIUtils.startActivityNextAnim(intent);
                            }else {
                                DialogUtils.showAlertDialog(GoodInfoActivity.this,
                                        "库存为0，无法购买！");
                            }

                        } else {
                            DialogUtils.showAlertDialog(GoodInfoActivity.this,
                                    "购买数量不能大于库存" + good_storage + "份");
                            goods_num = good_storage;
                            et_num.setText(""+goods_num);
                        }

                    }
                } else {
                    mDialog =DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(GoodInfoActivity.this,LoginActivity.class);
                                    intent.putExtra("type",1);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_add_shopcar: {
                if (SharedPrefrenceUtils.getBoolean(GoodInfoActivity.this, "isLogin")) {
                    if(getGoodDescResponse.goodJson.getGoodAttrList().size()>0&&TextUtils.isEmpty(attr_value)){
                        DialogUtils.showAlertDialog(GoodInfoActivity.this,
                                "请先选择色卡!");
                    }
                    else {
                        int goods_num = Integer.parseInt(et_num.getText().toString());

                        if (goods_num <=good_storage) {
                            if(good_storage>0) {
                                runAddCart();
                            }else {
                                DialogUtils.showAlertDialog(GoodInfoActivity.this,
                                        "库存为0，无法添加到购物车！");
                            }
                        } else {
                            DialogUtils.showAlertDialog(GoodInfoActivity.this,
                                    "购买数量不能大于库存" + good_storage + "份");
                            goods_num = good_storage;
                            et_num.setText(""+goods_num);
                        }

                    }
                } else {
                    mDialog =DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(GoodInfoActivity.this,LoginActivity.class);
                                    intent.putExtra("type",1);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.iv_shopcar: {
                Intent intent = new Intent(GoodInfoActivity.this, ShoppingCarActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_good_protection:
                Intent intent = new Intent(GoodInfoActivity.this,SaleSupportActivity.class);
                startActivity(intent);
                break;
        }
    }

    //增加到购物车
    public void runAddCart() {
        loadingDialog.show();
        AddCartProtocol addCartProtocol = new AddCartProtocol();
        AddCartRequest addCartRequest = new AddCartRequest();
        url = addCartProtocol.getApiFun();
        addCartRequest.map.put("goods_id", String.valueOf(good_id));
        addCartRequest.map.put("user_id", SharedPrefrenceUtils.getString(GoodInfoActivity.this, "user_id"));
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject json = new JSONObject();
            json.put("goods_attr_id", goods_attr_id);
            json.put("goods_attr", attr_value);
            json.put("goods_number", et_num.getText().toString());
            jsonArray.put(json);
            addCartRequest.map.put("goodsAttrList", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.uploadNoFile(MyVolley.POST, url, addCartRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e(TAG + json);
                Gson gson = new Gson();
                addCartResponse = gson.fromJson(json, AddCartResponse.class);
                if (addCartResponse.code == 0) {
                    LogUtils.e("json:" + json);
                    loadingDialog.dismiss();
                    Toast.makeText(GoodInfoActivity.this, "已添加成功!", Toast.LENGTH_SHORT).show();
                    iv_shopcar.setVisibility(View.VISIBLE);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            addCartResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(GoodInfoActivity.this, error);
            }
        });
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetGoodDescProtocol getGoodDescProtocol = new GetGoodDescProtocol();
        GetGoodDescRequest getGoodDescRequest = new GetGoodDescRequest();
        url = getGoodDescProtocol.getApiFun();
        getGoodDescRequest.map.put("goods_id", String.valueOf(good_id));


        MyVolley.uploadNoFile(MyVolley.POST, url, getGoodDescRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("详情"+json);
                Gson gson = new Gson();
                getGoodDescResponse = gson.fromJson(json, GetGoodDescResponse.class);
                if (getGoodDescResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getGoodDescResponse.goodJson);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            getGoodDescResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(GoodInfoActivity.this, error);
            }
        });
    }

    public void setDate(GoodDescBean goodBeans) {
        shareUrl= Constants.SHARE_URL+"share/goods_share.shtml?share_user_id="+SharedPrefrenceUtils.getString(GoodInfoActivity.this,"user_id")+"&goods_id="+good_id;
        shareDateBean=new ShareDateBean();
        shareDateBean.setContent("我在美甲屋发现一个不错的商品，赶快来看看吧。");
        shareDateBean.setImageurl(goodBeans.getGoods_img());
        shareDateBean.setTitle(goodBeans.getGoods_name());
        shareDateBean.setUrl(shareUrl);
        sadapter.setShareDateBean(shareDateBean);
        pagerTitles = new ArrayList<String>();
        pagerTitles.add("最热");
        pagerTitles.add("最快");
        pagerTitles.add("最新");

        concernBasePagerList = new ArrayList<ViewTabBasePager>();
        DetailsPager detailsPager = new DetailsPager(GoodInfoActivity.this, goodBeans.getGoods_desc());
        CommentsPager commentsPager = new CommentsPager(GoodInfoActivity.this, sv, String.valueOf(good_id));
        DealsPager dealsPager = new DealsPager(GoodInfoActivity.this, sv, String.valueOf(good_id));

        concernBasePagerList.add(detailsPager);
        concernBasePagerList.add(commentsPager);
        concernBasePagerList.add(dealsPager);

        ConcernInfoPagerAdapter concerninfopageradapter = new ConcernInfoPagerAdapter();
//        vpContent.setAdapter(concerninfopageradapter);
        concernBasePagerList.get(0).initData();
        tv_title.setText(goodBeans.getGoods_name());
        tv_good_describe.setText("商品简介:" + goodBeans.getGoods_brief());
        tv_selected_spec.setText(goodBeans.getMilliliters());
        tv_good_price.setText("￥" + goodBeans.getShop_price());
        tv_olde_price.setText("￥" + goodBeans.getMarket_price());
        tv_olde_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        good_storage = goodBeans.getGoods_number();
        tv_good_storage.setText("库存:" + goodBeans.getGoods_number());
        wv_desc.loadDataWithBaseURL(null, CSS_STYPE + goodBeans.getGoods_desc(), "text/html", "utf-8", null);
        lv_comment.setVisibility(View.GONE);
        ll_deals.setVisibility(View.GONE);
        ImageLoader.getInstance().displayImage(goodBeans.getGoods_img(), iv_good_image, PictureOption.getSimpleOptions());
        if (goodBeans.getGoodAttrList().size() > 0) {
            rl_num.setVisibility(View.GONE);
            //创建RelativeLayout.LayoutParams
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //添加规则  1为tv1的id
            params.topMargin= BitmapBlurUtils.dip2px(GoodInfoActivity.this,5);
            params.addRule(RelativeLayout.BELOW, R.id.line1);
            //将tv2加入布局 传入参数params
            rl_more_color.setLayoutParams(params);

        }
        else{
            line4.setVisibility(View.GONE);
            rl_more_color.setVisibility(View.GONE);
            //创建RelativeLayout.LayoutParams
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //添加规则  1为tv1的id
            params.topMargin= BitmapBlurUtils.dip2px(GoodInfoActivity.this,5);
            params.addRule(RelativeLayout.BELOW, R.id.line3);
            //将tv2加入布局 传入参数params
            rl_good_protection.setLayoutParams(params);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTCOLOR) {
            if (resultCode == 2) {
                Toast.makeText(GoodInfoActivity.this, "已添加成功!", Toast.LENGTH_SHORT).show();
                iv_shopcar.setVisibility(View.VISIBLE);
            } else if (resultCode == 3) {
                finish();
            }
        }
    }


    //获取商品评论
    public void runGetGoodCommentList() {
            loadingDialog.show();
        GetGoodCommentListProtocol getGoodCommentListProtocol = new GetGoodCommentListProtocol();
        GetGoodCommentListRequest getGoodCommentListRequest = new GetGoodCommentListRequest();
        url = getGoodCommentListProtocol.getApiFun();
        getGoodCommentListRequest.map.put("goods_id",String.valueOf(good_id) );
        getGoodCommentListRequest.map.put("pageNo", "1");
        getGoodCommentListRequest.map.put("pageSize", "100");
        MyVolley.uploadNoFile(MyVolley.POST, url, getGoodCommentListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                Gson gson = new Gson();
                GetGoodCommentListResponse getGoodCommentListResponse = gson.fromJson(json, GetGoodCommentListResponse.class);
                if (getGoodCommentListResponse.code == 0) {
                    LogUtils.e("json:" + json);
                    loadingDialog.dismiss();
                    setGoodComment(getGoodCommentListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            getGoodCommentListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(GoodInfoActivity.this, error);
            }
        });
    }
    public void setGoodComment(List<GoodCommentBean> goodCommentBeanList){
        if(goodCommentBeanList.size()==0){
            tv_empty.setVisibility(View.VISIBLE);
        }
        else{
            tv_empty.setVisibility(View.GONE);
            if (commentsAdapter == null) {
                commentsAdapter = new CommentsAdapter(GoodInfoActivity.this, goodCommentBeanList);
                lv_comment.setAdapter(commentsAdapter);
            } else {
                commentsAdapter.setDate(goodCommentBeanList);
            }
        }
    }

    //获取商品成交记录
    public void runSucDealGoodList() {
        loadingDialog.show();

        GetSucDealGoodListProtocol getSucDealGoodListProtocol = new GetSucDealGoodListProtocol();
        GetSucDealGoodListRequest getSucDealGoodListRequest = new GetSucDealGoodListRequest();
        url = getSucDealGoodListProtocol.getApiFun();
        getSucDealGoodListRequest.map.put("goods_id", String.valueOf(good_id));
        getSucDealGoodListRequest.map.put("pageNo", "1");
        getSucDealGoodListRequest.map.put("pageSize", "100");
        MyVolley.uploadNoFile(MyVolley.POST, url, getSucDealGoodListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                Gson gson = new Gson();
                GetSucDealGoodListResponse getSucDealGoodListResponse = gson.fromJson(json, GetSucDealGoodListResponse.class);
                if (getSucDealGoodListResponse.code == 0) {
                    LogUtils.e("json:" + json);
                    loadingDialog.dismiss();
                    setSucDealGoodList(getSucDealGoodListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(GoodInfoActivity.this,
                            getSucDealGoodListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(GoodInfoActivity.this, error);
            }
        });
    }
    public void setSucDealGoodList(List<SucDealGoodBean> sucDealGoodList){
        if(dealsAdapter==null) {
            dealsAdapter = new DealsAdapter(GoodInfoActivity.this, sucDealGoodList);
            lv_deal.setAdapter(dealsAdapter);
        }
        else{
            dealsAdapter.setDate(sucDealGoodList);
        }
    }
}
