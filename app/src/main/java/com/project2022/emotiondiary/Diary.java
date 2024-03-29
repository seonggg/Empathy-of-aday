package com.project2022.emotiondiary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Diary {
    private String writer_uid;
    private String writer_id;
    private String nickname;
    private String content;
    private Integer pictures;
    private Timestamp timestamp;
    private String date;
    private String weather;
    private ArrayList<String> emotion =new ArrayList<String>();
    private ArrayList<String> beads = new ArrayList<>();
    private Boolean share;

    public Diary(){
    }

    public Diary(String writer_uid, String writer_id, String nickname, String content, Timestamp timestamp, String date, String weather, Integer pictures){
        this.writer_uid=writer_uid;
        this.writer_id=writer_id;
        this.nickname=nickname;
        this.content=content;
        this.pictures=pictures;
        this.timestamp=timestamp;
        this.date=date;
        this.weather=weather;
        this.share=false;
    }

    public Diary(String writer_uid, String writer_id, String nickname, String content, Integer pictures, Timestamp timestamp, String weather, ArrayList<String> emotion, Boolean share){
        this.writer_uid=writer_uid;
        this.writer_id=writer_id;
        this.nickname=nickname;
        this.content=content;
        this.pictures=pictures;
        this.timestamp=timestamp;
        this.weather=weather;
        this.emotion=emotion;
        this.share=share;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> diary = new HashMap<>();
        diary.put("writer_uid", writer_uid);
        diary.put("writer_id", writer_id);
        diary.put("content", content);
        diary.put("pictures", pictures);
        diary.put("timestamp", timestamp);
        diary.put("weather", weather);
        diary.put("emotion", emotion);
        diary.put("share", share);

        return diary;
    }

    public String getWriter_uid() {
        return writer_uid;
    }

    public void setWriter_uid(String writer_uid) {
        this.writer_uid = writer_uid;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public String getContent() {
        return content;
    }

    public Integer getPictures() {
        return pictures;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getWeather() {
        return weather;
    }

    public ArrayList<String> getEmotion() {
        return emotion;
    }

    public Boolean getShare() {
        return share;
    }

    public ArrayList<String> getBeads() {
        return beads;
    }

    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPictures(Integer pictures) {
        this.pictures = pictures;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setEmotion(ArrayList<String> emotion) {
        this.emotion = emotion;
    }

    public void setShare(Boolean share) {
        this.share = share;
    }

    public void setBeads(ArrayList<String> beads) {
        this.beads = beads;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
