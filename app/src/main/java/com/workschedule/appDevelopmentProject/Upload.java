package com.workschedule.appDevelopmentProject;

public class Upload {
    private String mImageUrl;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
