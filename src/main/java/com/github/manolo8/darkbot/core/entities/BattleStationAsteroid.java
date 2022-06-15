package com.github.manolo8.darkbot.core.entities;


public class BattleStationAsteroid extends BattleStation {

    public BattleStationAsteroid(int id, long address) {
        super(id, address);
        this.asteroid = true;
    }

    @Override
    public String toString() {
        return super.toString() + ", Asteroid";
    }
}
