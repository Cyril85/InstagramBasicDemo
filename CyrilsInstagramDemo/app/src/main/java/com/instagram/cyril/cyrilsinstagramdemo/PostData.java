package com.instagram.cyril.cyrilsinstagramdemo;

import java.util.ArrayList;

/**
 * Created by cyril on 06/10/2017.
 */

public class PostData {
    public String postID;
    public String userID;
    public String userFullName;
    public String userProfilePicURL;
    public String userName;
    public String createdTime;
    public String captionText;
    public Boolean userLikedStatus = false;
    public String standardResolutionPostURL;
    public String thumbnailURL;
    public ArrayList<String> tagList = new ArrayList<>();
    public int likesCount;
    public int commentsCount;
    public String location;
    public ArrayList<String> carousel_mediaUrlList = new ArrayList<>();
    public String posts;
    public String followers;
    public String following;
    public Boolean isProfileTab = false;
    public int imageWidth;
    public int imageheight;
    public String postType;
    public String videoURL;


    public PostData(String userFullName, String posts, String followers, String following) {
        this.userFullName = userFullName;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
        this.isProfileTab = true;
    }

    public PostData(String postID, String userID, String userFullName, String userProfilePicURL, String userName, String createdTime, String captionText, Boolean userLikedStatus, String standardResolutionPostURL, int likesCount, int commentsCount, String location, String postType,String thumbnailURL) {
        this.postID = postID;
        this.userID = userID;
        this.userFullName = userFullName;
        this.thumbnailURL=thumbnailURL;
        this.userProfilePicURL = userProfilePicURL;
        this.userName = userName;
        this.createdTime = createdTime;
        this.captionText = captionText;
        this.userLikedStatus = userLikedStatus;
        this.standardResolutionPostURL = standardResolutionPostURL;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.location = location;
        this.postType = postType;

    }

    public void setVideoURL(String url) {
        this.videoURL = url;
    }

    public String getVideoURL(String url) {
       return videoURL;
    }

    public void setTags(ArrayList<String> tags) {
        for (int i = 0; i < tags.size(); i++) {
            tagList.add(tags.get(i));
        }
    }

    public void setCarousel_mediaUrlList(ArrayList<String> medialist) {
        for (int i = 0; i < medialist.size(); i++) {
            carousel_mediaUrlList.add(medialist.get(i));
        }
    }

    public void setImageDetails(int width, int height) {
        this.imageWidth = width;
        this.imageheight = height;
    }

}
