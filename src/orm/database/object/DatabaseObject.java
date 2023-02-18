package orm.database.object;

import orm.database.object.relation.ModelField;

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