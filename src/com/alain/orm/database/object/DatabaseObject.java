package com.alain.orm.database.object;

public abstract class DatabaseObject {
    
    // I- constructor
    public DatabaseObject() throws Exception {
        this.checkClassValidity();
    }

    // II- getter
    public abstract String getTarget();

    public abstract String[] getColumn() throws Exception;

    protected abstract void checkClassValidity() throws Exception;
}
