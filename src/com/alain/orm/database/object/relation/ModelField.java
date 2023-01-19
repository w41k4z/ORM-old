package com.alain.orm.database.object.relation;

public class ModelField {
    
    private String name;
    private String originalName;

    // I- constructor
    public ModelField(String name, String originalName) {
        this.setName(name);
        this.setOriginalName(originalName);
    }

    // II- setters
    public void setName(String name) {
        this.name = name;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    // III- getters
    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    
    
}
