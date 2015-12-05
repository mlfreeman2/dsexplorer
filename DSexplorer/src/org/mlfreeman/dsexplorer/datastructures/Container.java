package org.mlfreeman.dsexplorer.datastructures;

import java.util.List;

public interface Container extends Datastructure
{
    public void addField(Datastructure field);
    
    public void addField(Datastructure field, int index);
    
    public List<Datastructure> getFields();
    
    public int getOffset(int fieldIndex);
    
    public boolean isPointer();
    
    public void removeField(int fieldIndex);
    
    public void replaceField(Datastructure oldField, Datastructure newField, int index);
    
    public void setPointer(boolean pointer);
    
}
