package com.tesu.manicurehouse.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.fragment.PictrueFragment;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.widget.HackyViewPager;

import java.util.List;

/*	
*显示大图
*/
public class ShowBigPictrue extends FragmentActivity {

    private HackyViewPager viewPager;
    private List<String> imagePath;
    /**
     * 得到上一个界面点击图片的位置
     */
    private int position = 0;
    private TextView tv_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.show_big_pictrue);
        tv_close = (TextView) findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
            }
        });
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        imagePath = intent.getStringArrayListExtra("imagePath");
        initViewPager();
    }

    private void initViewPager() {

        viewPager = (HackyViewPager) findViewById(R.id.viewPager_show_bigPic);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //跳转到第几个界面
        viewPager.setCurrentItem(position);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String path = imagePath.get(position);
            return new PictrueFragment(path,true,true);
        }

        @Override
        public int getCount() {
            return imagePath.size();
        }


    }

}
