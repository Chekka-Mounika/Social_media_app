package com.example.bloodhelp.DataModels;

public class AnswerMember {
    String name,uid,answer,url,time , Reply_key;

    public AnswerMember(){}
    public String getReply_key(){return Reply_key;}

    public void setReply_key(String question_key) {
        this.Reply_key = question_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
