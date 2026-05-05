package com.civic.model;

import java.time.LocalDateTime;

public class Issue {
    private Long id;
    private Long userId;
    private String category;
    private String description;
    private String location;
    private String status;
    private String imagePath;
    private String proofStartPath;
    private String proofEndPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Issue() {}

    public Issue(Long id, String category, String description, String location, String status, String imagePath, String proofStartPath, String proofEndPath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.location = location;
        this.status = status;
        this.imagePath = imagePath;
        this.proofStartPath = proofStartPath;
        this.proofEndPath = proofEndPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getProofStartPath() { return proofStartPath; }
    public void setProofStartPath(String proofStartPath) { this.proofStartPath = proofStartPath; }

    public String getProofEndPath() { return proofEndPath; }
    public void setProofEndPath(String proofEndPath) { this.proofEndPath = proofEndPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
