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
    private Long priorityId;
    private String priorityName;
    private Long impactId;
    private String impactName;
    private Long slaRuleId;
    private String slaRuleName;
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

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public Long getImpactId() {
        return impactId;
    }

    public void setImpactId(Long impactId) {
        this.impactId = impactId;
    }

    public String getImpactName() {
        return impactName;
    }

    public void setImpactName(String impactName) {
        this.impactName = impactName;
    }

    public Long getSlaRuleId() {
        return slaRuleId;
    }

    public void setSlaRuleId(Long slaRuleId) {
        this.slaRuleId = slaRuleId;
    }

    public String getSlaRuleName() {
        return slaRuleName;
    }

    public void setSlaRuleName(String slaRuleName) {
        this.slaRuleName = slaRuleName;
    }

    public List<TicketCommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<TicketCommentResponse> comments) {
        this.comments = comments;
    }
}
