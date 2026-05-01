package app.service;

import app.db.DBConnection;
import app.model.Issue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueService {
    public boolean addIssue(Issue issue) {
    String sql = "INSERT INTO issues (category, description, location, status, image_path) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, issue.getCategory());
        stmt.setString(2, issue.getDescription());
        stmt.setString(3, issue.getLocation());
        stmt.setString(4, issue.getStatus());
        stmt.setString(5, issue.getImagePath());

        return stmt.executeUpdate() == 1;

    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}

    public List<Issue> getAllIssues() {
        String sql = "SELECT * FROM issues ORDER BY created_at DESC";
        List<Issue> issues = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return issues;
    }

    public List<Issue> getIssuesByLocation(String location) {
        String sql = "SELECT * FROM issues WHERE location LIKE ? ORDER BY created_at DESC";
        List<Issue> issues = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + location + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add(mapIssue(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return issues;
    }

    public List<Issue> searchIssues(String locationFilter, String issueTypeFilter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM issues WHERE 1=1");
        if (locationFilter != null && !locationFilter.isBlank()) {
            sql.append(" AND location LIKE ?");
        }
        if (issueTypeFilter != null && !issueTypeFilter.isBlank() && !"All".equalsIgnoreCase(issueTypeFilter)) {
            sql.append(" AND category = ?");
        }
        sql.append(" ORDER BY created_at DESC");

        List<Issue> issues = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (locationFilter != null && !locationFilter.isBlank()) {
                stmt.setString(index++, "%" + locationFilter + "%");
            }
            if (issueTypeFilter != null && !issueTypeFilter.isBlank() && !"All".equalsIgnoreCase(issueTypeFilter)) {
                stmt.setString(index, issueTypeFilter);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return issues;
    }

    public List<Issue> filterIssues(String location, String status, String category) {
        StringBuilder sql = new StringBuilder("SELECT * FROM issues WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location LIKE ?");
            params.add("%" + location + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category LIKE ?");
            params.add("%" + category + "%");
        }

        sql.append(" ORDER BY created_at DESC");

        List<Issue> issues = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add(mapIssue(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return issues;
    }

    public boolean updateIssueStatus(int id, String newStatus, String proofImagePath) {
        // First, get current issue to validate transition
        Issue currentIssue = getIssueById(id);
        if (currentIssue == null) {
            return false;
        }

        String currentStatus = currentIssue.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            return false;
        }

        // Validate proof image requirement
        if ((newStatus.equals("In Progress") || newStatus.equals("Resolved")) && (proofImagePath == null || proofImagePath.trim().isEmpty())) {
            return false;
        }

        String sql = "UPDATE issues SET status = ?, updated_at = CURRENT_TIMESTAMP";
        if (newStatus.equals("In Progress")) {
            sql += ", proof_start_path = ?";
        } else if (newStatus.equals("Resolved")) {
            sql += ", proof_end_path = ?";
        }
        sql += " WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            if (newStatus.equals("In Progress")) {
                stmt.setString(2, proofImagePath);
                stmt.setInt(3, id);
            } else if (newStatus.equals("Resolved")) {
                stmt.setString(2, proofImagePath);
                stmt.setInt(3, id);
            } else {
                stmt.setInt(2, id);
            }
            return stmt.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case "Pending":
                return newStatus.equals("In Progress");
            case "In Progress":
                return newStatus.equals("Resolved");
            case "Resolved":
                return false; // Cannot change from Resolved
            default:
                return false;
        }
    }

    public Issue getIssueById(int id) {
        String sql = "SELECT * FROM issues WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapIssue(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean updateProofImage(int id, String proofImagePath) {
        String sql = "UPDATE issues SET proof_image_path = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, proofImagePath);
            stmt.setInt(2, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getDashboardStats(String location) {
        Map<String, Integer> stats = new HashMap<>();
        String sql;
        PreparedStatement stmt = null;

        if (location != null && !location.trim().isEmpty()) {
            sql = "SELECT status, COUNT(*) AS count FROM issues WHERE location LIKE ? GROUP BY status";
            try {
                stmt = DBConnection.getConnection().prepareStatement(sql);
                stmt.setString(1, "%" + location + "%");
            } catch (SQLException ex) {
                ex.printStackTrace();
                return stats;
            }
        } else {
            sql = "SELECT status, COUNT(*) AS count FROM issues GROUP BY status";
            try {
                stmt = DBConnection.getConnection().prepareStatement(sql);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return stats;
            }
        }

        try (ResultSet rs = stmt.executeQuery()) {
            int total = 0;
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                stats.put(status, count);
                total += count;
            }
            stats.put("Total", total);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return stats;
    }

    public int getHighPriorityCount(String location) {
        String sql;
        PreparedStatement stmt = null;

        if (location != null && !location.trim().isEmpty()) {
            sql = "SELECT COUNT(*) AS count FROM issues WHERE status = 'Pending' AND location LIKE ? AND created_at < NOW() - INTERVAL 3 DAY";
            try {
                stmt = DBConnection.getConnection().prepareStatement(sql);
                stmt.setString(1, "%" + location + "%");
            } catch (SQLException ex) {
                ex.printStackTrace();
                return 0;
            }
        } else {
            sql = "SELECT COUNT(*) AS count FROM issues WHERE status = 'Pending' AND created_at < NOW() - INTERVAL 3 DAY";
            try {
                stmt = DBConnection.getConnection().prepareStatement(sql);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return 0;
            }
        }

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return 0;
    }

    private List<Issue> queryIssues(String sql) {
        List<Issue> issues = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return issues;
    }

    private Issue mapIssue(ResultSet rs) throws SQLException {
        return new Issue(
            rs.getInt("id"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getString("location"),
            rs.getString("status"),
            rs.getString("image_path"),
            rs.getString("proof_image_path"),
            rs.getString("proof_start_path"),
            rs.getString("proof_end_path"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
