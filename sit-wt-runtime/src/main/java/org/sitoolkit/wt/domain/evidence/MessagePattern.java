package org.sitoolkit.wt.domain.evidence;

public enum MessagePattern {
    項目をXXします("{}({})を{}します"), 項目にXXをYYします("{}({})に{}を{}します"), 項目をXXしました(
            "{}({})を{}しました"), 項目にXXをYYしました("{}({})に{}を{}しました");

    private String pattern;

    private MessagePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
