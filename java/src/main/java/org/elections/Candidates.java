package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Candidates {
    private final List<Candidate> candidates = new ArrayList<>();

    public void addOfficial(String name) {
        candidates.add(Candidate.official(name));
    }

    public void addUnofficial(String name) {
        candidates.add(Candidate.unofficial(name));
    }

    public int indexOf(String candidate) {
        for (int i = 0; i < candidates.size(); i++) {
            if (candidate.equals(candidates.get(i).name())) {
                return i;
            }
        }
        return -1;
    }

    public List<String> officialCandidates() {
        return candidates.stream()
                .filter(Candidate::isOfficial)
                .map(Candidate::name)
                .collect(Collectors.toUnmodifiableList());
    }

    public long officialCandidatesCount() {
        return candidates.stream().filter(Candidate::isOfficial).count();
    }

    public boolean isOfficial(String candidate) {
        return candidates.stream().filter(c -> candidate.equals(c.name())).anyMatch(Candidate::isOfficial);
    }
}
