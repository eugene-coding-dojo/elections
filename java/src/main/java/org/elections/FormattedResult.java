package org.elections;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FormattedResult {
    private final Locale locale;
    private final String percentFormat;
    private final Map<String, String> result;

    public FormattedResult(Locale locale, String percentFormat) {
        this.locale = locale;
        this.percentFormat = percentFormat;
        result = new HashMap<>();
    }

    public void addCandidateResult(String candidate, Fraction candidateResult) {
        putFormattedValue(candidate, candidateResult);
    }

    private void putFormattedValue(String key, Fraction value) {
        result.put(key, String.format(locale, percentFormat, value.asPercent()));
    }

    public void addBlankVotes(Fraction blanks) {
        putFormattedValue("Blank", blanks);
    }

    public void addNullVotes(Fraction nulls) {
        putFormattedValue("Null", nulls);
    }

    public void addAbstention(Fraction abstentionResult) {
        putFormattedValue("Abstention", abstentionResult);
    }

    public Map<String, String> result() {
        return new HashMap<>(this.result);
    }
}
