package com.project2022.emotiondiary;

public class PushDTO {
    String to = null; //푸시 보낼 곳
    Notification notification = new Notification(); //알림

    public String getTo() {
        return to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public void setTo(String to) {
        this.to = to;
    }

    class Notification{
        String title = null; //알림 제목
        String body = null; //알림 내용

        public String getBody() {
            return body;
        }

        public String getTitle() {
            return title;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
