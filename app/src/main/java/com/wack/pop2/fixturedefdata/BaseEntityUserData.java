package com.wack.pop2.fixturedefdata;

import com.wack.pop2.identifiers.UniqueIdGenerator;

public abstract class BaseEntityUserData {

    protected int id;

    public BaseEntityUserData() {
        this(UniqueIdGenerator.getUniqueId());
    }

    public BaseEntityUserData(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
