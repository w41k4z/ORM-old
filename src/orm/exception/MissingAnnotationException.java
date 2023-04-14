package orm.exception;

import orm.database.object.relation.Relation;

public class MissingAnnotationException extends Exception {
    public MissingAnnotationException(Relation<?> relation) {
        super("ERROR: The model '" + relation.getClass().getSimpleName() + "' must be annoted with @Table !");
    }
}
