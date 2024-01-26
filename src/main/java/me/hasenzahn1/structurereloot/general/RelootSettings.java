package me.hasenzahn1.structurereloot.general;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import me.hasenzahn1.structurereloot.util.TimeUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Getter
@Setter
public class RelootSettings implements ConfigurationSerializable {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

    //Serialized
    public boolean relootOnStartup;
    public int maxRelootAmount;
    public LocalDateTime nextDate;
    public String durationPattern;

    //Internal
    public long duration;
    private boolean shouldBeRelooted;

    public RelootSettings(boolean relootOnStartup, int maxRelootAmount, String durationPattern) {
        this.relootOnStartup = relootOnStartup;
        this.maxRelootAmount = maxRelootAmount;
        this.durationPattern = durationPattern;
        duration = TimeUtil.parsePeriodToSeconds(durationPattern);
        nextDate = LocalDateTime.now().plusSeconds(duration);
        shouldBeRelooted = relootOnStartup;
    }

    public RelootSettings(Map<String, Object> fields) {
        relootOnStartup = (boolean) fields.get("relootOnStartup");
        maxRelootAmount = (int) fields.get("maxRelootAmount");
        nextDate = LocalDateTime.parse((String) fields.get("nextReloot"), FORMATTER);
        durationPattern = (String) fields.get("duration");
        duration = TimeUtil.parsePeriodToSeconds(durationPattern);
        shouldBeRelooted = relootOnStartup;
    }

    public void nextDate() {
        nextDate = LocalDateTime.now().plusSeconds(duration);
        shouldBeRelooted = false;
    }

    public boolean needsUpdate() {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), nextDate) <= 0 || shouldBeRelooted;
    }

    public int getMaxRelootAmount() {
        return maxRelootAmount < 0 ? Integer.MAX_VALUE : maxRelootAmount;
    }

    public long setDurationPattern(String durationPattern) {
        this.durationPattern = durationPattern;
        this.duration = TimeUtil.parsePeriodToSeconds(durationPattern);
        return this.duration;
    }

    @Override
    public Map<String, Object> serialize() {
        return new ImmutableMap.Builder<String, Object>()
                .put("relootOnStartup", relootOnStartup)
                .put("maxRelootAmount", maxRelootAmount)
                .put("duration", durationPattern)
                .put("nextReloot", FORMATTER.format(nextDate))
                .build();
    }

    @Override
    public String toString() {
        return "RelootSettings{" +
                "relootOnStartup=" + relootOnStartup +
                ", maxRelootAmount=" + maxRelootAmount +
                ", nextDate=" + FORMATTER.format(nextDate) +
                ", duration=" + (duration == Integer.MAX_VALUE ? -1 : duration) +
                ", durationPattern='" + durationPattern + '\'' +
                '}';
    }
}
