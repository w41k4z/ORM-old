package orm.exception;

import orm.database.object.relation.Relation;

public class MissingSetterException extends Exception {
    public MissingSetterException(Relation<?> relation) {
        super("ERROR: Every column field must have a string setter for the model '"
                + relation.getClass().getSimpleName() + "' !");
    }
}
