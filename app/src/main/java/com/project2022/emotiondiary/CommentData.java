package com.project2022.emotiondiary;


public class CommentData {
    String nickname;
    String time;
    String content;
    boolean del_vision;
    String commentId;
    String diaryId;

    CommentData(String nickname, String time, String content, boolean del_vision, String commentId, String diaryId){
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.del_vision = del_vision;
        this.commentId = commentId;
        this.diaryId = diaryId;
    }
}
