package me.marveldc.kitpvp.players;

public class PlayerData {

    private String uuid;
    private int kills;
    private int deaths;
    private String lastKilled;


    public PlayerData() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public String getLastKilled() {
        return lastKilled;
    }

    public void setLastKilled(String lastKilled) {
        this.lastKilled = lastKilled;
    }

    @Override
    public String toString() {
        return String.format("Player [uuid=%s, kills=%d, deaths=%d, lastKilled=%s]", uuid, kills, deaths, lastKilled);
    }
}
