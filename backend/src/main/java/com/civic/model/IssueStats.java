package com.civic.model;

import java.util.Map;

public class IssueStats {
    private long total;
    private long pending;
    private long inProgress;
    private long resolved;
    private Map<String, Integer> categoryCounts;
    private int citizenScore;

    public IssueStats() {}

    public IssueStats(long total, long pending, long inProgress, long resolved, Map<String, Integer> categoryCounts, int citizenScore) {
        this.total = total;
        this.pending = pending;
        this.inProgress = inProgress;
        this.resolved = resolved;
        this.categoryCounts = categoryCounts;
        this.citizenScore = citizenScore;
    }

    // Getters and Setters
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getPending() { return pending; }
    public void setPending(long pending) { this.pending = pending; }

    public long getInProgress() { return inProgress; }
    public void setInProgress(long inProgress) { this.inProgress = inProgress; }

    public long getResolved() { return resolved; }
    public void setResolved(long resolved) { this.resolved = resolved; }

    public Map<String, Integer> getCategoryCounts() { return categoryCounts; }
    public void setCategoryCounts(Map<String, Integer> categoryCounts) { this.categoryCounts = categoryCounts; }

    public int getCitizenScore() { return citizenScore; }
    public void setCitizenScore(int citizenScore) { this.citizenScore = citizenScore; }
}
