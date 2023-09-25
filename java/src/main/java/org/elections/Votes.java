package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

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

    public long countValid() {
        return votes.size() - errorVotesStream().count();
    }

    public long countAllVotes() {
        return votes.size();
    }

    public Map<String, Long> resultForNoDistrict() {
        return votesForOfficialCandidates().collect(groupingBy(Vote::candidateName, counting()));
    }

    private Stream<Vote> votesForOfficialCandidates() {
        return votes.stream().filter(Vote::isForOfficialCandidate);
    }

    public String districtWinner(String districtName) {
        return resultForDistrict(districtName).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Map<String, Long> resultForDistrict(String districtName) {
        return votesForOfficialCandidates()
                .filter(vote -> vote.isForDistrict(districtName))
                .collect(groupingBy(Vote::candidateName, counting()));
    }
}
