package ru.boomearo.tntrun.objects;

import org.bukkit.Material;
import ru.boomearo.gamecontrol.objects.statistics.IStatsType;

public enum TntStatsType implements IStatsType {

    Wins("Побед", "wins", Material.IRON_SWORD),
    Defeat("Поражений", "defeats", Material.SKELETON_SKULL);

    private final String name;
    private final String dbName;
    private final Material icon;

    TntStatsType(String name, String dbName, Material icon) {
        this.name = name;
        this.dbName = dbName;
        this.icon = icon;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getTableName() {
        return this.dbName;
    }

    @Override
    public Material getIcon() {
        return this.icon;
    }


}
