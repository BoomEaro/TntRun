package ru.boomearo.tntrun.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.sqlite.JDBC;

import ru.boomearo.serverutils.utils.other.ExtendedThreadFactory;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.database.sections.SectionStats;
import ru.boomearo.tntrun.objects.statistics.TntStatsType;

public class Sql {
    private final Connection connection;
    private final ExecutorService executor;

    private static final String CON_STR = "jdbc:sqlite:[path]database.db";
    private static Sql instance = null;

    public static void initSql() throws SQLException {
        if (instance != null) {
            return;
        }

        instance = new Sql();
    }

    public static Sql getInstance() {
        return instance;
    }

    private Sql() throws SQLException {
        DriverManager.registerDriver(new JDBC());

        this.executor = Executors.newFixedThreadPool(1, new ExtendedThreadFactory("TntRun-SQL", 3));

        this.connection = DriverManager.getConnection(CON_STR.replace("[path]", TntRun.getInstance().getDataFolder() + File.separator));

        for (TntStatsType type : TntStatsType.values()) {
            createNewDatabaseStatsData(type);
        }
    }

    public Future<List<SectionStats>> getAllStatsData(TntStatsType type) {
        return this.executor.submit(() -> {
            try (Statement statement = this.connection.createStatement()) {
                List<SectionStats> collections = new ArrayList<>();
                ResultSet resSet = statement.executeQuery("SELECT id, name, value FROM " + type.getDBName());
                while (resSet.next()) {
                    collections.add(new SectionStats(resSet.getInt("id"), resSet.getString("name"), resSet.getInt("value")));
                }
                return collections;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        });
    }

    public void putStatsData(TntStatsType type, String name, double value) {
        this.executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO " + type.getDBName() + "(`name`, `value`) " +
                            "VALUES(?, ?)")) {
                statement.setString(1, name);
                statement.setDouble(2, value);
                statement.execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateStatsData(TntStatsType type, String name, double value) {
        this.executor.execute(() -> {
            String sql = "UPDATE " + type.getDBName() + " SET value = ? "
                    + "WHERE name = ?";

            try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {

                pstmt.setDouble(1, value);
                pstmt.setString(2, name);
                pstmt.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void createNewDatabaseStatsData(TntStatsType type) {
        String sql = "CREATE TABLE IF NOT EXISTS " + type.getDBName() + " (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	value double NOT NULL\n"
                + ");";

        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sql);
            TntRun.getInstance().getLogger().info("Таблица " + type.getDBName() + " успешно загружена.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws SQLException, InterruptedException {
        this.executor.shutdown();
        this.executor.awaitTermination(15, TimeUnit.SECONDS);
        this.connection.close();
    }

}
