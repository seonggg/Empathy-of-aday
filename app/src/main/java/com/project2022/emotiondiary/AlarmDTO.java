package com.project2022.emotiondiary;


import java.util.Date;

public class AlarmDTO {
    String destinationUid = null; //알림 받을 사용자 uid
    String nickname = null; //알림 보낸 사용자 uid
    String docid = null; //일기 정보 저장

    String message = null; //알림에 띄울 문구
    Date timestamp = null; //시간

    public AlarmDTO() {
    }

    public String getDestinationUid() {
        return destinationUid;
    }

    public void setDestinationUid(String destinationUid) {
        this.destinationUid = destinationUid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
