package com.alain.orm.database.object;

import com.alain.orm.utilities.ModelField;

public abstract class DatabaseObject {
    
    // I- constructor
    public DatabaseObject() throws Exception {
        this.checkClassValidity();
    }

    // II- getter
    public abstract String getTarget();

    public abstract ModelField[] getColumn() throws Exception;

    protected abstract void checkClassValidity() throws Exception;
}