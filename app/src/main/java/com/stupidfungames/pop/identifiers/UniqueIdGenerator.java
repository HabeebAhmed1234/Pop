package com.stupidfungames.pop.identifiers;

public class UniqueIdGenerator {

    private static int id = 0;

    public static int getUniqueId() {
        return id++;
    }
}
