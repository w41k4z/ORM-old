package com.alain.orm.database.object.relation;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;

import com.alain.orm.annotation.Column;
import com.alain.orm.annotation.Table;
import com.alain.orm.annotation.PrimaryKey;
import com.alain.orm.database.connection.DatabaseConnection;
import com.alain.orm.database.connection.PostgresConnection;
import com.alain.orm.database.object.DatabaseObject;
import com.alain.orm.database.object.sequence.Sequence;
import com.alain.orm.enumeration.CRUDOperator;
import com.alain.orm.exception.InvalidColumnCountException;
import com.alain.orm.exception.MissingAnnotationException;
import com.alain.orm.exception.MissingSetterException;
import com.alain.orm.exception.PrimaryKeyCountException;
import com.alain.orm.database.request.Request;
import com.alain.orm.utilities.Treatment;

public class Relation<T> extends DatabaseObject {

    // I- constructor
    public Relation() throws Exception {
        super();
    }

    // II- setters
    private void setPrimaryKey(String pk) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        if(this.hasPrimaryKey()) this.getPrimaryKeySetter().invoke(this, pk);
    }

    // III- getters
    //// primaryKey
    private String getPrimaryKeyPrefix() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return field.getAnnotation(PrimaryKey.class).prefix();
            }
        }
        return null;
    }

    private int getPrimaryKeyLength() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return field.getAnnotation(PrimaryKey.class).length();
            }
        }
        return 0;
    }

    private String getPrimaryKeySequence() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return field.getAnnotation(PrimaryKey.class).sequence();
            }
        }
        return null;
    }

    private Method getPrimaryKeySetter() throws NoSuchMethodException, SecurityException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return this.getClass().getDeclaredMethod(Treatment.toCamelCase("set", field.getName()), String.class);
            }
        }
        return null;
    }

    public ModelField getPrimaryKeyField() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                String name = field.getAnnotation(PrimaryKey.class).column().name().length() > 0
                        ? field.getAnnotation(PrimaryKey.class).column().name()
                        : field.getName();
                String originalName = field.getName();
                return new ModelField(name, originalName);
            }
        }
        return null;
    }

    public String getPrimaryKey() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                Object pk = this.getClass().getDeclaredMethod(Treatment.toCamelCase("get", field.getName()))
                        .invoke(this);
                return pk == null ? null : pk.toString();
            }
        }
        return null;
    }

    //// column
    private int getColumnCount() {
        return this.getClass().getAnnotation(Table.class).columnCount();
    }

    @Override
    public ModelField[] getColumn() {
        ModelField[] columns = new ModelField[this.getColumnCount()];
        int index = 0;
        if(this.hasPrimaryKey()) columns[index++] = this.getPrimaryKeyField();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                String name = field.getAnnotation(Column.class).name().length() > 0
                        ? field.getAnnotation(Column.class).name()
                        : field.getName();
                String originalName = field.getName();
                columns[index++] = new ModelField(name, originalName);
            }
        }
        return columns;
    }

    //// target name
    @Override
    public String getTarget() {
        return this.getClass().getAnnotation(Table.class).name().length() > 0
                ? this.getClass().getAnnotation(Table.class).name()
                : this.getClass().getSimpleName();
    }

    // IV- CRUD
    public int create(DatabaseConnection connection) throws Exception {
        this.setPrimaryKey(this.createPrimaryKey(connection));
        Request request = new Request(CRUDOperator.INSERT, this, this.getClass(), connection);
        return Integer.parseInt(request.executeRequest().toString());
    }

    @SuppressWarnings("unchecked")
    public T[] findAll(DatabaseConnection connection) throws Exception {
        Request request = new Request(CRUDOperator.SELECT, this, this.getClass(), connection);
        ArrayList<Object> result = (ArrayList<Object>) request.executeRequest();
        T[] ans = (T[]) Array.newInstance(this.getClass(), result.size());
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (T) Relation.class.cast(result.get(i));
        }
        return ans;
    }

    @SuppressWarnings("unchecked")
    public T[] findAll(DatabaseConnection connection, String spec) throws Exception {
        Request request = new Request(CRUDOperator.SELECT, this, this.getClass(), connection);
        request.setSpecification(spec);
        ArrayList<Object> result = (ArrayList<Object>) request.executeRequest();
        T[] ans = (T[]) Array.newInstance(this.getClass(), result.size());
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (T) Relation.class.cast(result.get(i));
        }
        return ans;
    }

    public int update(DatabaseConnection connection) throws Exception {
        Request request = new Request(CRUDOperator.UPDATE, this, this.getClass(), connection);
        request.setSpecification("WHERE " + this.getPrimaryKeyField().getName() + " = '" + this.getPrimaryKey() + "'");
        return Integer.parseInt(request.executeRequest().toString());
    }

    public int update(DatabaseConnection connection, String spec) throws Exception {
        Request request = new Request(CRUDOperator.UPDATE, this, this.getClass(), connection);
        request.setSpecification(spec);
        return Integer.parseInt(request.executeRequest().toString());
    }

    public int delete(DatabaseConnection connection) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        request.setSpecification("WHERE " + this.getPrimaryKeyField().getName() + " = '" + this.getPrimaryKey() + "'");
        return Integer.parseInt(request.executeRequest().toString());
    }

    public int delete(DatabaseConnection connection, String spec) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        request.setSpecification(spec);
        return Integer.parseInt(request.executeRequest().toString());
    }

    // V- method
    private String createPrimaryKey(DatabaseConnection connection) throws Exception {
        if (!this.hasCustomizedPrimaryKey()) // case for mysql auto_increment
            return connection instanceof PostgresConnection ? "DEFAULT" : "NULL";   // or for postgres serial
        String primaryKey = this.getPrimaryKeyPrefix();
        int sequence = new Sequence(this.getPrimaryKeySequence()).get(connection);
        int limit = this.getPrimaryKeyLength() -
                this.getPrimaryKeyPrefix().length() -
                String.valueOf(sequence).length();
        for (int i = 0; i < limit; i++) {
            primaryKey = primaryKey.concat("0");
        }
        return primaryKey.concat(String.valueOf(sequence));
    }

    private boolean hasPrimaryKey() {
        return this.getPrimaryKeyField() == null ? false : true;
    }

    private boolean hasCustomizedPrimaryKey() {
        return this.getPrimaryKeyPrefix().length() > 0;
    }

    // VII- validation
    private void checkTableAnnotation() throws MissingAnnotationException {
        if (!this.getClass().isAnnotationPresent(Table.class))
            throw new MissingAnnotationException();
    }

    private void checkColumnValidity() throws InvalidColumnCountException {
        int count = 0;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(PrimaryKey.class)) {
                count++;
            }
        }
        if (this.getColumnCount() != count)
            throw new InvalidColumnCountException();
    }

    private void checkSetterValidity() throws MissingSetterException, NoSuchFieldException, SecurityException {
        for (ModelField field : this.getColumn()) {
            try {
                this.getClass().getDeclaredMethod(Treatment.toCamelCase("set", field.getOriginalName()),
                        String.class);
            } catch (NoSuchMethodException e) {
                throw new MissingSetterException();
            } catch (SecurityException e) {
                e.printStackTrace();
            }   
        }
    }

    private void checkPrimaryKeyValidity() throws PrimaryKeyCountException {
        int count = 0;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                count++;
            }
        }
        if (count > 1)
            throw new PrimaryKeyCountException();
    }

    protected void checkClassValidity()
            throws MissingAnnotationException, InvalidColumnCountException, PrimaryKeyCountException,
            MissingSetterException, NoSuchFieldException, SecurityException {
        this.checkTableAnnotation();
        this.checkColumnValidity();
        this.checkSetterValidity();
        this.checkPrimaryKeyValidity();
    }
}
