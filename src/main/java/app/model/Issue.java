package app.model;

import java.sql.Timestamp;

public class Issue {
    private int id;
    private String category;
    private String description;
    private String location;
    private String status;
    private String imagePath;
    private String proofImagePath;
    private String proofStartPath;
    private String proofEndPath;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor for creating new issue
    public Issue(String category, String description, String location, String imagePath) {
        this.category = category;
        this.description = description;
        this.location = location;
        this.status = "Pending";
        this.imagePath = imagePath;
        this.proofImagePath = null;
        this.proofStartPath = null;
        this.proofEndPath = null;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for loading from database
    public Issue(int id, String category, String description, String location, String status,
                 String imagePath, String proofImagePath, String proofStartPath, String proofEndPath,
                 Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.location = location;
        this.status = status;
        this.imagePath = imagePath;
        this.proofImagePath = proofImagePath;
        this.proofStartPath = proofStartPath;
        this.proofEndPath = proofEndPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getProofImagePath() {
        return proofImagePath;
    }

    public void setProofImagePath(String proofImagePath) {
        this.proofImagePath = proofImagePath;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getProofStartPath() {
        return proofStartPath;
    }

    public void setProofStartPath(String proofStartPath) {
        this.proofStartPath = proofStartPath;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getProofEndPath() {
        return proofEndPath;
    }

    public void setProofEndPath(String proofEndPath) {
        this.proofEndPath = proofEndPath;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Check if issue is high priority (pending > 3 days)
    public boolean isHighPriority() {
        if (!"Pending".equals(status)) return false;
        long diff = System.currentTimeMillis() - createdAt.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        return days > 3;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", proofImagePath='" + proofImagePath + '\'' +
                ", proofStartPath='" + proofStartPath + '\'' +
                ", proofEndPath='" + proofEndPath + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
