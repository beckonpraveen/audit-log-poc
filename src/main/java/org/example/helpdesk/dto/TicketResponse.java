package org.example.helpdesk.dto;

import java.util.List;
import org.example.helpdesk.entity.TicketStatus;

public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private Long userId;
    private String userName;
    private List<TicketCommentResponse> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<TicketCommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<TicketCommentResponse> comments) {
        this.comments = comments;
    }
}
