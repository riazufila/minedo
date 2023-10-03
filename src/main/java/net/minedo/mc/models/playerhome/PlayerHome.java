package net.minedo.mc.models.playerhome;

import org.bukkit.World;

public class PlayerHome {

    private int playerId;
    private String name;
    private World worldType;
    private double coordinateX;
    private double coordinateY;
    private double coordinateZ;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorldType() {
        return worldType;
    }

    public void setWorldType(World worldType) {
        this.worldType = worldType;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public double getCoordinateZ() {
        return coordinateZ;
    }

    public void setCoordinateZ(double coordinateZ) {
        this.coordinateZ = coordinateZ;
    }

}
