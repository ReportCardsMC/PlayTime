package me.reportcardsmc.github.playtime.utils.server;

import com.google.gson.Gson;

public class ServerStats {

    public long totalPlayTime;
    public long totalJoins;
    public long uniqueJoins;

    public ServerStats(long totalPlayTime, long totalJoins, long uniqueJoins) {
        this.totalPlayTime=totalPlayTime;
        this.totalJoins=totalJoins;
        this.uniqueJoins=uniqueJoins;
    }

    public long getTotalJoins() {
        return totalJoins;
    }
    public long getTotalPlayTime() {
        return totalPlayTime;
    }
    public long getUniqueJoins() {
        return uniqueJoins;
    }
    public long getAverageSession() {
        return totalPlayTime/(totalJoins + 1);
    }

    public void setTotalJoins(long totalJoins) {
        this.totalJoins = totalJoins;
    }
    public void setTotalPlayTime(long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }
    public void setUniqueJoins(long uniqueJoins) {
        this.uniqueJoins = uniqueJoins;
    }

    public void addTotalJoin() {this.totalJoins++;}
    public void addTotalPlayTime(long playTime) {this.totalPlayTime+=playTime;}
    public void addUniqueJoin() {this.uniqueJoins++;}

    public String getJson() {
        return new Gson().toJson(this);
    }
}
