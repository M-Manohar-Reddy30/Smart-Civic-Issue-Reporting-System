package com.civic.repository;

import com.civic.model.Issue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class IssueRepository {

    private final JdbcTemplate jdbcTemplate;

    public IssueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Issue> issueRowMapper = (rs, rowNum) -> {
        Issue issue = new Issue();
        issue.setId(rs.getLong("id"));
        issue.setUserId(rs.getObject("user_id", Long.class));
        issue.setCategory(rs.getString("category"));
        issue.setDescription(rs.getString("description"));
        issue.setLocation(rs.getString("location"));
        issue.setStatus(rs.getString("status"));
        issue.setImagePath(rs.getString("image_path"));
        issue.setProofStartPath(rs.getString("proof_start_path"));
        issue.setProofEndPath(rs.getString("proof_end_path"));
        issue.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        issue.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return issue;
    };

    public List<Issue> findAll() {
        String sql = "SELECT * FROM issues ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, issueRowMapper);
    }

    public Issue findById(Long id) {
        String sql = "SELECT * FROM issues WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, issueRowMapper, id);
    }

    public int save(Issue issue) {
        String sql = "INSERT INTO issues (user_id, category, description, location, status, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
            issue.getUserId(),
            issue.getCategory(), 
            issue.getDescription(), 
            issue.getLocation(), 
            issue.getStatus() == null ? "Pending" : issue.getStatus(),
            issue.getImagePath()
        );
    }

    public int updateStatus(Long id, String status, String proofPath, boolean isStartProof) {
        String proofColumn = isStartProof ? "proof_start_path" : "proof_end_path";
        String sql = "UPDATE issues SET status = ?, " + proofColumn + " = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, proofPath, id);
    }
    
    public List<Issue> findByLocation(String location) {
        String sql = "SELECT * FROM issues WHERE location LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, issueRowMapper, "%" + location + "%");
    }

    public List<Issue> findByUserId(Long userId) {
        String sql = "SELECT * FROM issues WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, issueRowMapper, userId);
    }

    public long countByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM issues", Long.class);
        }
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM issues WHERE status = ?", Long.class, status);
    }
}
