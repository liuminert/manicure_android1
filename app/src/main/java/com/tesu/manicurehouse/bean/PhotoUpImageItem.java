package com.tesu.manicurehouse.bean;

import java.io.Serializable;

public class PhotoUpImageItem implements Serializable {

	//图片ID
	private String imageId;
	//原图路径
	private String imagePath;
	//是否被选择
	private boolean isSelected = false;

	private String content;

	@Override
	public String toString() {
		return "PhotoUpImageItem{" +
				"imageId='" + imageId + '\'' +
				", imagePath='" + imagePath + '\'' +
				", isSelected=" + isSelected +
				", content='" + content + '\'' +
				'}';
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
