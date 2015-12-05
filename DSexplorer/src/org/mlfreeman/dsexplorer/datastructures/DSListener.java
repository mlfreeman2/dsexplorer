package org.mlfreeman.dsexplorer.datastructures;

public interface DSListener
{
    
    public void addedField(Datastructure field, int fieldIndex);
    
    public void hasChanged();
    
    public void pointerChanged(boolean pointer);
    
    public void removedField(int fieldIndex);
    
    public void replacedField(Datastructure oldField, Datastructure newField, int fieldIndex);
    
}
