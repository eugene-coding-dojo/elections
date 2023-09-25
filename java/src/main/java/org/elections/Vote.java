package org.elections;

public class Vote {
    private final Elector elector;
    private final Candidate candidate;
    private final String district;

    public Vote(Elector elector, Candidate candidate, String district) {
        this.elector = elector;
        this.candidate = candidate;
        this.district = district;
    }

    public boolean isBlank() {
        return this.candidate.isBlank();
    }

    public boolean hasCandidate() {
        return !this.isBlank();
    }

    public boolean isForUnofficialCandidate() {
        return !this.candidate.isOfficial();
    }
}
