package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class AudioBean {
    private int audio_id;
    private String audio_name;
    private String file_url;
    private boolean isChecked;

    public int getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(int audio_id) {
        this.audio_id = audio_id;
    }

    public String getAudio_name() {
        return audio_name;
    }

    public void setAudio_name(String audio_name) {
        this.audio_name = audio_name;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "AudioBean{" +
                "audio_id=" + audio_id +
                ", audio_name='" + audio_name + '\'' +
                ", file_url='" + file_url + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
