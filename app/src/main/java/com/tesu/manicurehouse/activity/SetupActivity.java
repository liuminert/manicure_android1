package com.tesu.manicurehouse.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.huanxin.CustomerHelper;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 设置页面
 */
public class SetupActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private RelativeLayout rl_person_information;
    private RelativeLayout rl_account_safe;
    private RelativeLayout rl_help;
    private RelativeLayout rl_about;
    private Button btn_out_login;
    private AlertDialog alertDialog;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_setup, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        rl_person_information = (RelativeLayout) rootView.findViewById(R.id.rl_person_information);
        rl_account_safe = (RelativeLayout) rootView.findViewById(R.id.rl_account_safe);
        rl_help = (RelativeLayout) rootView.findViewById(R.id.rl_help);
        rl_about= (RelativeLayout) rootView.findViewById(R.id.rl_about);
        btn_out_login = (Button) rootView.findViewById(R.id.btn_out_login);
        initData();
        return null;
    }


    public void initData() {
        rl_about.setOnClickListener(this);
        btn_out_login.setOnClickListener(this);
        rl_account_safe.setOnClickListener(this);
        tv_top_back.setOnClickListener(this);
        rl_help.setOnClickListener(this);
        rl_person_information.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_about: {
                Intent intent = new Intent(SetupActivity.this, AboutActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_cancle:
                alertDialog.dismiss();
                break;
            case R.id.tv_ensure: {
                alertDialog.dismiss();
                SharedPrefrenceUtils.setBoolean(SetupActivity.this,
                        "isLogin", false);
                SharedPrefrenceUtils.setString(SetupActivity.this, "user_id", "");
                SharedPrefrenceUtils.setString(SetupActivity.this, "avatar", "");
                SharedPrefrenceUtils.setBoolean(SetupActivity.this, "isloaded", false);
                SharedPrefrenceUtils.setString(SetupActivity.this, "alias", "无");
                SharedPrefrenceUtils.setString(SetupActivity.this, "followCnt", String.valueOf(0));
                SharedPrefrenceUtils.setString(SetupActivity.this, "collectionCnt", String.valueOf(0));
                SharedPrefrenceUtils.setString(SetupActivity.this, "worksCnt", String.valueOf(0));
                CustomerHelper.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                Intent intent = new Intent(SetupActivity.this, LoginActivity.class);
                UIUtils.startActivityPreAnim(intent);
                finish();
                break;
            }
            case R.id.btn_out_login: {
                alertDialog = DialogUtils.showAlertDoubleBtnDialog(SetupActivity.this, "确定要退出本账号？", SetupActivity.this);
                break;
            }
            case R.id.rl_help: {
                Intent intent = new Intent(SetupActivity.this, HelpActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_account_safe: {
                setFinish();
                Intent intent = new Intent(SetupActivity.this, AccountSafeActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_person_information: {
                Intent intent = new Intent(SetupActivity.this, PersonalActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }
}
