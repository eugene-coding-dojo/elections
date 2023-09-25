package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ElectionsStrategy {
    protected List<String> candidates = new ArrayList<>();
    protected List<String> officialCandidates = new ArrayList<>();

    public abstract void voteFor(String candidate, String electorDistrict);

    public abstract void calculateResults(Elections.VotesCountByType votes, Map<String, String> results);

    public abstract void addCandidate(String candidate);
}
