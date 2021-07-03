package me.xepos.rpg.database;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.enums.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;


public class MySQLDatabaseManager extends DatabaseManager {

    private final static XRPG plugin = XRPG.getPlugin(XRPG.class);
    private final static FileConfiguration config = plugin.getConfig();
    private Connection connection;
    private final SkillLoader skillLoader;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            final String host = config.getString("MySQL.host", "localhost");
            final String port = config.getString("MySQL.port", "3306");
            final String database = config.getString("MySQL.database");
            final String username = config.getString("MySQL.username", "root");
            final String password = config.getString("MySQL.password");
            final boolean useSSL = config.getBoolean("MySQL.SSL", false);

            String connectionString = "jdbc:mysql://" + host
                    + ":" + port + "/" + database;

            if(useSSL)
                connectionString +=
                        "?verifyServerCertificate=false"+
                                "&useSSL=true"+
                                "&requireSSL=true";
            else
                connectionString+= "?useSSL=false";


            connection = DriverManager.getConnection(connectionString, username, password);
        }

        if(isConnected())
        {
            Bukkit.getLogger().info("Successfully connected to database");
            createTable("xrpg_classes");
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected MySQLDatabaseManager(SkillLoader skillLoader) {
        setDatabaseType(DatabaseType.MYSQL);
        this.skillLoader = skillLoader;
        try {
            connect();
        } catch (ClassNotFoundException | SQLException e) {
            Bukkit.getLogger().info("Database connection failed.");
            Bukkit.getServer().getPluginManager().disablePlugin(XRPG.getPlugin(XRPG.class));
        }
    }

    @Override
    public void loadPlayerData(UUID playerId) {
        if (!uuidExists(playerId)) {
            createPlayer(playerId);
        }
        XRPGPlayer xrpgPlayer = getPlayerData(playerId);
        plugin.addRPGPlayer(playerId, xrpgPlayer);
    }

    @Override
    public PlayerData savePlayerData(XRPGPlayer xrpgPlayer) {
        updatePlayer(xrpgPlayer);
        return null;
    }

    private void createTable(String tableName){
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                    + tableName + " (uuid VARCHAR(100),classId VARCHAR(20),tickets VARCHAR(2),PRIMARY KEY (uuid))");
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void createPlayer(UUID playerId){
//        try {
//            if (!uuidExists(playerId)) {
//                PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO xrpg_classes (uuid,classId,tickets) VALUES (?,?,2)");
//                ps.setString(1, playerId.toString());
//                ps.setString(2, plugin.getDefaultClassId());
//
//                ps.executeUpdate();
//            }
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
    }

    private XRPGPlayer getPlayerData(UUID playerId){
        try{
            if (uuidExists(playerId)){
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM xrpg_classes WHERE uuid=?");
                ps.setString(1, playerId.toString());

                ResultSet results = ps.executeQuery();
                if (results.next()) {
                    String classId = getClassId(playerId);
                    XRPGPlayer xrpgPlayer = new XRPGPlayer(playerId, classId);
                    //classLoader.loadClass(classId, xrpgPlayer);
                    //xrpgPlayer.setFreeChangeTickets(results.getInt("tickets"));
                    return xrpgPlayer;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private boolean uuidExists(UUID playerId){
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM xrpg_classes WHERE uuid=?");
            ps.setString(1, playerId.toString());

            ResultSet resultSet = ps.executeQuery();
            return resultSet.next(); // True if player is found

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private String getClassId(UUID playerId)
    {
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT classId FROM xrpg_classes WHERE uuid=?");
            ps.setString(1, playerId.toString());

            ResultSet results = ps.executeQuery();
            if (results.next()){
                return results.getString("classId");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "";
    }

    private void setClassId(UUID playerId, String classId){
        try{
            PreparedStatement ps = connection.prepareStatement("UPDATE xrpg_classes SET classId=? WHERE uuid=?");
            ps.setString(1, classId);
            ps.setString(2, playerId.toString());

            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updatePlayer(XRPGPlayer xrpgPlayer){
        try{
            PreparedStatement ps = connection.prepareStatement("UPDATE xrpg_classes SET classId=?,tickets=? WHERE uuid=?");
            ps.setString(1, xrpgPlayer.getClassId());
            //ps.setString(2, String.valueOf(xrpgPlayer.getFreeChangeTickets()));
            ps.setString(3, xrpgPlayer.getPlayerId().toString());

            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
