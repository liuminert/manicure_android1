package com.tesu.manicurehouse.bean;


public class ShareInfo {

	/**分享图*/
	private int share_photo=0;
	private int share_photo_selected=0;
	/** 分享名 */
	private String share_name = null;

	public int getShare_photo() {
		return share_photo;
	}
	public void setShare_photo(int share_photo) {
		this.share_photo = share_photo;
	}
	public String getShare_name() {
		return share_name;
	}
	public void setShare_name(String share_name) {
		this.share_name = share_name;
	}
	public int getShare_photo_selected() {
		return share_photo_selected;
	}
	public void setShare_photo_selected(int share_photo_selected) {
		this.share_photo_selected = share_photo_selected;
	}
	
}
