package com.alain.orm.database.object;

public abstract class DatabaseObject {
    
    // I- constructor
    public DatabaseObject() {}

    


    // II- getter
    public abstract String getTarget();

    public abstract String[] getColumn();
}
