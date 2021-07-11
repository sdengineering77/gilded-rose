package com.gildedrose;

public interface GildedRose {
    /**
     * update the store items' quality properties when a day passes
     */
    void updateQuality();

    /**
     * sets the items in the store
     * @param items
     */
    void init(Item... items);
}
