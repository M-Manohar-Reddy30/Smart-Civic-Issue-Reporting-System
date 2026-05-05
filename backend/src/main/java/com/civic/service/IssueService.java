package com.civic.service;

import com.civic.model.Issue;
import com.civic.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final com.civic.repository.UserRepository userRepository;

    public IssueService(IssueRepository issueRepository, com.civic.repository.UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public Issue getIssueById(Long id) {
        return issueRepository.findById(id);
    }

    public boolean createIssue(Issue issue) {
        issue.setStatus("Pending");
        boolean saved = issueRepository.save(issue) > 0;
        if (saved && issue.getUserId() != null) {
            userRepository.updatePointsAndIssueCount(issue.getUserId(), 10, 1);
        }
        return saved;
    }

    public boolean updateIssueStatus(Long id, String newStatus, String proofPath) {
        Issue currentIssue = issueRepository.findById(id);
        String currentStatus = currentIssue.getStatus();

        // Strict Workflow Logic
        if ("In Progress".equalsIgnoreCase(newStatus)) {
            if (!"Pending".equalsIgnoreCase(currentStatus)) {
                throw new IllegalArgumentException("Can only move to 'In Progress' from 'Pending'");
            }
            if (proofPath == null || proofPath.isEmpty()) {
                throw new IllegalArgumentException("Start proof is mandatory for 'In Progress' status");
            }
            return issueRepository.updateStatus(id, "In Progress", proofPath, true) > 0;
        } else if ("Resolved".equalsIgnoreCase(newStatus)) {
            if (!"In Progress".equalsIgnoreCase(currentStatus)) {
                throw new IllegalArgumentException("Can only move to 'Resolved' from 'In Progress'");
            }
            if (proofPath == null || proofPath.isEmpty()) {
                throw new IllegalArgumentException("End proof is mandatory for 'Resolved' status");
            }
            boolean updated = issueRepository.updateStatus(id, "Resolved", proofPath, false) > 0;
            if (updated && currentIssue.getUserId() != null) {
                userRepository.updatePointsAndIssueCount(currentIssue.getUserId(), 20, 0);
            }
            return updated;
        } else {
            throw new IllegalArgumentException("Invalid status transition");
        }
    }

    public List<Issue> getIssuesByLocation(String location) {
        return issueRepository.findByLocation(location);
    }

    public List<Issue> getIssuesByUserId(Long userId) {
        return issueRepository.findByUserId(userId);
    }

    public com.civic.model.IssueStats getGlobalStats() {
        long total = issueRepository.countByStatus(null);
        long pending = issueRepository.countByStatus("Pending");
        long inProgress = issueRepository.countByStatus("In Progress");
        long resolved = issueRepository.countByStatus("Resolved");

        // Gamification: +10 per report, +20 per resolution
        int citizenScore = (int) (total * 10 + resolved * 20);

        return new com.civic.model.IssueStats(total, pending, inProgress, resolved, null, citizenScore);
    }
}
