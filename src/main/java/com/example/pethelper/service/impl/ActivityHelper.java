package com.example.pethelper.service.impl;

import org.springframework.stereotype.Component;

@Component("activityHelper")
public class ActivityHelper {

    public String getActivityIcon(String activityType) {
        switch (activityType) {
            case "POST_CREATED": return "fa-plus";
            case "POST_DELETED": return "fa-trash";
            case "POST_LIKED": return "fa-heart";
            case "POST_UNLIKED": return "fa-heart-crack";
            case "COMMENT_CREATED": return "fa-comment";
            case "COMMENT_DELETED": return "fa-comment-slash";
            case "PET_CREATED": return "fa-paw";
            case "PET_UPDATED": return "fa-pen";
            case "PET_DELETED": return "fa-trash";
            default: return "fa-circle";
        }
    }

    public String getActivityTypeText(String activityType) {
        return activityType.replace('_', ' ').toLowerCase();
    }
}