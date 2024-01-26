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

    /**
     * Should be used when creating the settings Object via code.
     *
     * @param relootOnStartup Whether a reloot should be started when the server starts.
     * @param maxRelootAmount The maximum amount of block/entities that should be relooted
     * @param durationPattern The amount of time between Reloots
     */
    public RelootSettings(boolean relootOnStartup, int maxRelootAmount, String durationPattern) {
        this.relootOnStartup = relootOnStartup;
        this.maxRelootAmount = maxRelootAmount;
        this.durationPattern = durationPattern;
        duration = TimeUtil.parsePeriodToSeconds(durationPattern);
        nextDate = LocalDateTime.now().plusSeconds(duration);
        shouldBeRelooted = relootOnStartup;
    }

    /**
     * Should not be used!!! Only for usage with loading from the config
     *
     * @param fields
     */
    public RelootSettings(Map<String, Object> fields) {
        relootOnStartup = (boolean) fields.get("relootOnStartup");
        maxRelootAmount = (int) fields.get("maxRelootAmount");
        nextDate = LocalDateTime.parse((String) fields.get("nextReloot"), FORMATTER);
        durationPattern = (String) fields.get("duration");
        duration = TimeUtil.parsePeriodToSeconds(durationPattern);
        shouldBeRelooted = relootOnStartup;
    }

    /**
     * Calculates when the nextReloot should take place, provided the reloot just happened
     */
    public void nextDate() {
        nextDate = LocalDateTime.now().plusSeconds(duration);
        shouldBeRelooted = false;
    }

    /**
     * Returns true if the setting (block/entity) in the respective world should be relooted
     *
     * @return whether this setting needs a reloot
     */
    public boolean needsUpdate() {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), nextDate) <= 0 || shouldBeRelooted;
    }

    /**
     * Returns the maximum amount of blocks/entities that should be relooted. Returns Integer.MAX_VALUE if all elements should be relooted
     *
     * @return the amount of elements that should be relooted
     */
    public int getMaxRelootAmount() {
        return maxRelootAmount < 0 ? Integer.MAX_VALUE : maxRelootAmount;
    }

    /**
     * Method to set the duration pattern and convert them to a second's duration
     *
     * @param durationPattern The Duration pattern, e.g. 2h30m
     */
    public void setDurationPattern(String durationPattern) {
        this.durationPattern = durationPattern;
        this.duration = TimeUtil.parsePeriodToSeconds(durationPattern);
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
