package com.alain.orm.database.object.view;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.alain.orm.database.connection.DatabaseConnection;
import com.alain.orm.database.object.DatabaseObject;
import com.alain.orm.database.object.relation.Relation;
import com.alain.orm.database.request.Request;
import com.alain.orm.enumeration.CRUDOperator;
import com.alain.orm.exception.MissingAnnotationException;
import com.alain.orm.database.object.relation.ModelField;

public abstract class View<T extends Relation<?>> extends DatabaseObject {

    private String viewName;
    private Class<T> type;

    // I- constructors
    public View(String viewName, Class<T> returnType) throws Exception {
        this.setViewName(viewName);
        this.setType(returnType);
    }

    // II- setter
    private void setViewName(String viewName) {
        this.viewName = viewName;
    }

    private void setType(Class<T> returnType) {
        this.type = returnType;
    }

    // III- getter
    public String getViewName() {
        return this.viewName;
    }

    public Class<T> getType() {
        return this.type;
    }
    
    @Override
    public String getTarget() {
        return this.getViewName().concat("()");
    }

    @Override
    public ModelField[] getColumn() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, IllegalArgumentException {
        return (ModelField[]) this.getType().getMethod("getColumn").invoke(this.getType().getConstructor().newInstance());
    }

    // IV- fetch
    @SuppressWarnings("unchecked")
    public T[] findAll(DatabaseConnection connection) throws Exception {
        Request request = new Request(CRUDOperator.SELECT, this, this.getType(), connection);
        ArrayList<Object> result = (ArrayList<Object>) request.executeRequest();
        T[] ans = (T[]) Array.newInstance(this.getType(), result.size());
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (T) this.getType().cast(result.get(i));
        }
        return ans;
    }

    @SuppressWarnings("unchecked")
    public T[] findAll(DatabaseConnection connection, String spec) throws Exception {
        Request request = new Request(CRUDOperator.SELECT, this, this.getType(), connection);
        request.setSpecification(spec);
        ArrayList<Object> result = (ArrayList<Object>) request.executeRequest();
        T[] ans = (T[]) Array.newInstance(this.getType(), result.size());
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (T) this.getType().cast(result.get(i));
        }
        return ans;
    }
    
    // V- validation
    @Override
    protected void checkClassValidity() throws MissingAnnotationException {
        return;
    }
}