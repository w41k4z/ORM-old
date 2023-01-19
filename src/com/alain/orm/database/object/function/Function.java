package com.alain.orm.database.object.function;

import java.lang.reflect.InvocationTargetException;

import com.alain.orm.annotation.DatabaseFunction;
import com.alain.orm.database.connection.Connection;
import com.alain.orm.database.object.DatabaseObject;
import com.alain.orm.database.object.relation.Relation;
import com.alain.orm.exception.MissingAnnotationException;

public abstract class Function extends DatabaseObject {
    
    private Object[] parameters;
    private Class<? extends Relation> type;

    // I- constructors
    public Function(Object[] parameters, Class<? extends Relation> returnType) throws Exception {
        this.setParameters(parameters);
        this.setType(returnType);
    }

    // II- setter
    private void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    private void setType(Class<? extends Relation> returnType) {
        this.type = returnType;
    }

    // III- getter
    public Object[] getParameters() {
        return this.parameters;
    }

    public Class<? extends Relation> getType() {
        return this.type;
    }

    @Override
    public String getTarget() {
        String[] paramStrVer = new String[this.getParameters().length];
        int index = 0;
        for (Object parameter : this.getParameters()) {
            paramStrVer[index++] = parameter instanceof String ? "'".concat(parameter.toString()).concat("'") : parameter.toString();
        }
        String target = this.getClass().getAnnotation(DatabaseFunction.class).function().length() > 0
                ? this.getClass().getAnnotation(DatabaseFunction.class).function()
                : this.getClass().getSimpleName();
        return target.concat("(" + String.join(",", paramStrVer) + ")");
    }

    @Override
    public String[] getColumn() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            SecurityException, InstantiationException, IllegalArgumentException {
        return (String[]) this.getType().getMethod("getColumn").invoke(this.getType().getConstructor().newInstance());
    }

    // IV- fetch
    public abstract Relation[] findAll(Connection connection);

    // V- validation
    private void checkTableAnnotation() throws MissingAnnotationException {
        if (!this.getClass().isAnnotationPresent(DatabaseFunction.class))
            throw new MissingAnnotationException();
    }

    @Override
    protected void checkClassValidity() throws MissingAnnotationException {
        this.checkTableAnnotation();
    }
}
