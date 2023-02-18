package orm.database.object.function;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import orm.database.connection.DatabaseConnection;
import orm.database.object.DatabaseObject;
import orm.database.object.relation.ModelField;
import orm.database.object.relation.Relation;
import orm.database.request.Request;
import orm.enumeration.CRUDOperator;
import orm.exception.MissingAnnotationException;

public class Function<T extends Relation<?>> extends DatabaseObject {
    
    private String functionName;
    private Object[] parameters;
    private Class<T> type;

    // I- constructors
    public Function(String functionName, Object[] parameters, Class<T> returnType) throws Exception {
        this.setFunctionName(functionName);
        this.setParameters(parameters);
        this.setType(returnType);
    }

    // II- setter
    private void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    private void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    private void setType(Class<T> returnType) {
        this.type = returnType;
    }

    // III- getter
    public Object[] getParameters() {
        return this.parameters;
    }

    public Class<T> getType() {
        return this.type;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    @Override
    public String getTarget() {
        String[] paramStrVer = new String[this.getParameters().length];
        int index = 0;
        for (Object parameter : this.getParameters()) {
            System.out.println(parameter.toString());
            paramStrVer[index++] = parameter instanceof String || parameter instanceof java.sql.Date ? "'".concat(parameter.toString()).concat("'") : parameter.toString();
        }
        return this.getFunctionName().concat("(" + String.join(",", paramStrVer) + ")");
    }

    @Override
    public ModelField[] getColumn() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            SecurityException, InstantiationException, IllegalArgumentException {
        Object target = this.getType().getConstructor().newInstance();
        return (ModelField[]) this.getType().getMethod("getColumn").invoke(target);
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
