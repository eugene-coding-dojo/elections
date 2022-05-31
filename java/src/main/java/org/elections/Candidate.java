package org.elections;

public class Candidate {
    private final String name;
    private final boolean isOfficial;

    public Candidate(String name, boolean isOfficial) {
        this.name = name;
        this.isOfficial = isOfficial;
    }

    public static Candidate official(String name) {
        return new Candidate(name, true);
    }

    public static Candidate unofficial(String name) {
        return new Candidate(name, false);
    }

    public String name() {
        return this.name;
    }

    public boolean isOfficial() {
        return this.isOfficial;
    }

    public boolean hasName(String candidateName) {
        return this.name.equals(candidateName);
    }

    public boolean isBlank() {
        return this.name.trim().isEmpty();
    }
}
