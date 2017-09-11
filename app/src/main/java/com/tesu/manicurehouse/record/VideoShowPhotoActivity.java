package com.tesu.manicurehouse.record;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.LoginActivity;
import com.tesu.manicurehouse.adapter.GoodsAttrAdapter;
import com.tesu.manicurehouse.adapter.GroupProductAdapter;
import com.tesu.manicurehouse.adapter.SelectedGoodsAttrAdapter;
import com.tesu.manicurehouse.adapter.ShowPhotoContentAdapter;
import com.tesu.manicurehouse.adapter.VideoUploadClassAdapter;
import com.tesu.manicurehouse.adapter.VideoUploadLableAdapter;
import com.tesu.manicurehouse.base.BaseApplication;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.GroupProductBean;
import com.tesu.manicurehouse.bean.ProductBean;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.UploadGoodsBean;
import com.tesu.manicurehouse.bean.UploadVideoMessageBean;
import com.tesu.manicurehouse.bean.VideoClassBean;
import com.tesu.manicurehouse.bean.VideoLableBean;
import com.tesu.manicurehouse.response.AddVideoLableResponse;
import com.tesu.manicurehouse.response.BaseResponse;
import com.tesu.manicurehouse.response.GetGoodsAttrResponse;
import com.tesu.manicurehouse.response.GetGroupProductResponse;
import com.tesu.manicurehouse.response.GetVideoClassResponse;
import com.tesu.manicurehouse.response.GetVideoLableResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MySQLiteHelper;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.HorizontalListView;
import com.tesu.manicurehouse.widget.MyListView;
import com.tesu.manicurehouse.widget.NoScrollGridView;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.UpCompleteListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoShowPhotoActivity extends RecordBaseActivity {
	private NoScrollGridView lable_gv;
	private VideoUploadLableAdapter mAdapter;
	private EditText title_et;
	private MyListView content_lv;
	private NoScrollGridView class_gv;
	private TextView charge_tv;
	private TextView free_tv;
	private TextView setting_et;
	private MyListView product_lv;
	private TextView submit_tv;
	private EditText lable_et;
	private TextView lable_submit_tv;
	private TextView lable_cancel_tv;
	private PercentLinearLayout edit_ll;

	private VideoUploadLableAdapter lableAdapter;
	private List<VideoLableBean> videoLableBeanList;
	private Dialog loadingDialog;
	private String url;
	private GetVideoLableResponse getLableResponse;
	private Gson gson;
	private GetVideoClassResponse getVideoClassResponse;
	private List<VideoClassBean> videoClassBeanList;
	private VideoUploadClassAdapter classAdapter;
	private String lableName;
	private AddVideoLableResponse addVideoLableResponse;
	private int lableId;
	private boolean isFree;
	private ShowPhotoContentAdapter showPhotoContentAdapter;
	private List<ShowPhotoContentBean> showPhotoContentBeanList;

	/**
	 * 照相选择界面
	 */
	private PopupWindow pWindow;
	private View root;
	private LayoutInflater mInflater;
	private String timepath;
	private static final int PHOTO_GRAPH = 1;// 拍照
	private static final int PHOTO_ZOOM = 2; // 缩放
	private static final int DESCRIPTION_RESOULT = 3;// 结果

	private static final String IMAGE_UNSPECIFIED = "image/*";
	private String path;
	private int position;

	private GroupProductAdapter groupProductAdapter;
	private List<GroupProductBean> groupProductBeanList;
	private GetGroupProductResponse getGroupProductResponse;

	private int classId;
	private String title;
	private List<Integer> lableList;
	private GetGoodsAttrResponse getGoodsAttrResponse;
	private List<GoodsAttrBean> goodsAttrBeanList;
	private PopupWindow goodsAttrWindow;
	private View goodsAttrRoot;
	private GoodsAttrAdapter goodsAttrAdapter;
	private GridView goodsAttrGv;
	private HorizontalListView goods_attr_lv;
	private List<GoodsAttrBean> selectedGoodsAttrBeans;
	private SelectedGoodsAttrAdapter selectedGoodsAttrAdapter;
	private TextView product_name_tv;
	private String goods_thumb;
	private String shop_price;
	private String productName;
	private String fee;
	private List<GoodsAttrBean> uploadGoodsBeans;
	private BaseResponse baseResponse;
	private int addLableCount;
	private TextView save_tv;
	private String userId;
	private SQLiteDatabase db ;
	private Dialog mDialog;

	private File mFile;
	private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
	private ImageView video_new_img_back;
	private int addOrUpdate;    //0 添加图片，  1 修改图片
	private int updateType;  //1 第一张图片，  2 第二张图片

	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_show_photo;
	}

	@Override
	protected void findViews() {
		lable_gv = (NoScrollGridView) findViewById(R.id.lable_gv);
		title_et = (EditText) findViewById(R.id.title_et);
		lable_et = (EditText) findViewById(R.id.lable_et);
		lable_submit_tv = (TextView) findViewById(R.id.lable_submit_tv);
		lable_cancel_tv = (TextView) findViewById(R.id.lable_cancel_tv);
		class_gv = (NoScrollGridView) findViewById(R.id.class_gv);
		charge_tv = (TextView) findViewById(R.id.charge_tv);
		free_tv = (TextView) findViewById(R.id.free_tv);
		setting_et = (TextView) findViewById(R.id.setting_et);
		product_lv = (MyListView) findViewById(R.id.product_lv);
		submit_tv = (TextView) findViewById(R.id.submit_tv);
		edit_ll = (PercentLinearLayout) findViewById(R.id.edit_ll);
		content_lv = (MyListView) findViewById(R.id.content_lv);
		save_tv = (TextView) findViewById(R.id.save_tv);
		video_new_img_back = (ImageView) findViewById(R.id.video_new_img_back);
	}
	
	@Override
	protected void initGetData() {
		super.initGetData();

		loadingDialog = DialogUtils.createLoadDialog(VideoShowPhotoActivity.this, false);
		gson = new Gson();

//		charge_tv.setBackgroundResource(R.drawable.bt_bg_editor);
//		free_tv.setBackgroundResource(R.drawable.bg_label);
		isFree = true;

		if (mInflater == null) {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		root = mInflater.inflate(R.layout.alert_dialog, null);
		pWindow = new PopupWindow(root, ActionBar.LayoutParams.FILL_PARENT,
				ActionBar.LayoutParams.FILL_PARENT);
		root.findViewById(R.id.btn_Phone).setOnClickListener(itemsOnClick);
		root.findViewById(R.id.btn_TakePicture)
				.setOnClickListener(itemsOnClick);
		root.findViewById(R.id.bg_photo).getBackground().setAlpha(100);

		root.findViewById(R.id.btn_cancel).setOnClickListener(itemsOnClick);



		lableList = new ArrayList<Integer>();

		goodsAttrRoot = mInflater.inflate(R.layout.goods_attr_dialog, null);
		goodsAttrWindow = new PopupWindow(goodsAttrRoot, ActionBar.LayoutParams.FILL_PARENT,
				ActionBar.LayoutParams.FILL_PARENT);
		goodsAttrRoot.findViewById(R.id.back_iv).setOnClickListener(goodsAttrClickListener);
		goodsAttrRoot.findViewById(R.id.next_tv)
				.setOnClickListener(goodsAttrClickListener);
		goodsAttrGv = (GridView)goodsAttrRoot.findViewById(R.id.goods_attr_gv);
		goods_attr_lv = (HorizontalListView) goodsAttrRoot.findViewById(R.id.goods_attr_lv);
		product_name_tv = (TextView) goodsAttrRoot.findViewById(R.id.product_name_tv);

		selectedGoodsAttrBeans = new ArrayList<GoodsAttrBean>();
		selectedGoodsAttrAdapter = new SelectedGoodsAttrAdapter(this,selectedGoodsAttrBeans);
		goods_attr_lv.setAdapter(selectedGoodsAttrAdapter);

		uploadGoodsBeans = new ArrayList<GoodsAttrBean>();

		addLableCount = 0;

		userId = SharedPrefrenceUtils.getString(this, "user_id");

		db = BaseApplication.getDbHelper().getWritableDatabase();

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		if(!TextUtils.isEmpty(title)){
			title_et.setText(title);
		}

		int is_fee = intent.getIntExtra("is_fee",0);
		if(is_fee==1){
			charge_tv.setBackgroundResource(R.drawable.bt_bg_editor);
			free_tv.setBackgroundResource(R.drawable.bg_label);
			isFree = false;
			product_lv.setVisibility(View.VISIBLE);
			setting_et.setVisibility(View.VISIBLE);
			charge_tv.setTextColor(getResources().getColor(R.color.white));
			free_tv.setTextColor(getResources().getColor(R.color.text_color_black));
		}

		showPhotoContentBeanList = new ArrayList<ShowPhotoContentBean>();


		String pics = intent.getStringExtra("pics");
		if(!TextUtils.isEmpty(pics)){
			try {
				showPhotoContentBeanList = gson.fromJson(pics,new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				ShowPhotoContentBean showPhotoContentBean = new ShowPhotoContentBean();
				showPhotoContentBeanList.add(showPhotoContentBean);
			}
		}else {
			ShowPhotoContentBean showPhotoContentBean = new ShowPhotoContentBean();
			showPhotoContentBeanList.add(showPhotoContentBean);
		}
		showPhotoContentAdapter = new ShowPhotoContentAdapter(this,showPhotoContentBeanList);
		content_lv.setAdapter(showPhotoContentAdapter);
	}

	@Override
	protected void init() {
		getLableList();

		getClassList();

		getGroupProductList();

	}

	private void getGroupProductList() {
//		groupProductBeanList = new ArrayList<GroupProductBean>();
//		for(int i=0;i<2;i++){
//			GroupProductBean groupProductBean = new GroupProductBean();
//			List<ProductBean> productBeans = new ArrayList<ProductBean>();
//			for(int j =0;j<3;j++){
//				ProductBean productBean = new ProductBean();
//				productBean.setGoods_id(j);
//				productBean.setGoods_name("商品名" + j);
//				productBean.setGoods_thumb("http://ceshi0823.b0.upaiyun.com/uploads/20160912/pacq6oh0k1ti61obpk2f0yy9zqjrs65a.png");
//				productBeans.add(productBean);
//			}
//			groupProductBean.setGoods_type_id(i);
//			groupProductBean.setGoods_type_name("分组名" + i);
//			groupProductBean.setGoodsList(productBeans);
//			groupProductBeanList.add(groupProductBean);
//		}
//		LogUtils.e("分组商品列表:" + groupProductBeanList.toString());
//		groupProductAdapter = new GroupProductAdapter(this,groupProductBeanList);
//		product_lv.setAdapter(groupProductAdapter);

		loadingDialog.show();
		url = "shop/getGroupGoodList.do";
		Map<String,String> params = new HashMap<String,String>();
		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("获取分组商品列表:" + json);
				loadingDialog.dismiss();
				getGroupProductResponse = gson.fromJson(json,GetGroupProductResponse.class);
				if(getGroupProductResponse.getCode()==0){
					groupProductBeanList = getGroupProductResponse.getDataList();
					groupProductAdapter = new GroupProductAdapter(VideoShowPhotoActivity.this,groupProductBeanList);
		            product_lv.setAdapter(groupProductAdapter);

					groupProductAdapter.setiProductCallBack(new GroupProductAdapter.IProductCallBack() {
						@Override
						public void chooseProduct(int pos, int position) {
							ProductBean productBean = groupProductBeanList.get(pos).getGoodsList().get(position);
							if (productBean != null) {
								productName = productBean.getGoods_name();
								goods_thumb= productBean.getGoods_thumb();
								shop_price=productBean.getShop_price();
								getProductAttrList(productBean.getGoods_id(), pos, position);
							}
						}

						@Override
						public void unChooseProduct(int pos, int position) {
							ProductBean productBean = groupProductBeanList.get(pos).getGoodsList().get(position);
							if (productBean != null) {
								int productId = productBean.getGoods_id();
								for (int i = 0; i < uploadGoodsBeans.size(); i++) {
									GoodsAttrBean goodsAttrBean = uploadGoodsBeans.get(i);
									if (goodsAttrBean.getGoods_id() == productId) {
										uploadGoodsBeans.remove(i);
									}
								}
							}
						}
					});

				}else {
					DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, getGroupProductResponse.getResultText());
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("获取分组商品列表错误:" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, error);

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 拍照
		if (requestCode == PHOTO_GRAPH) {
			// 设置文件保存路径
			File dir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			path = dir + "/" + timepath + ".jpg";


			Bitmap photo = BitmapUtils.getSmallBitmap(path);

			Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			new_photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);

			String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			try {
				String suoPath = BitmapUtils.saveMyBitmap(suoName,new_photo);
				MyUpyunUtils.upload(suoPath,completeListener);
				loadingDialog.show();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// 读取相册缩放图片
		if (requestCode == PHOTO_ZOOM && data != null) {
			if(data.getData() != null){
				// 图片信息需包含在返回数据中
				String[] proj = { MediaStore.Images.Media.DATA };
				// 获取包含所需数据的Cursor对象
				Uri uri = data.getData();
				@SuppressWarnings("deprecation")
				Cursor cursor = null;
				cursor = managedQuery(uri, proj, null, null, null);
				if (cursor == null) {
					uri = BitmapUtils.getPictureUri(data, VideoShowPhotoActivity.this);
					cursor = managedQuery(uri, proj, null, null, null);
				}
				// 获取索引
				int photocolumn = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标一直开头
				cursor.moveToFirst();
				// 根据索引值获取图片路径
				path = cursor.getString(photocolumn);

				Bitmap photo = BitmapUtils.getSmallBitmap(path);
				Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				new_photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);

				String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				try {
					String suoPath = BitmapUtils.saveMyBitmap(suoName,new_photo);
					MyUpyunUtils.upload(suoPath,completeListener);
					loadingDialog.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**获得分类列表**/
	private void getClassList() {
		loadingDialog.show();
		url = "video/getVideoCategoryList.do";
		Map<String,String> params = new HashMap<String,String>();
		params.put("pageNo", "1");
		params.put("pageSize", "100");
		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("获取分类列表:" + json);
				loadingDialog.dismiss();
				getVideoClassResponse = gson.fromJson(json, GetVideoClassResponse.class);
				if (getVideoClassResponse != null) {
					if (getVideoClassResponse.getCode() == 0) {
						videoClassBeanList = getVideoClassResponse.getDataList();
						if (videoClassBeanList != null) {
							classAdapter = new VideoUploadClassAdapter(VideoShowPhotoActivity.this, videoClassBeanList);
							class_gv.setAdapter(classAdapter);
						}

					} else {
						DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, getVideoClassResponse.getResultText());
					}
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("获取分类列表错误:" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, error);
			}
		});
	}

	/**获得标签列表**/
	private void getLableList() {
		loadingDialog.show();
		url = "video/getTags.do";
		Map<String,String> params = new HashMap<String,String>();

		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("获取标签列表:" + json);
				loadingDialog.dismiss();
				getLableResponse = gson.fromJson(json, GetVideoLableResponse.class);
				if (getLableResponse != null) {
					if (getLableResponse.getCode() == 0) {
						videoLableBeanList = getLableResponse.getDataList();
						if (videoLableBeanList != null) {
							if (videoLableBeanList.size() > 7) {
								for (int i = videoLableBeanList.size() - 1; i >= 7; i--) {
									videoLableBeanList.remove(i);
								}
							}
							VideoLableBean videoLableBean = new VideoLableBean();
							videoLableBeanList.add(videoLableBean);
							lableAdapter = new VideoUploadLableAdapter(VideoShowPhotoActivity.this, videoLableBeanList);
							lable_gv.setAdapter(lableAdapter);
						}

					} else {
						DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, getLableResponse.getResultText());
					}
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("获取标签列表错误:" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, error);
			}
		});

	}

	@Override
	protected void widgetListener() {

		lable_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == videoLableBeanList.size() - 1) {
					edit_ll.setVisibility(View.VISIBLE);
				} else {
					VideoLableBean videoLableBean = videoLableBeanList.get(position);
					if (videoLableBean.ischecked()) {
						videoLableBean.setIschecked(false);
					} else {
						videoLableBean.setIschecked(true);
					}
					lableAdapter.notifyDataSetChanged();
				}

			}
		});
		class_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VideoClassBean videoClassBean = videoClassBeanList.get(position);
				if (videoClassBean.isChecked()) {
					videoClassBean.setIsChecked(false);
					classId = 0;
				} else {
					for (VideoClassBean videoClassBean1 : videoClassBeanList) {
						videoClassBean1.setIsChecked(false);
					}
					videoClassBean.setIsChecked(true);
					classId = videoClassBean.getVideo_category_id();
				}
				classAdapter.notifyDataSetChanged();

			}
		});
		lable_submit_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lableName = lable_et.getText().toString();
				if (StringUtils.isEmpty(lableName)) {
					Toast.makeText(VideoShowPhotoActivity.this, "请输入标签名称", Toast.LENGTH_LONG).show();
					return;
				}
				if (addLableCount >= 3) {
					Toast.makeText(VideoShowPhotoActivity.this, "最多只能增加3个标签", Toast.LENGTH_LONG).show();
					return;
				}
				boolean isHave = false;
				for (int i = 0; i < videoLableBeanList.size(); i++) {
					VideoLableBean videoLableBean = videoLableBeanList.get(i);
					if ((i != videoLableBeanList.size() - 1) && videoLableBean.getTag_name().equals(lableName)) {
						isHave = true;
						break;
					}
				}
				if (isHave) {
					Toast.makeText(VideoShowPhotoActivity.this, "请不要添加重复标签", Toast.LENGTH_LONG).show();
					return;
				}
				addVideoLable();

			}
		});
		lable_cancel_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edit_ll.setVisibility(View.GONE);

			}
		});
		charge_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				charge_tv.setBackgroundResource(R.drawable.bt_bg_editor);
				free_tv.setBackgroundResource(R.drawable.bg_label);
				isFree = false;
				product_lv.setVisibility(View.VISIBLE);
				setting_et.setVisibility(View.VISIBLE);
				charge_tv.setTextColor(getResources().getColor(R.color.white));
				free_tv.setTextColor(getResources().getColor(R.color.text_color_black));

			}
		});
		free_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				charge_tv.setBackgroundResource(R.drawable.bg_label);
				free_tv.setBackgroundResource(R.drawable.bt_bg_editor);
				isFree = true;
				product_lv.setVisibility(View.GONE);
				setting_et.setVisibility(View.GONE);
				charge_tv.setTextColor(getResources().getColor(R.color.text_color_black));
				free_tv.setTextColor(getResources().getColor(R.color.white));
			}
		});
		showPhotoContentAdapter.setCallBack(new ShowPhotoContentAdapter.ICallBack() {
			@Override
			public void addImage(int pos) {
				addOrUpdate=0;
				position = pos;
				pWindow.showAtLocation((PercentRelativeLayout) findViewById(R.id.relative),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

			}

			@Override
			public void addTotal(int pos) {
				if (pos == 0) {
					if (showPhotoContentBeanList.get(pos).getPicCnt() != 1) {
						DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "请先上传最终效果图");
						return;
					}
				} else {
					if (showPhotoContentBeanList.get(pos).getPicCnt() == 0) {
						DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "每个步骤必须有图片");
						return;
					}
				}
				ShowPhotoContentBean showPhotoContentBean = new ShowPhotoContentBean();
				showPhotoContentBeanList.add(showPhotoContentBean);
				showPhotoContentAdapter.notifyDataSetChanged();
			}

			@Override
			public void updateImage(int type, int pos) {
				addOrUpdate=1;
				updateType= type;
				position = pos;
				pWindow.showAtLocation((PercentRelativeLayout) findViewById(R.id.relative),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		submit_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userId = SharedPrefrenceUtils.getString(VideoShowPhotoActivity.this, "user_id");
				title = title_et.getText().toString();
				if (StringUtils.isEmpty(title)) {
					Toast.makeText(VideoShowPhotoActivity.this, "请输入标题", Toast.LENGTH_LONG).show();
					return;
				}

				if(classId ==0){
					Toast.makeText(VideoShowPhotoActivity.this, "请选择分类", Toast.LENGTH_LONG).show();
					return;
				}

				lableList.clear();
				for (VideoLableBean videoLableBean : videoLableBeanList) {
					if (videoLableBean.ischecked()) {
						lableList.add(videoLableBean.getTag_id());
					}
				}

				if (lableList.size() == 0) {
					Toast.makeText(VideoShowPhotoActivity.this, "请选择标签", Toast.LENGTH_LONG).show();
					return;
				}
				fee = "9.99";
//				if (!isFree) {
//					if (StringUtils.isEmpty(fee)) {
//						Toast.makeText(VideoShowPhotoActivity.this, "付费类型，设计费不能为空", Toast.LENGTH_LONG).show();
//						return;
//					}
//				}
				if (showPhotoContentBeanList != null && showPhotoContentBeanList.size() > 0) {
					if (showPhotoContentBeanList.size() == 1) {
						if (showPhotoContentBeanList.get(0).getPicCnt() == 0) {
							Toast.makeText(VideoShowPhotoActivity.this, "最终效果图不能为空", Toast.LENGTH_LONG).show();
							return;
						}
					} else {
						if (showPhotoContentBeanList.get(showPhotoContentBeanList.size() - 1).getPicCnt() == 0) {
							Toast.makeText(VideoShowPhotoActivity.this, "每个步骤必须有图片", Toast.LENGTH_LONG).show();
							return;
						}

					}
				}
				if (StringUtils.isEmpty(userId)) {
					mDialog = DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "请先登陆", new OnClickListener() {
						@Override
						public void onClick(View v) {
							switch (v.getId()) {
								case R.id.tv_roger:
									mDialog.dismiss();
									Intent intent = new Intent(VideoShowPhotoActivity.this, LoginActivity.class);
									startActivity(intent);
									break;
							}

						}
					});
					return;
				}

				if(!isFree){
					if(uploadGoodsBeans == null || uploadGoodsBeans.size()==0){
						Toast.makeText(VideoShowPhotoActivity.this, "付费教程必须有商品，请先选择商品", Toast.LENGTH_LONG).show();
						return;
					}
				}

				uploadAllMesage();
			}
		});

		save_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userId = SharedPrefrenceUtils.getString(VideoShowPhotoActivity.this, "user_id");
				if (StringUtils.isEmpty(userId)) {
					mDialog =DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "请先登陆", new OnClickListener() {
						@Override
						public void onClick(View v) {
							switch (v.getId()) {
								case R.id.tv_roger:
									mDialog.dismiss();
									Intent intent = new Intent(VideoShowPhotoActivity.this, LoginActivity.class);
									startActivity(intent);
									break;
							}

						}
					});
					return;
				}
				saveAllMessage();
			}
		});

		video_new_img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	/**
	 * 保存所有数据
	 */
	private void saveAllMessage() {

//		UploadVideoMessageBean uploadVideoMessageBean = new UploadVideoMessageBean();
//		uploadVideoMessageBean.user_id = userId;
//		uploadVideoMessageBean.pics = gson.toJson(showPhotoContentBeanList);
//		uploadVideoMessageBean.type = 2;
//		uploadVideoMessageBean.title = title;
//		uploadVideoMessageBean.create_time = System.currentTimeMillis()/1000;
//		LogUtils.e("存储前id:" + uploadVideoMessageBean.id);

		title = title_et.getText().toString();

		if(TextUtils.isEmpty(title)){
			Toast.makeText(this,"请输入标题",Toast.LENGTH_LONG).show();
			return;
		}

		if (showPhotoContentBeanList != null && showPhotoContentBeanList.size() > 0) {
			if (showPhotoContentBeanList.size() == 1) {
				if (showPhotoContentBeanList.get(0).getPicCnt() == 0) {
					Toast.makeText(VideoShowPhotoActivity.this, "最终效果图不能为空", Toast.LENGTH_LONG).show();
					return;
				}
			} else {
				if (showPhotoContentBeanList.get(showPhotoContentBeanList.size() - 1).getPicCnt() == 0) {
					Toast.makeText(VideoShowPhotoActivity.this, "每个步骤必须有图片", Toast.LENGTH_LONG).show();
					return;
				}

			}
		}
		if (StringUtils.isEmpty(userId)) {
			mDialog = DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "请先登陆", new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.tv_roger:
							mDialog.dismiss();
							Intent intent = new Intent(VideoShowPhotoActivity.this, LoginActivity.class);
							startActivity(intent);
							break;
					}

				}
			});
			return;
		}

		ContentValues values = new ContentValues();
		values.put("user_id", userId);
		values.put("pics", gson.toJson(showPhotoContentBeanList));
		values.put("type", 2);
		values.put("title", title);
		values.put("create_time", System.currentTimeMillis() / 1000);
		if(isFree){
			values.put("is_fee",0);
		}else{
			values.put("is_fee",1);
		}
		long id = db.insert("videomessage", null, values);

		LogUtils.e("保存临时视频ID:"+id);
		if(id ==0){
			DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "保存数据失败");
		}else {
			finish();
		}
	}


	/**
	 * 获取商品色卡
	 */
	private void getProductAttrList(final int productId, final int pos, final int attrPos) {
		loadingDialog.show();
		String url="shop/getGoodAttrList.do";
		Map<String,String> params = new HashMap<String,String>();
		params.put("goods_id", productId + "");
		params.put("pageNo", "1");
		params.put("pageSize", "999");
		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("获取商品色卡列表:" + json);
				loadingDialog.dismiss();
				getGoodsAttrResponse = gson.fromJson(json,GetGoodsAttrResponse.class);
				if(getGoodsAttrResponse != null){
					goodsAttrBeanList = getGoodsAttrResponse.getDataList();
					if(goodsAttrBeanList != null && goodsAttrBeanList.size()>0){
						goodsAttrAdapter = new GoodsAttrAdapter(VideoShowPhotoActivity.this,goodsAttrBeanList);
						goodsAttrGv.setAdapter(goodsAttrAdapter);
						goodsAttrGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								GoodsAttrBean goodsAttrBean = goodsAttrBeanList.get(position);
								if (goodsAttrBean.isSelected()) {
									goodsAttrBean.setIsSelected(false);
									if (selectedGoodsAttrBeans.contains(goodsAttrBean)) {
										selectedGoodsAttrBeans.remove(goodsAttrBean);
									}
								} else {
									goodsAttrBean.setIsSelected(true);
									goodsAttrBean.setNum(1);
									goodsAttrBean.setGoods_id(productId);
									selectedGoodsAttrBeans.add(goodsAttrBean);
								}
								selectedGoodsAttrAdapter.notifyDataSetChanged();
								goodsAttrAdapter.notifyDataSetChanged();
							}
						});
						if(!StringUtils.isEmpty(productName)){
							product_name_tv.setText(productName);
						}
						selectedGoodsAttrBeans.clear();
						selectedGoodsAttrAdapter.notifyDataSetChanged();
						goodsAttrWindow.showAtLocation((PercentRelativeLayout) findViewById(R.id.relative),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
					}else {
//						DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "该商品没有色卡");
						GoodsAttrBean goodsAttrBean=new GoodsAttrBean();
						goodsAttrBean.setNum(1);
						goodsAttrBean.setGoods_id(productId);
						goodsAttrBean.setGoods_name(productName);
						goodsAttrBean.setGoods_thumb(goods_thumb);
						goodsAttrBean.setShop_price(Float.parseFloat(shop_price));
						uploadGoodsBeans.add(goodsAttrBean);
//						ProductBean productBean = groupProductBeanList.get(pos).getGoodsList().get(attrPos);
//						if(productBean != null){
//							productBean.setIsSelected(false);
//							groupProductAdapter.notifyDataSetChanged();
//						}
					}
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("获取商品色卡列表错误:" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, error);
			}
		});
	}

	/**
	 * 提交所有数据
	 */
	private void uploadAllMesage() {
		loadingDialog.show();
		String url="video/uploadCCVideoFile.do";
		Map<String,String> params = new HashMap<String,String>();
		if(!StringUtils.isEmpty(userId)){
			params.put("user_id",userId);
		}else {
			params.put("user_id","1");
		}
		params.put("video_category_id",classId+"");
		params.put("type","2");
		params.put("pics",gson.toJson(showPhotoContentBeanList));
		params.put("title",title);
		params.put("tag_ids",gson.toJson(lableList));
		if(isFree){
			params.put("is_fee","0");
		}else {
			params.put("is_fee","1");
			params.put("fee",fee);
		}
		params.put("goodList",gson.toJson(uploadGoodsBeans));
		LogUtils.e("uploadGoodsBeans:"+uploadGoodsBeans.toString());
		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("提交所有数据:"+json);
				loadingDialog.dismiss();
				baseResponse = gson.fromJson(json,BaseResponse.class);
				if(baseResponse.getCode()==0){
					DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "上传成功", new OnClickListener() {
						@Override
						public void onClick(View v) {
							switch (v.getId()){
								case R.id.tv_roger:
									SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
									finishActivity();
									VideoShowPhotoActivity.this.finish();
									break;
							}
						}
					});

				}else {
					DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, baseResponse.getResultText());
				}
			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("提交所有数据错误:"+error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, error);
			}
		});
	}

	/**增加视频标签**/
	private void addVideoLable() {
		loadingDialog.show();
		url = "video/addVideoTag.do";
		Map<String,String> params = new HashMap<String,String>();
		params.put("tag_name", lableName);
		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("增加视频标签:" + json);
				loadingDialog.dismiss();
				addVideoLableResponse = gson.fromJson(json, AddVideoLableResponse.class);
				if (addVideoLableResponse.getCode() == 0) {
					lableId = addVideoLableResponse.getTag_id();
					videoLableBeanList.remove(videoLableBeanList.size() - 1);
					VideoLableBean videoLableBean = new VideoLableBean();
					videoLableBean.setIschecked(true);
					videoLableBean.setTag_name(lableName);
					videoLableBean.setTag_id(lableId);
					videoLableBeanList.add(videoLableBean);
					videoLableBeanList.add(new VideoLableBean());
					lableAdapter.notifyDataSetChanged();
					addLableCount++;
					lable_et.setText("");

				} else {
					DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, addVideoLableResponse.getResultText());
				}
			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("增加视频标签错误:" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, error);
			}
		});

	}

	private View.OnClickListener itemsOnClick = new View.OnClickListener() {

		public void onClick(View v) {
			pWindow.dismiss();
			switch (v.getId()) {
				case R.id.btn_TakePicture: {
					timepath = new SimpleDateFormat("yyyyMMdd_HHmmss")
							.format(new Date());

					if (Environment.MEDIA_MOUNTED.equals(Environment
							.getExternalStorageState())) {
						File dir = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
						if (!dir.exists()) {
							dir.mkdir();
						}
						mFile = new File(dir, timepath + ".jpg");

						if (ContextCompat.checkSelfPermission(VideoShowPhotoActivity.this,
								Manifest.permission.CAMERA)
								!= PackageManager.PERMISSION_GRANTED)
						{

							ActivityCompat.requestPermissions(VideoShowPhotoActivity.this,
									new String[]{Manifest.permission.CAMERA},
									MY_PERMISSIONS_REQUEST_CAMERA);
						}else {
							startActivityForResult(new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
											MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile)),
									PHOTO_GRAPH);
						}
					}
					break;
				}
				case R.id.btn_Phone: {
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							IMAGE_UNSPECIFIED);
					startActivityForResult(intent, PHOTO_ZOOM);
					break;
				}
				case R.id.btn_cancel: {
					pWindow.dismiss();
					break;
				}
				default:
					break;
			}

		}

	};

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{

		if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA)
		{
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile)), PHOTO_GRAPH);
			} else
			{
				// Permission Denied
				Toast.makeText(VideoShowPhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
			}
			return;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private View.OnClickListener goodsAttrClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			goodsAttrWindow.dismiss();
			switch (v.getId()) {
				case R.id.next_tv:
					goodsAttrWindow.dismiss();
					uploadGoodsBeans.addAll(selectedGoodsAttrBeans);
					break;

				case R.id.back_iv:
					goodsAttrWindow.dismiss();
					break;

				default:
					break;
			}

		}

	};

	//结束回调，不可为空
	UpCompleteListener completeListener = new UpCompleteListener() {
		@Override
		public void onComplete(boolean isSuccess, String result) {
			Gson gson = new Gson();
			loadingDialog.dismiss();
			LogUtils.e("上传图片result:"+result);
			try {
				if(isSuccess){
                    HashMap<String,String> hs = gson.fromJson(result, HashMap.class);
                    String url = hs.get("url");
                    url =MyUpyunUtils.UpyunBaseUrl+url;
                    LogUtils.e("上传图片到又拍:"+url);

					if(addOrUpdate==0){
						ShowPhotoContentBean showPhotoContentBean = showPhotoContentBeanList.get(position);
						if(showPhotoContentBean.getPicCnt() ==0){
							showPhotoContentBean.setPic1(url);
							showPhotoContentBean.setPicCnt(1);
						}else if(showPhotoContentBean.getPicCnt() ==1){
							showPhotoContentBean.setPic2(url);
							showPhotoContentBean.setPicCnt(2);
						}
					}else {
						ShowPhotoContentBean showPhotoContentBean = showPhotoContentBeanList.get(position);
						if(updateType==1){
							showPhotoContentBean.setPic1(url);
						}else {
							showPhotoContentBean.setPic2(url);
						}
					}
                    showPhotoContentAdapter.notifyDataSetChanged();
                    }else {
                    LogUtils.e("上传图片到又拍错误:"+result);
                    DialogUtils.showAlertDialog(VideoShowPhotoActivity.this, "上传图片到又拍云错误");
                   }
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}

	};

}
