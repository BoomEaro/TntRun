package ru.boomearo.tntrun.objects.statistics;

public enum TntStatsType {

    Wins("Побед", "wins"),
    Defeat("Поражений", "defeats");
    
    private final String name;
    private final String dbName;
    
    TntStatsType(String name, String dbName) {
        this.name = name;
        this.dbName = dbName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDBName() {
        return this.dbName;
    }
    
}
