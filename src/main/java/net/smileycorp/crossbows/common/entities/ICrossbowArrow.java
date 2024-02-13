package net.smileycorp.crossbows.common.entities;

public interface ICrossbowArrow {

    void setShotFromCrossbow(boolean crossbow);

    void setPierceLevel(byte level);

    boolean shotFromCrossbow();

    byte getPierceLevel();

}
