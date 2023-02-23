package orm.database.object.relation;

public class ModelField {
    
    private Class<?> classType;
    private String name;
    private String originalName;

    // I- constructor
    public ModelField(Class<?> classType, String name, String originalName) {
        this.setClassType(classType);
        this.setName(name);
        this.setOriginalName(originalName);
    }

    // II- setters
    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    // III- getters
    public Class<?> getClassType() {
        return this.classType;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    
    
}
