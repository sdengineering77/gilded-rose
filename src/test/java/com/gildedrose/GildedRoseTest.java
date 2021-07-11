package com.gildedrose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class GildedRoseTest {

    private static final Class<? extends GildedRose> IMPLEMENTATION = GildedRoseDanny.class;

//	- v Once the sell by date has passed, Quality degrades twice as fast
//	- v The Quality of an item is never negative
//	- v "Aged Brie" actually increases in Quality the older it gets
//	- v The Quality of an item is never more than 50
//  - v "Sulfuras", being a legendary item, never has to be sold or decreases in Quality
//	- v "Backstage passes", like aged brie, increases in Quality as its SellIn value approaches;
//         v Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but
//         v Quality drops to 0 after the concert
//
//  - "Conjured" items degrade in Quality twice as fast as normal items

    // "Conjured" items degrade in Quality twice as fast as normal items
    @Test
    void qualityShouldDegradeTwiceAsFastWhenConjured() {
        Item conjuredItem3daysTogo = new Item("Conjured Mana Cake", 3, 6);
        // process
        processItems(conjuredItem3daysTogo);

        // should decrease double
        assertEquals(4, conjuredItem3daysTogo.quality);

    }

    // Once the sell by date has passed, Quality degrades twice as fast
    @Test
    void qualityShouldDegradeTwiceAsFastWhenSellInPassed() {
        Item fooItem0daysTogo = new Item("Foo", 0, 10);
        // process
        processItems(fooItem0daysTogo);

        // should decrease double
        assertEquals(8, fooItem0daysTogo.quality);
    }

    // When sell by date hasn't passed, Quality degrades normal
    @Test
    void qualityShouldDegradeWhenDayPasses() {
        Item fooItem1daysTogo = new Item("Foo", 1, 10);
        // process
        processItems(fooItem1daysTogo);

        // should decrease single
        assertEquals(9, fooItem1daysTogo.quality);
    }

    // The Quality of an item is never negative
    // The Quality of an item is never more than 50 (only applies to aged brie, does not apply to sulfuras)
    @Test
    void qualityShouldBeBoxed() {
        Item fooItem1daysTogo = new Item("Foo", 1, 0);
        Item agedBrie0daysTogo = new Item("Aged Brie", 0, 49); // edge case, increases by 2
        Item agedBrie1daysTogo = new Item("Aged Brie", 0, 49);
        // process
        processItems(fooItem1daysTogo, agedBrie0daysTogo, agedBrie1daysTogo);

        // should be boxed at zero
        assertEquals(0, fooItem1daysTogo.quality);
        // should be boxed at 50
        assertEquals(50, agedBrie0daysTogo.quality);
        assertEquals(50, agedBrie1daysTogo.quality);
    }

    //	Test sellIn drop
    @Test
    void sellInShouldDecrease() {
        Item fooItem1daysTogo = new Item("Foo", 1, 10);
        // process
        processItems(fooItem1daysTogo);

        // should decrease sellIn
        assertEquals(0, fooItem1daysTogo.sellIn);
    }

    // "Aged Brie" actually increases in Quality the older it gets
    // NOTE: is it really logical that quality increases twice as fast when sellIn has passed in this case?
    @Test
    void agedBrieShouldIncreaseQuality() {
        Item agedBrie1daysTogo = new Item("Aged Brie", 1, 0);
        Item agedBrie0daysTogo = new Item("Aged Brie", 0, 0);
        // process
        processItems(agedBrie1daysTogo, agedBrie0daysTogo);

        // should increase quality single before selldate has passed
        assertEquals(1, agedBrie1daysTogo.quality);
        // should increase double after selldate has passed
        assertEquals(2, agedBrie0daysTogo.quality);
    }

    // "Sulfuras", being a legendary item, never has to be sold or decreases in Quality
    @Test
    void sulfurasShouldNotAffectQualityOrSellIn() {
        Item sulfuras1daysTogo = new Item("Sulfuras, Hand of Ragnaros", 1, 80);
        Item sulfuras0daysTogo = new Item("Sulfuras, Hand of Ragnaros", 0, 80);
        // process
        processItems(sulfuras1daysTogo, sulfuras0daysTogo);

        // should not increase quality single before selldate has passed
        assertEquals(80, sulfuras1daysTogo.quality);
        // should not increase double after selldate has passed
        assertEquals(80, sulfuras0daysTogo.quality);
        // should not affected sellIn
        assertEquals(1, sulfuras1daysTogo.sellIn);
        assertEquals(0, sulfuras0daysTogo.sellIn);
    }

    // "Backstage passes", like aged brie, increases in Quality as its SellIn value approaches;
    //    Quality increases by 2 when there are 10 days or less and by 3 when there are 5 days or less but
    //    Quality drops to 0 after the concert
    @Test
    void backStagePassesShouldIncreaseQualityBeforeSellIn() {
        Item backstagePass11daysTogo = new Item("Backstage passes to a TAFKAL80ETC concert", 11, 20);
        Item backstagePass10daysTogo= new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20);
        Item backstagePass5daysTogo = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20);
        Item backstagePass0daysTogo = new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20);
        // process
        processItems(backstagePass11daysTogo, backstagePass10daysTogo, backstagePass5daysTogo, backstagePass0daysTogo);

        // should increase quality single before selldate has passed
        assertEquals(21, backstagePass11daysTogo.quality);
        assertEquals(22, backstagePass10daysTogo.quality);
        assertEquals(23, backstagePass5daysTogo.quality);
        // should have zero quality after sell date passed
        assertEquals(0, backstagePass0daysTogo.quality);

    }

    private void processItems(Item... items) {
        try {
            GildedRose gildedRose = GildedRoseFactory.createInstance(IMPLEMENTATION, items);
            gildedRose.updateQuality();
        } catch(Exception e) {
            // should never happen, but...
            fail(e);
        }
    }

}
