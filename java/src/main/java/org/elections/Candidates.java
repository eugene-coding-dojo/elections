package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Candidates {
    private final List<Candidate> candidates = new ArrayList<>();

    public void addOfficial(String name) {
        candidates.add(Candidate.official(name));
    }

    public void addUnofficial(String name) {
        candidates.add(Candidate.unofficial(name));
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

    public boolean isUnregistered(String candidate) {
        return candidates.stream().noneMatch(sameNameAs(candidate));
    }

    private Predicate<Candidate> sameNameAs(String candidate) {
        return c -> candidate.equals(c.name());
    }

    public String get(int index) {
        return candidates.get(index).name();
    }

    public Candidate findByName(String candidateName) {
        return candidates.stream()
                .filter(candidate -> candidate.hasName(candidateName))
                .findFirst().orElse(null);
    }
}
