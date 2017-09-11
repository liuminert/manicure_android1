package com.tesu.manicurehouse.bean;


public class ShareDateBean {

	//视频地址
	private String VideoUrl;
	//标题
	private String title;
	//分享地址
	private String url;
	//内容
	private String content;
	//图片网络地址
	private String imageurl;
	//图片地址
	private String imagepath;

	public String getVideoUrl() {
		return VideoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		VideoUrl = videoUrl;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
}
