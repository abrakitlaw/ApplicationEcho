package com.thesis.application.echo.models;

/**
 * Created by Abra Kitlaw on 12-Jul-18.
 */

public class Post {
    public String postId;
    public String userId;
    public String postTitle;
    public String description;
    public String postCategory;
    public String postSource;
    public String imageVideoUrl;
    public String postDate;
    public int totalLikes;
    public int totalDislikes;
    public int totalPoints;
    public int totalReports;

    public Post() {
    }

    public Post(String postId, String userId, String postTitle, String description,
                String postCategory, String postSource, String imageVideoUrl, String postDate,
                int totalLikes, int totalDislikes, int totalPoints, int totalReports) {
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.description = description;
        this.postCategory = postCategory;
        this.postSource = postSource;
        this.imageVideoUrl = imageVideoUrl;
        this.postDate = postDate;
        this.totalLikes = totalLikes;
        this.totalDislikes = totalDislikes;
        this.totalPoints = totalPoints;
        this.totalReports = totalReports;
    }

    public Post(String userId, String postTitle, String description, String postCategory,
                String postSource, String imageVideoUrl, String postDate, int totalPoints, int totalReports) {
        this.userId = userId;
        this.postTitle = postTitle;
        this.description = description;
        this.postCategory = postCategory;
        this.postSource = postSource;
        this.imageVideoUrl = imageVideoUrl;
        this.postDate = postDate;
        this.totalPoints = totalPoints;
        this.totalReports = totalReports;
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

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostSource() {
        return postSource;
    }

    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }

    public String getImageVideoUrl() {
        return imageVideoUrl;
    }

    public void setImageVideoUrl(String imageVideoUrl) {
        this.imageVideoUrl = imageVideoUrl;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalDislikes() {
        return totalDislikes;
    }

    public void setTotalDislikes(int totalDislikes) {
        this.totalDislikes = totalDislikes;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(int totalReports) {
        this.totalReports = totalReports;
    }
}
