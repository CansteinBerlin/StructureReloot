package me.hasenzahn1.structurereloot.config;


import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.config.LanguageConfig;

public class DefaultConfig extends CustomConfig {
    public DefaultConfig() {
        super(StructureReloot.getInstance(), "config.yml");
    }
}
