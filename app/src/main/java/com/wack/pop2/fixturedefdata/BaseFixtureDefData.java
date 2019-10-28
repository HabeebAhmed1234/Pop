package com.wack.pop2.fixturedefdata;

import com.wack.pop2.identifiers.UniqueIdGenerator;

public abstract class BaseFixtureDefData {

    protected int id;

    public BaseFixtureDefData() {
        this(UniqueIdGenerator.getUniqueId());
    }

    public BaseFixtureDefData(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
