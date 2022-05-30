package com.project2022.emotiondiary;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Diary {
    private String writer_id;
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

    public Diary(String writer_id,String content, Timestamp timestamp, String date, String weather, Integer pictures){
        this.writer_id=writer_id;
        this.content=content;
        this.pictures=pictures;
        this.timestamp=timestamp;
        this.date=date;
        this.weather=weather;
        this.share=false;
    }

    public Diary(String writer_id, String content, Integer pictures, Timestamp timestamp, String weather, ArrayList<String> emotion, Boolean share){
        this.writer_id=writer_id;
        this.content=content;
        this.pictures=pictures;
        this.timestamp=timestamp;
        this.weather=weather;
        this.emotion=emotion;
        this.share=share;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> diary = new HashMap<>();
        diary.put("writer_id", writer_id);
        diary.put("content", content);
        diary.put("pictures", pictures);
        diary.put("timestamp", timestamp);
        diary.put("weather", weather);
        diary.put("emotion", emotion);
        diary.put("share", share);

        return diary;
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
}
