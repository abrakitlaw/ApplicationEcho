package com.thesis.application.echo.models;

/**
 * Created by Abra Kitlaw on 15-Jul-18.
 */

public class Comment {
    private String commentId;
    private String postId;
    private String userId;
    private String username;
    private String date;
    private String time;
    private String comment;

    public Comment() {
    }

    public Comment(String commentId, String postId, String userId, String username, String date, String comment, String time) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.date = date;
        this.time = time;
        this.comment = comment;
    }

    public Comment(String userId, String username, String date, String time, String comment) {
        this.userId = userId;
        this.username = username;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
