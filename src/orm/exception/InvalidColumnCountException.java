package orm.exception;

import orm.database.object.relation.Relation;

public class InvalidColumnCountException extends Exception {
    public InvalidColumnCountException(Relation<?> relation) {
        super("ERROR: The number of column set and the actual column mismatched for the model '"
                + relation.getClass().getSimpleName() + "' !");
    }
}
