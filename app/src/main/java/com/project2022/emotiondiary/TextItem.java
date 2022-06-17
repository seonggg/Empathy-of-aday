package com.project2022.emotiondiary;

public class TextItem {
    //서버 통신 때 사용할 클래스
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;
}
