package com.tesu.manicurehouse.record;

public class SubtitleBean {
	private int id;  //字幕编号
	private String time;  //时间段
	private String content;  //内容
	private long videoId;  //字幕对应的视频ID
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getVideoId() {
		return videoId;
	}
	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}
	@Override
	public String toString() {
		return "SubtitleBean [id=" + id + ", time=" + time + ", content="
				+ content + ", videoId=" + videoId + "]";
	}

}
