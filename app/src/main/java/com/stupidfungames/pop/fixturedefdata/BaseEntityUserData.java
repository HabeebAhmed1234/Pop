package com.stupidfungames.pop.fixturedefdata;

import com.stupidfungames.pop.identifiers.UniqueIdGenerator;

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

    public void reset() {
        id = 0;
    }
}
