package org.example.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTicketCommentRequest {

    @NotBlank
    private String commentText;

    private String createdBy;

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
