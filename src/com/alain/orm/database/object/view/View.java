package com.alain.orm.database.object.view;

import java.lang.reflect.InvocationTargetException;

import com.alain.orm.annotation.DatabaseView;
import com.alain.orm.database.connection.DatabaseConnection;
import com.alain.orm.database.object.DatabaseObject;
import com.alain.orm.database.object.relation.Relation;
import com.alain.orm.exception.MissingAnnotationException;
import com.alain.orm.utilities.ModelField;

public abstract class View extends DatabaseObject {

    private Class<? extends Relation> type;

    // I- constructors
    public View(Class<? extends Relation> returnType) throws Exception {
        this.setType(returnType);
    }

    // II- setter
    private void setType(Class<? extends Relation> returnType) {
        this.type = returnType;
    }

    // III- getter
    public Class<? extends Relation> getType() {
        return this.type;
    }
    
    @Override
    public String getTarget() {
        return this.getClass().getAnnotation(DatabaseView.class).view().length() > 0
                ? this.getClass().getAnnotation(DatabaseView.class).view()
                : this.getClass().getSimpleName();
    }

    @Override
    public ModelField[] getColumn() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, IllegalArgumentException {
        return (ModelField[]) this.getType().getMethod("getColumn").invoke(this.getType().getConstructor().newInstance());
    }

    // IV- fetch
    public abstract Relation[] findAll(DatabaseConnection connection);
    
    // V- validation
    private void checkTableAnnotation() throws MissingAnnotationException {
        if (!this.getClass().isAnnotationPresent(DatabaseView.class))
            throw new MissingAnnotationException();
    }

    @Override
    protected void checkClassValidity() throws MissingAnnotationException {
        this.checkTableAnnotation();
    }
}