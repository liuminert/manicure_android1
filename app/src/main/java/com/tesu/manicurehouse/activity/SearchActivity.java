package com.tesu.manicurehouse.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HistoryAdapter;
import com.tesu.manicurehouse.adapter.SearchAdapter;
import com.tesu.manicurehouse.adapter.ShopAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodBean;
import com.tesu.manicurehouse.protocol.GetGoodListProtocol;
import com.tesu.manicurehouse.protocol.GetInformationDescProtocol;
import com.tesu.manicurehouse.protocol.GetKeywordsProtocol;
import com.tesu.manicurehouse.protocol.SearchGoodsProtocol;
import com.tesu.manicurehouse.request.GetGoodListRequest;
import com.tesu.manicurehouse.request.GetInformationDescRequest;
import com.tesu.manicurehouse.request.GetKeywordsRequest;
import com.tesu.manicurehouse.request.SearchGoodsRequest;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;
import com.tesu.manicurehouse.response.GetGoodListResponse;
import com.tesu.manicurehouse.response.GetInformationDescResponse;
import com.tesu.manicurehouse.response.GetKeywordsResponse;
import com.tesu.manicurehouse.response.SearchGoodsResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 搜索页面
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private PopupWindow pWindow;
    private View root;
    private LayoutInflater mInflater;
    private ImageView iv_top_back;
    private View rootView;
    private TextView tv_sp_name;
    private GridView gv_hot_search;
    private GridView gv_history_search;
    private SearchAdapter searchAdapter;
    private HistoryAdapter historyAdapter;
    private TextView tv_course;
    private TextView tv_good;
    private TextView tv_community;
    private TextView tv_claer_history;
    private PercentRelativeLayout rl_manage_dialog;
    private TextView tv_search;
    private String url;
    private Dialog loadingDialog;
    private String keyword;
    private EditText et_search;
    //1、教程；2、商品；3、社区
    private int type;
    //1、社区；2、教程；3、商品
    private int position;
    //1代表首页,2代表晒美甲
    private int int_type;
    private GetKeywordsResponse getKeywordsResponse;
    private List<String> historyList;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_search, null);
        setContentView(rootView);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        root = mInflater.inflate(R.layout.alert_dialog_manage, null);
        pWindow = new PopupWindow(root, ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);
        et_search= (EditText) rootView.findViewById(R.id.et_search);
        tv_search= (TextView) rootView.findViewById(R.id.tv_search);
        gv_history_search=(GridView)rootView.findViewById(R.id.gv_history_search);
        tv_sp_name = (TextView) rootView.findViewById(R.id.sp_name);
        tv_claer_history= (TextView) rootView.findViewById(R.id.tv_claer_history);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        gv_hot_search=(GridView)rootView.findViewById(R.id.gv_hot_search);
        iv_top_back.setOnClickListener(this);
        tv_course=(TextView)root.findViewById(R.id.tv_course);
        tv_good=(TextView)root.findViewById(R.id.tv_good);
        tv_community=(TextView)root.findViewById(R.id.tv_community);
        rl_manage_dialog=(PercentRelativeLayout)root.findViewById(R.id.rl_manage_dialog);
        initData();
        return null;
    }


    public void initData() {
        Intent intent=getIntent();
        type=intent.getIntExtra("type", -1);
        int_type=intent.getIntExtra("int_type", -1);
        LogUtils.e("int_type:"+int_type);
        position=intent.getIntExtra("position",-1);
        loadingDialog = DialogUtils.createLoadDialog(SearchActivity.this, true);
        switch (type){
            case 1:{
                tv_sp_name.setText("教程");
                position=2;
                break;
            }
            case 2:{
                tv_sp_name.setText("商品");
                position=3;
                break;
            }
            case 3:{
                tv_sp_name.setText("社区");
                position=1;
                break;
            }
            default:break;
        }
        if( !TextUtils.isEmpty(SharedPrefrenceUtils.getString(SearchActivity.this, "history"))){

            String history=SharedPrefrenceUtils.getString(SearchActivity.this, "history");
            historyList=StringUtils.getShareListToHead(history);
            if(historyAdapter==null){
                historyAdapter=new HistoryAdapter(SearchActivity.this,historyList);
            }
            gv_history_search.setAdapter(historyAdapter);
        }

        tv_sp_name.setOnClickListener(this);
        rl_manage_dialog.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tv_good.setOnClickListener(this);
        tv_community.setOnClickListener(this);
        tv_claer_history.setOnClickListener(this);
        tv_course.setOnClickListener(this);
        runAsyncTask();
        gv_history_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (historyList != null) {
                    switch (SearchActivity.this.position){
                        case 2:{
                            Intent intent=new Intent(SearchActivity.this,SearchVideoActivity.class);
                            intent.putExtra("int_type",int_type);
                            intent.putExtra("keyword", historyList.get(position));
                            UIUtils.startActivityNextAnim(intent);
                            break;
                        }
                        case 3:{
                            Intent intent=new Intent(SearchActivity.this,SearchGoodsActivity.class);
                            intent.putExtra("keyword", historyList.get(position));
                            UIUtils.startActivityNextAnim(intent);
                            break;
                        }
                        case 1:{
                            Intent intent=new Intent(SearchActivity.this,SearchCommunityActivity.class);
                            intent.putExtra("keyword", historyList.get(position));
                            UIUtils.startActivityNextAnim(intent);
                            break;
                        }
                        default:break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_course:{
                tv_sp_name.setText("教程");
                position=2;
                pWindow.dismiss();
                break;
            }
            case R.id.tv_good:{
                tv_sp_name.setText("商品");
                position=3;
                pWindow.dismiss();
                break;
            }
            case R.id.tv_community:{
                tv_sp_name.setText("社区");
                position=1;
                pWindow.dismiss();
                break;
            }
            case R.id.tv_claer_history:
                SharedPrefrenceUtils.setString(SearchActivity.this, "history", "");
                if(historyAdapter!=null){
                    historyAdapter.clearDate();
                }
                break;
            case R.id.tv_search:
                keyword=et_search.getText().toString();
                if(!TextUtils.isEmpty(keyword)){
                    if( !TextUtils.isEmpty(SharedPrefrenceUtils.getString(SearchActivity.this, "history"))){
                        String history=SharedPrefrenceUtils.getString(SearchActivity.this, "history");
                        historyList=StringUtils.getShareListToHead(history);
                        if(!historyList.contains(keyword)){
                            history=history+","+keyword;
                            SharedPrefrenceUtils.setString(SearchActivity.this,"history",history);
                        }
                    }
                    else{
                        SharedPrefrenceUtils.setString(SearchActivity.this,"history",keyword);
                    }

                    switch (position){
                        case 2:{
                            Intent intent=new Intent(SearchActivity.this,SearchVideoActivity.class);
                            intent.putExtra("int_type",int_type);
                            intent.putExtra("keyword", keyword);
                            UIUtils.startActivityForResultNextAnim(intent, 1);
                            break;
                        }
                        case 3:{
                            Intent intent=new Intent(SearchActivity.this,SearchGoodsActivity.class);
                            intent.putExtra("keyword", keyword);
                            UIUtils.startActivityForResultNextAnim(intent, 1);
                            break;
                        }
                        case 1:{
                            Intent intent=new Intent(SearchActivity.this,SearchCommunityActivity.class);
                            intent.putExtra("keyword", keyword);
                            UIUtils.startActivityForResultNextAnim(intent, 1);
                            break;
                        }
                        default:break;
                    }

                    finishActivity();
                }else{
                    DialogUtils.showAlertDialog(SearchActivity.this,
                            "请输入搜索词!");
                }

                break;
            case R.id.rl_manage_dialog:
                pWindow.dismiss();
                break;
            case R.id.sp_name:
                if(pWindow.isShowing()){
                    pWindow.dismiss();
                }else {
                    pWindow.showAsDropDown(tv_sp_name);
                }
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetKeywordsProtocol getKeywordsProtocol = new GetKeywordsProtocol();
        GetKeywordsRequest getKeywordsRequest = new GetKeywordsRequest();
        url = getKeywordsProtocol.getApiFun();
        getKeywordsRequest.map.put("position",String.valueOf(position));



        MyVolley.uploadNoFile(MyVolley.POST, url, getKeywordsRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getKeywordsResponse = gson.fromJson(json, GetKeywordsResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getKeywordsResponse.code==0) {
                    loadingDialog.dismiss();
                    setDate(getKeywordsResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(SearchActivity.this,
                            getKeywordsResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(SearchActivity.this, error);
            }
        });
    }
    public void setDate(final List<GetKeywordsResponse.KeywordsBean> keywordsBeanList){
        if(searchAdapter==null){
            searchAdapter=new SearchAdapter(SearchActivity.this,keywordsBeanList);
        }
        gv_hot_search.setAdapter(searchAdapter);
        gv_hot_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("history:"+SharedPrefrenceUtils.getString(SearchActivity.this, "history"));
                if( !TextUtils.isEmpty(SharedPrefrenceUtils.getString(SearchActivity.this, "history"))){
                    String history=SharedPrefrenceUtils.getString(SearchActivity.this, "history");
                    historyList=StringUtils.getShareListToHead(history);
                    if(!historyList.contains(keywordsBeanList.get(position).search_keyword)){
                        history=history+","+keywordsBeanList.get(position).search_keyword;
                        SharedPrefrenceUtils.setString(SearchActivity.this,"history",history);
                    }
                }
                else{
                    LogUtils.e("history:加"+keywordsBeanList.get(position).search_keyword);
                    SharedPrefrenceUtils.setString(SearchActivity.this,"history",keywordsBeanList.get(position).search_keyword);
                }
                switch (SearchActivity.this.position){
                    case 2:{
                        Intent intent=new Intent(SearchActivity.this,SearchVideoActivity.class);
                        intent.putExtra("int_type",int_type);
                        intent.putExtra("keyword",keywordsBeanList.get(position).search_keyword);
                        UIUtils.startActivityForResultNextAnim(intent, 1);
                        break;
                    }
                    case 3:{
                        Intent intent=new Intent(SearchActivity.this,SearchGoodsActivity.class);
                        intent.putExtra("keyword",keywordsBeanList.get(position).search_keyword);
                        UIUtils.startActivityForResultNextAnim(intent, 1);
                        break;
                    }
                    case 1:{
                        Intent intent=new Intent(SearchActivity.this,SearchCommunityActivity.class);
                        intent.putExtra("keyword",keywordsBeanList.get(position).search_keyword);
                        UIUtils.startActivityForResultNextAnim(intent, 1);
                        break;
                    }
                    default:break;
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if( !TextUtils.isEmpty(SharedPrefrenceUtils.getString(SearchActivity.this, "history"))){
                String history=SharedPrefrenceUtils.getString(SearchActivity.this, "history");
                historyList=StringUtils.getShareListToHead(history);
                if(historyAdapter==null){
                    historyAdapter=new HistoryAdapter(SearchActivity.this,historyList);
                    gv_history_search.setAdapter(historyAdapter);
                }
                else{
                    historyAdapter.setDate(historyList);
                }
            }
        }
    }
}
