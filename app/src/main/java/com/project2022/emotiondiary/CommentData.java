package com.project2022.emotiondiary;

import java.util.concurrent.atomic.AtomicBoolean;

public class CommentData {
    String nickname;
    String time;
    String content;
    AtomicBoolean del_vision;
    String commentId;
    String diaryId;
    String type;
    String uid;

    CommentData(String nickname, String time, String content, AtomicBoolean del_vision, String commentId, String diaryId, String type, String uid){
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.del_vision = del_vision;
        this.commentId = commentId;
        this.diaryId = diaryId;
        this.type = type;
        this.uid = uid;
    }
}
