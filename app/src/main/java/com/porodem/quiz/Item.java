package com.porodem.quiz;

/**
 * Created by porod on 09.11.2015.
 */
public class Item {
    int itemID;
    String title;
    int img;
    String sound;
    int level;
    int gold;
    String param;
    String use;
    String info;

    public void Item(int itemID, String title, int img, String sound, int level, int gold, String param,
                     String use, String info) {
        this.itemID = itemID;
        this.title = title;
        this.img = img;
        this.sound = sound;
        this.level = level;
        this.gold = gold;
        this.param = param;
        this.use = use;
        this.info = info;
    }
}
