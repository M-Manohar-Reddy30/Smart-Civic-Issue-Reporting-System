package com.civic.controller;

import com.civic.model.Issue;
import com.civic.service.IssueService;
import com.civic.service.FileStorageService;
import com.civic.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    private final IssueService issueService;
    private final FileStorageService fileStorageService;
    private final JwtUtil jwtUtil;

    public IssueController(IssueService issueService, FileStorageService fileStorageService, JwtUtil jwtUtil) {
        this.issueService = issueService;
        this.fileStorageService = fileStorageService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Issue> getAllIssues() {
        return issueService.getAllIssues();
    }

    @GetMapping("/stats")
    public ResponseEntity<com.civic.model.IssueStats> getStats() {
        return ResponseEntity.ok(issueService.getGlobalStats());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Issue>> getMyIssues(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(issueService.getIssuesByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issue> getIssueById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(issueService.getIssueById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createIssue(
            HttpServletRequest request,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) return ResponseEntity.status(401).body("Authentication required");

            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                imagePath = fileStorageService.save(image);
            }
            
            Issue issue = new Issue();
            issue.setUserId(userId);
            issue.setCategory(category);
            issue.setDescription(description);
            issue.setLocation(location);
            issue.setImagePath(imagePath);
            
            if (issueService.createIssue(issue)) {
                return ResponseEntity.ok("Issue created successfully");
            }
            return ResponseEntity.internalServerError().body("Failed to create issue");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @RequestParam(value = "proof", required = false) MultipartFile proof) {
        
        try {
            String proofPath = null;
            if (proof != null && !proof.isEmpty()) {
                proofPath = fileStorageService.save(proof);
            }
            
            if (issueService.updateIssueStatus(id, status, proofPath)) {
                return ResponseEntity.ok("Status updated successfully");
            }
            return ResponseEntity.internalServerError().body("Failed to update status");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public List<Issue> searchByLocation(@RequestParam String location) {
        return issueService.getIssuesByLocation(location);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.extractUserId(token);
        }
        return null;
    }
}
