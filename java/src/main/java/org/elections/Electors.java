package org.elections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Electors {
    private final List<Elector> electors;

    private Electors() {
        electors = new ArrayList<>();
    }

    public static Electors fromMapByDistrict(Map<String, List<String>> electorsByDistrict) {
        Electors instance = new Electors();
        for (Map.Entry<String, List<String>> district : electorsByDistrict.entrySet()) {
            instance.addAll(district.getValue().stream()
                    .map(el -> Elector.withDistrict_Name_(district.getKey(), el))
                    .collect(Collectors.toList()));
        }
        return instance;
    }

    private void addAll(List<Elector> electorList) {
        electors.addAll(electorList);
    }

    public int size() {
        return electors.size();
    }

    public Elector findByName(String electorName) {
        return electors.stream()
                .filter(elector -> elector.hasName(electorName))
                .findAny()
                .orElse(null);
    }
}
