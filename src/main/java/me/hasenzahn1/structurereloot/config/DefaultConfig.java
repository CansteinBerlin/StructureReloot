package me.hasenzahn1.structurereloot.config;


import me.hasenzahn1.structurereloot.StructureReloot;

public class DefaultConfig extends CustomConfig {
    public DefaultConfig() {
        super(StructureReloot.getInstance(), "config.yml");
    }
}
