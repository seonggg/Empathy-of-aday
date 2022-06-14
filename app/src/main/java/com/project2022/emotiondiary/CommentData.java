package com.project2022.emotiondiary;


public class CommentData {
    String nickname;
    String time;
    String content;
    boolean del_vision;

    CommentData(String nickname, String time, String content){
        this.nickname = nickname;
        this.time = time;
        this.content = content;
    }

    CommentData(String nickname, String time, String content, boolean del_vision){
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.del_vision = del_vision;
    }
}
