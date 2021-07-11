package com.gildedrose;

import org.apache.commons.lang.StringUtils;

class GildedRoseDanny implements GildedRose {
    private static final int QUALITY_MIN = 0;
    private static final int QUALITY_MAX = 50;

    private static final String ITEM_AGEDBRIE = "Aged Brie";
    private static final String ITEM_SULFURES = "Sulfuras";
    private static final String ITEM_BACKSTAGE = "Backstage passes";
    private static final String ITEM_CONJURED = "Conjured";

    private Item[] items;

    // NOTE: normally I would keep this simple and just program the backstage pass rule separately
    // But since all of these item rules seem to be "sellIn" related and obviously requests to add
    // new items are not out of the question, this approach makes things a bit more flexible, yet
    // at the cost of added complexity and a few lines more of code
    @FunctionalInterface
    private interface SellInRule {
        /**
         * evaluates the rule
         * @param item
         * @return true if rule was executed (also means the chain is ended)
         */
        boolean evaluate(Item item);
    }

    @Override
    public void init(Item... items) {
        this.items = items;
    }

    public void updateQuality() {
        for (Item item : items) {
            if (StringUtils.startsWith(item.name, ITEM_AGEDBRIE)) {
                // sellIn decreases, quality increase by 1 normally or by 2 if sellIn expired
                updateItemProperties(item, 1,
                    updateWhenGTE(0, 1),
                    updateWhenLT(0,2));
            } else if (StringUtils.startsWith(item.name, ITEM_SULFURES)) {
                // does not affect sellIn or quality
                updateItemProperties(item, 0);
            } else if (StringUtils.startsWith(item.name, ITEM_BACKSTAGE)) {
                // sellIn decreases, quality increase by 1 normally, or by 2 if sellIn is <= 10 days,
                // or by 3 is sellin <= 5 days or set to zero if sellIn = -1 days
                updateItemProperties(item, 1,
                    setWhenLT(0, 0),
                    updateWhenLT(5, 3),
                    updateWhenLT(10, 2),
                    updateWhenGTE(10, 1));
            } else if (StringUtils.startsWith(item.name, ITEM_CONJURED)) {
                // sellIn decreases, quality decreases by 2 normally, or by 4 if sellIn expired
                // or set to zero if sellIn < 0 days
                updateItemProperties(item, 1,
                    updateWhenGTE(0, -2),
                    updateWhenLT(0, -4));
            } else {
                // default behaviour: sellIn decreases, quality decreases by 1 normally, or by 2 if sellIn expired
                updateItemProperties(item, 1,
                    updateWhenGTE(0, -1),
                    updateWhenLT(0, -2));
            }
        }
    }

    // for example, daysToGo 0 -> rate -2 == degrade by 2 if sellIn passed
    private SellInRule updateWhenLT(int daysToGo, int rate) {
        return item -> {
            if (item.sellIn < daysToGo) {
                item.quality += rate;
                return true;
            }
            return false;
        };
    }

    // for example, daysToGo 0 -> rate -1 == degrade by 1 if sellIn not passed
    private SellInRule updateWhenGTE(int daysToGo, int rate) {
        return item -> {
            if (item.sellIn >= daysToGo) {
                item.quality += rate;
                return true;
            }
            return false;
        };
    }

    private SellInRule setWhenLT(int daysToGo, int value) {
        return item -> {
            if (item.sellIn < daysToGo) {
                item.quality = value;
                return true;
            }
            return false;
        };
    }

    private void updateItemProperties(Item item, int sellRate, SellInRule... rules) {
        if (sellRate < 0) throw new IllegalArgumentException("SellRate must be >= 0");
        // decrease sellIn
        item.sellIn -= sellRate;
        // evaluate the rules
        for (SellInRule rule : rules) {
            // evaluate the rule, this affects the quality
            if (rule.evaluate(item)) {
                // bound to box
                if (item.quality < QUALITY_MIN) {
                    item.quality = QUALITY_MIN;
                } else if (item.quality > QUALITY_MAX) {
                    item.quality = QUALITY_MAX;
                }
                break;
            }
        }
    }
}
