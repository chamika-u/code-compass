package com.hackathon.mcp.model;

import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Repository metadata model
 */
public class RepositoryMetadata {
    
    private String name;
    private String description;
    
    @JsonbProperty("full_name")
    private String fullName;
    
    @JsonbProperty("html_url")
    private String htmlUrl;
    
    private String language;
    
    @JsonbProperty("stargazers_count")
    private int stars;
    
    @JsonbProperty("forks_count")
    private int forks;
    
    @JsonbProperty("open_issues_count")
    private int openIssues;
    
    @JsonbProperty("default_branch")
    private String defaultBranch;
    
    private String[] topics;
    
    @JsonbProperty("created_at")
    private String createdAt;
    
    @JsonbProperty("updated_at")
    private String updatedAt;

    public RepositoryMetadata() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

// Made with Bob
