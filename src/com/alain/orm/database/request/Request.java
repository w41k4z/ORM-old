package com.alain.orm.database.request;

import java.lang.reflect.Method;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import com.alain.orm.database.connection.Connection;
import com.alain.orm.database.object.DatabaseObject;
import com.alain.orm.database.object.relation.Relation;
import com.alain.orm.enumeration.CRUDOperator;
import com.alain.orm.exception.InvalidRequestException;
import com.alain.orm.utilities.Treatment;

public class Request {

    private CRUDOperator crudOperator;
    private DatabaseObject databaseObject;
    private Class<? extends DatabaseObject> type;
    private String specification = "";
    private Connection connection;

    // I- constructors
    public Request(CRUDOperator crudOperator, DatabaseObject target, Class<? extends DatabaseObject> returnType,
            Connection connection) {
        this.setCrudOperator(crudOperator);
        this.setdatabaseObject(target);
        this.setType(returnType);
        this.setConnection(connection);
    }

    // II- setters
    private void setCrudOperator(CRUDOperator crudOperator) {
        this.crudOperator = crudOperator;
    }

    private void setdatabaseObject(DatabaseObject target) {
        this.databaseObject = target;
    }

    private void setType(Class<? extends DatabaseObject> type) {
        this.type = type;
    }

    public void setSpecification(String spec) {
        this.specification = spec;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    // III- getters
    private CRUDOperator getCrudOperator() {
        return this.crudOperator;
    }

    private DatabaseObject getDatabaseObject() {
        return this.databaseObject;
    }

    private Class<? extends DatabaseObject> getType() {
        return this.type;
    }

    private String getSpecification() {
        return this.specification;
    }

    private Connection getConnection() {
        return this.connection;
    }

    // IV- methods
    //// request builder
    ////// INSERT request
    private String buildInsertRequest() throws Exception {
        Object returnType = this.getType().getConstructor().newInstance();
        String[] columnName = (String[]) returnType.getClass().getMethod("getColumn").invoke(returnType);
        String req = this.getCrudOperator() + " INTO ".concat(
                this.getDatabaseObject().getTarget().concat("(".concat(String.join(",", columnName).concat(") VALUES("))));

        for (int i = 0; i < columnName.length; i++) {
            Object data = this.getDatabaseObject().getClass()
                    .getMethod(Treatment.toCamelCase("get", columnName[i])).invoke(this.getDatabaseObject());
            String toInsert;
            switch (data.getClass().getSimpleName()) {
                case "String":
                    toInsert = data == null ? "NULL" : "'" + data.toString() + "'";
                    break;
                case "Date":
                    toInsert = data == null ? "NULL" : this.getConnection().dateFormat(data.toString());
                    break;
                case "Timestamp":
                    toInsert = data == null ? "NULL" : "TIMESTAMP '" + data.toString() + "'";
                    break;
                default:
                    toInsert = data == null ? "NULL" : data.toString();
                    break;
            }
            req = i == 0 ? req.concat(toInsert) : req.concat(", " + toInsert);
        }

        return req.concat(")");
    }

    ////// SELECT request
    private String buildSelectRequest() throws Exception { 
        switch(this.getDatabaseObject().getClass().getSimpleName()) {
            case "Function":
                return this.getConnection().functionGetter(
                this.getDatabaseObject()
                        .getTarget());

            default:
                Object returnType = this.getType().getConstructor().newInstance();
                String[] columnName = (String[]) returnType.getClass().getMethod("getColumn").invoke(returnType);
                return this.getCrudOperator() + " ".concat(String.join(",", columnName).concat(
                        " FROM ".concat(
                                this.getDatabaseObject().getTarget().concat(" " + this.getSpecification()))));
        }
    }

    ////// UPDATE Request
    private String buildUpdateRequest() throws Exception {
        String req = this.getCrudOperator() + " ".concat(this.getDatabaseObject().getTarget().concat(" SET "));
        Relation table = Relation.class.cast(this.getDatabaseObject());
        String[] columnName = this.getDatabaseObject().getColumn();
        for (int i = 0; i < columnName.length; i++) {
            String separator = i == columnName.length - 1 ? " " : ", ";
            Object data = this.getDatabaseObject().getClass()
                    .getMethod(Treatment.toCamelCase("get",
                            columnName[i]))
                    .invoke(this.getDatabaseObject());

            if (data == null || columnName[i].equals(table.getPrimaryKeyField()))
                continue;

            String toInsert;
            switch (data.getClass().getSimpleName()) {
                case "String":
                    toInsert = "'" + data.toString() + "'";
                    break;
                case "Date":
                    toInsert = this.getConnection().dateFormat(data.toString());
                    break;
                case "Timestamp":
                    toInsert = "TIMESTAMP '" + data.toString() + "'";
                    break;
                default:
                    toInsert = data.toString();
                    break;
            }
            req = req.concat(columnName[i] + "=" + toInsert + separator);
        }

        return req.concat(this.getSpecification());
    }

    ////// DELETE request
    private String buildDeleteRequest() {
        return this.getCrudOperator() + " FROM ".concat(this.getDatabaseObject().getTarget().concat(
                " " + this.getSpecification()));
    }

    private String buildRequest() throws Exception {
        String request;
        switch (this.getCrudOperator()) {
            case INSERT:
                request = this.buildInsertRequest();
                break;
            case SELECT:
                request = this.buildSelectRequest();
                break;
            case UPDATE:
                request = this.buildUpdateRequest();
                break;
            case DELETE:
                request = this.buildDeleteRequest();
                break;
            default:
                throw new InvalidRequestException();
        }
        return request;
    }

    //// request executor
    ////// Execute INSERT-UPDATE-DELETE request
    private Integer executeDMRequest(String req) throws SQLException {
        int rowAffected = 0;
        boolean CONNECTION_ALREADY_OPENED = true;
        try {
            if (this.getConnection().getConnection() == null) {
                CONNECTION_ALREADY_OPENED = false;
                this.setConnection(this.getConnection().defaultConnection());
                this.getConnection().setAutoCommit(false);
            }
            Statement statement = this.getConnection().createStatement();
            rowAffected = statement.executeUpdate(req);
            statement.close();
        } catch (SQLException exception) {
            this.getConnection().rollback();
            throw exception;
        }

        if (!CONNECTION_ALREADY_OPENED) {
            this.getConnection().commit();
            this.getConnection().close();
        }

        return rowAffected;
    }

    ////// Execute SELECT request
    private ArrayList<Object> executeDQRequest(String req) throws Exception {
        ArrayList<Object> results = new ArrayList<>();
        boolean CONNECTION_ALREADY_OPENED = true;
        if (this.getConnection().getConnection() == null) {
            CONNECTION_ALREADY_OPENED = false;
            this.setConnection(this.getConnection().defaultConnection());
            this.getConnection().setAutoCommit(false);
        }
        Statement statement = this.getConnection().createStatement();
        ResultSet result = statement.executeQuery(req);
        String[] columnName = this.getDatabaseObject().getColumn();
        while (result.next()) {
            Object newObject = this.getType().getConstructor().newInstance();
            for (int i = 0; i < columnName.length; i++) {
                Object data = result.getObject(columnName[i]);
                Method method = newObject
                        .getClass()
                        .getMethod(
                                Treatment.toCamelCase("set", columnName[i]),
                                String.class);
                method.invoke(
                        newObject,
                        data == null ? null : data.toString());
            }
            results.add(newObject);
        }

        if (!CONNECTION_ALREADY_OPENED) {
            this.getConnection().close();
        }

        return results;
    }

    public Object executeRequest() throws Exception {
        String request = this.buildRequest();
        return this.getCrudOperator() == CRUDOperator.SELECT ? this.executeDQRequest(request)
                : this.executeDMRequest(request);
    }
}