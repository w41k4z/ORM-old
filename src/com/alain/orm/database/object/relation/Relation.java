package com.alain.orm.database.object.relation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;

import com.alain.orm.annotation.Column;
import com.alain.orm.annotation.DatabaseTable;
import com.alain.orm.annotation.PrimaryKey;
import com.alain.orm.database.connection.Connection;
import com.alain.orm.database.object.DatabaseObject;
import com.alain.orm.enumeration.CRUDOperator;
import com.alain.orm.exception.InvalidColumnCountException;
import com.alain.orm.exception.MissingAnnotationException;
import com.alain.orm.exception.MissingSetterException;
import com.alain.orm.exception.PrimaryKeyCountException;
import com.alain.orm.database.request.Request;
import com.alain.orm.utilities.Treatment;

public class Relation extends DatabaseObject {

    // I- constructors
    public Relation() throws MissingAnnotationException, InvalidColumnCountException, PrimaryKeyCountException,
            MissingSetterException {
        this.checkClassValidity();
    }

    // II- setters
    private void setPrimaryKey(String pk) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        this.getPrimaryKeySetter().invoke(this, pk);
    }

    // III- getters
    //// primaryKey
    private String getPrimaryKeyPrefix() {
        return this.getClass().getAnnotation(PrimaryKey.class).prefix();
    }

    private int getPrimaryKeyLength() {
        return this.getClass().getAnnotation(PrimaryKey.class).length();
    }

    private Method getPrimaryKeySetter() throws NoSuchMethodException, SecurityException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return this.getClass().getDeclaredMethod(Treatment.toCamelCase("set", field.getName()), String.class);
            }
        }
        return null;
    }

    public String getPrimaryKeyField() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return field.getAnnotation(PrimaryKey.class).column().name();
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
        return this.getClass().getAnnotation(DatabaseTable.class).columnCount();
    }

    @Override
    public String[] getColumn() {
        String[] columns = new String[this.getColumnCount()];
        int index = 0;
        columns[index++] = this.getPrimaryKeyField();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                columns[index++] = field.getAnnotation(Column.class).name();
            }
        }
        return columns;
    }

    //// target name
    @Override
    public String getTarget() {
        return this.getClass().getAnnotation(DatabaseTable.class).name().length() > 0
                ? this.getClass().getAnnotation(DatabaseTable.class).name()
                : this.getClass().getSimpleName();
    }

    // IV- CRUD
    public int create(Connection connection) throws Exception {
        //if(this.hasCustomizedPrimaryKey())  this.setPrimaryKey(connection);
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        return Integer.parseInt(request.executeRequest().toString());
    }

    @SuppressWarnings("unchecked")
    public Relation[] findAll(Connection connection) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        ArrayList<Object> result = (ArrayList<Object>) request.executeRequest();
        Relation[] ans = new Relation[result.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = Relation.class.cast(result.get(i));
        }
        return ans;
    }

    @SuppressWarnings("unchecked")
    public Relation[] findAll(Connection connection, String spec) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        request.setSpecification(spec);
        ArrayList<Object> result = (ArrayList<Object>) request.executeRequest();
        Relation[] ans = new Relation[result.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = Relation.class.cast(result.get(i));
        }
        return ans;
    }

    public int update(Connection connection) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        request.setSpecification("WHERE " + this.getPrimaryKeyField() + " = '" + this.getPrimaryKey() + "'");
        return Integer.parseInt(request.executeRequest().toString());
    }

    public int update(Connection connection, String spec) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        request.setSpecification(spec);
        return Integer.parseInt(request.executeRequest().toString());
    }

    public int delete(Connection connection) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        request.setSpecification("WHERE " + this.getPrimaryKeyField() + " = '" + this.getPrimaryKey() + "'");
        return Integer.parseInt(request.executeRequest().toString());
    }

    public int delete(Connection connection, String spec) throws Exception {
        Request request = new Request(CRUDOperator.DELETE, this, this.getClass(), connection);
        request.setSpecification(spec);
        return Integer.parseInt(request.executeRequest().toString());
    }

    // VI- method
    private int getSequenceFromDB(Connection connection) throws Exception {
        boolean connectionWasMine = false;
        if (connection.getConnection() == null) {
            connectionWasMine = true;
            connection = connection.defaultConnection();
        }

        // the specified request up to the dbms used
        String request = connection.sequenceGetter(this.getSequenceGetter());

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(request);
        result.next();
        int seq = result.getInt(1);
        statement.close();
        if (connectionWasMine)
            connection.close();
        return seq;
    }

    private String createPrimaryKey(Connection connection) {
        if (!this.hasCustomizedPrimaryKey())    // case for mysql auto_increment
            return null;                        // or postgres serial
        String primaryKey = this.getPrimaryKeyPrefix();
        int sequence = this.getSequenceFromDB(connection);
        int limit = this.getPrimaryKeyLength() -
                this.getPrimaryKeyPrefix().length() -
                String.valueOf(sequence).length();
        for (int i = 0; i < limit; i++) {
            primaryKey = primaryKey.concat("0");
        }
        return primaryKey.concat(String.valueOf(sequence));
    }

    private boolean hasCustomizedPrimaryKey() {
        return this.getPrimaryKeyPrefix().length() > 0;
    }

    // VII- validation
    private void checkTableAnnotation() throws MissingAnnotationException {
        if (!this.getClass().isAnnotationPresent(DatabaseTable.class))
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

    private void checkSetterValidity() throws MissingSetterException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(PrimaryKey.class)) {
                try {
                    Method setter = this.getClass().getDeclaredMethod(Treatment.toCamelCase("set", field.getName()),
                            String.class);
                } catch (NoSuchMethodException e) {
                    throw new MissingSetterException();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
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

    private void checkClassValidity()
            throws MissingAnnotationException, InvalidColumnCountException, PrimaryKeyCountException,
            MissingSetterException {
        this.checkTableAnnotation();
        this.checkColumnValidity();
        this.checkSetterValidity();
        this.checkPrimaryKeyValidity();
    }
}
