package orm.exception;

import orm.database.object.relation.Relation;

public class PrimaryKeyCountException extends Exception {
    public PrimaryKeyCountException(Relation<?> relation) {
        super("ERROR: The number of primary key for a table can not be greater than 1 !\n\n SOURCE: '"
                + relation.getClass().getSimpleName() + "'");
    }
}
