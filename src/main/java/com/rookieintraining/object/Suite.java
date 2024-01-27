package com.rookieintraining.object;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Suite {

    private static Suite INSTANCE;

    public UUID id;
    public String suiteName;
    public List<String> scenarioList;
    public int totalPassed;
    public int totalFailed;
    public int totalSkipped;
    public Date startTime;
    public Date endTime;

    public static Suite createOrGetInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new Suite();
            INSTANCE.id = UUID.randomUUID();
        }
        return INSTANCE;
    }
}
