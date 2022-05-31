package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Votes {
    private final List<Vote> votes;

    public Votes() {
        votes = new ArrayList<>();
    }

    public void registerVote(String district, Elector elector, Candidate candidate) {
        votes.add(new Vote(elector, candidate, district));
    }

    public long countBlanks() {
        return errorVotesStream().filter(Vote::isBlank).count();
    }

    public long countNulls() {
        return errorVotesStream().filter(Vote::hasCandidate).count();
    }

    private Stream<Vote> errorVotesStream() {
        return votes.stream().filter(Vote::isForUnofficialCandidate);
    }
}
