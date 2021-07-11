package com.gildedrose;

public class GildedRoseFactory {
    /**
     * creates an instance of the store with given implementation and items
     * @param implementation the implementation class to use (normally I'd use a system property, but this is a demo)
     * @param items the store items
     * @throws Exception (yes, I've been lazy here... happens when instance cannot be created)
     */
    public static GildedRose createInstance(Class<? extends GildedRose> implementation, Item... items) throws Exception {
        GildedRose impl = implementation.newInstance();
        impl.init(items);
        return impl;
    }
}
