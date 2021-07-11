package com.gildedrose;

public class TexttestFixture {
    private static final Class<? extends GildedRose> IMPLEMENTATION = GildedRoseDanny.class;

    public static void main(String[] args) {
        System.out.println("OMGHAI!");

        Item[] items = new Item[] {
                new Item("+5 Dexterity Vest", 10, 20), //
                new Item("Aged Brie", 2, 0), //
                new Item("Elixir of the Mongoose", 5, 7), //
                new Item("Sulfuras, Hand of Ragnaros", 0, 80), //
                new Item("Sulfuras, Hand of Ragnaros", -1, 80),
                new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
                new Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
                new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49),
                // this conjured item does not work properly yet
                new Item("Conjured Mana Cake", 3, 6) };

        GildedRose app = createGildedRoseInstance(items);

        int days = 5;
        if (args.length > 0) {
            days = Integer.parseInt(args[0]) + 1;
        }

        for (int i = 0; i < days; i++) {
            System.out.println("-------- day " + i + " --------");
            System.out.println("name, sellIn, quality");
            for (Item item : items) {
                System.out.println(item);
            }
            System.out.println();
            app.updateQuality();
        }
    }

    private static GildedRose createGildedRoseInstance(Item... items) {
        try {
            return GildedRoseFactory.createInstance(IMPLEMENTATION, items);
        } catch(Exception e) {
            System.out.println("GildedRose app implementation class could not be created. Please check the code. Message: " + e.getMessage());
            System.exit(-1);
        }
        return null; // this is never reached. Alternative would be to just rethrow the exception and let the JVM handle the exit
    }
}
