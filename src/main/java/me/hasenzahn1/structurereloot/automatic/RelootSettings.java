package me.hasenzahn1.structurereloot.automatic;

import com.google.common.collect.ImmutableMap;
import me.hasenzahn1.structurereloot.util.TimeUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;

public class RelootSettings implements ConfigurationSerializable {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    public boolean relootOnStartup;
    public int maxRelootAmount;
    public LocalDateTime nextDate;
    public long duration;
    public String durationPattern;

    public RelootSettings(boolean relootOnStartup, int maxRelootAmount, String durationPattern) {
        this.relootOnStartup = relootOnStartup;
        this.maxRelootAmount = maxRelootAmount;
        this.durationPattern = durationPattern;
        duration = TimeUtil.parsePeriodToSeconds(durationPattern);
        nextDate = LocalDateTime.now().plusSeconds(duration);
    }

    public RelootSettings(Map<String, Object> fields){
        relootOnStartup = (boolean) fields.get("relootOnStartup");
        maxRelootAmount = (int) fields.get("maxRelootAmount");
        nextDate = LocalDateTime.parse((String) fields.get("nextReloot"), FORMATTER);
        durationPattern = (String) fields.get("duration");
        duration = TimeUtil.parsePeriodToSeconds(durationPattern);
    }

    public void nextDate(){
        nextDate = nextDate.plusSeconds(duration);
    }


    //Getter
    public boolean isRelootOnStartup() {
        return relootOnStartup;
    }

    public int getMaxRelootAmount() {
        return maxRelootAmount < 0 ? Integer.MAX_VALUE : maxRelootAmount;
    }

    public LocalDateTime getNextDate() {
        return nextDate;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationPattern() {
        return durationPattern;
    }


    //Setter
    public void setRelootOnStartup(boolean relootOnStartup) {
        this.relootOnStartup = relootOnStartup;
    }

    public void setMaxRelootAmount(int maxRelootAmount) {
        this.maxRelootAmount = maxRelootAmount;
    }

    public void setDurationPattern(String durationPattern) {
        this.durationPattern = durationPattern;
    }

    public void setNextDate(LocalDateTime nextDate) {
        this.nextDate = nextDate;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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
}
