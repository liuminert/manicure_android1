package com.tesu.manicurehouse.huanxin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.model.EaseImageCache;
import com.tesu.manicurehouse.R;

import java.util.List;

/**
 * Created by lzan13 on 2015/11/7 21:00.
 * 商品详情类
 */
public class HuanXinDetailActivity extends HuanXinBaseActivity implements EMEventListener {

    private ImageView mImageView;
    private RelativeLayout rl_tochat;
    private ImageButton mImageButton;
    private Bitmap mBitmap = null;
    private int index = CustomerConstants.INTENT_CODE_IMG_SELECTED_DEFAULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huanxin_detail);

        index = getIntent().getIntExtra(CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY, CustomerConstants.INTENT_CODE_IMG_SELECTED_DEFAULT);

        rl_tochat = (RelativeLayout) findViewById(R.id.rl_tochat);
        mImageButton = (ImageButton) findViewById(R.id.ib_shop_back);
        mImageView = (ImageView) findViewById(R.id.iv_buy);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mBitmap = EaseImageCache.getInstance().get("shop_image_details");
        if(mBitmap==null){
            BitmapFactory.Options opts= new BitmapFactory.Options();
            opts.inSampleSize =2;
            mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.em_shop_image_details);
            EaseImageCache.getInstance().put("shop_image_details", mBitmap);
        }
        if(mBitmap!=null)
            mImageView.setImageBitmap(mBitmap);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HuanXinDetailActivity.this.finish();
            }
        });
        rl_tochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY, index);
                intent.setClass(HuanXinDetailActivity.this, HuanXinLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomerHelper.getInstance().pushActivity(this);
        //register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage});
    }

    @Override
    protected void onStop() {
        super.onStop();
        CustomerHelper.getInstance().popActivity(this);
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                EMMessage message = (EMMessage) event.getData();
                //提示新消息
                CustomerHelper.getInstance().getNotifier().onNewMsg(message);
                break;
            case EventOfflineMessage:
                //处理离线消息
                List<EMMessage> messages = (List<EMMessage>) event.getData();
                //消息提醒或只刷新UI
                CustomerHelper.getInstance().getNotifier().onNewMesg(messages);
                break;
            default:
                break;
        }

    }

}
