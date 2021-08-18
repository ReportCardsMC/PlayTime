package me.reportcardsmc.github.playtime.utils;

import com.google.gson.Gson;

public class PlayerStats {
    public long timePlayed;
    public long lastSession;
    public long sessions;

    public PlayerStats(long timePlayed, long lastSession, long sessions, long totalSessionTime) {
        this.timePlayed = timePlayed;
        this.lastSession = lastSession;
        this.sessions = sessions;
    }

    public String getJson() {
        return new Gson().toJson(this);
    } // Get json of this object

    public long getTimePlayed() {
        return timePlayed;
    } // Get time played of user object

    public long getLastSession() {
        return lastSession;
    } // Get last session of user

    public long getSessions() {
        return sessions;
    }

    public long setLastSession(long lastSession) {
        this.lastSession = lastSession;
        return lastSession;
    } // Set last session

    public long setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
        return timePlayed;
    } // Set time played

    public void setSessions(long sessions) {
        this.sessions = sessions;
    }

    public void addSession(long amount) {
        this.sessions += amount;
    }

    public long averageSession() {
        return this.timePlayed / (sessions + 1);
    }
    public long averageSessionOffline() {
        return this.timePlayed / (sessions);
    }
}
