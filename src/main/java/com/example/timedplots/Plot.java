package com.example.timedplots;

import java.util.UUID;

public class Plot {
    private final UUID owner;
    private final int id;
    private final int centerX;
    private final int centerZ;
    private long creationTime;
    private boolean locked;

    public Plot(UUID owner, int id, int centerX, int centerZ, long creationTime, boolean locked) {
        this.owner = owner;
        this.id = id;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.creationTime = creationTime;
        this.locked = locked;
    }

    public UUID getOwner() { return owner; }
    public int getId() { return id; }
    public int getCenterX() { return centerX; }
    public int getCenterZ() { return centerZ; }
    public long getCreationTime() { return creationTime; }
    public boolean isLocked() { return locked; }

    public void setLocked(boolean locked) { this.locked = locked; }
    public void setCreationTime(long time) { this.creationTime = time; }
}