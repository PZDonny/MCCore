package net.donnypz.mccore.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class DatabaseUpdate {
    private final HashMap<String, Object> setValues = new HashMap<>();
    private final HashMap<String, NumberUpdate> incrementedValues = new HashMap<>();
    private final MongoCollection<Document> collection;
    private final Document filter;

    DatabaseUpdate(@NotNull MongoCollection<Document> collection, @NotNull Document filter){
        this.collection = collection;
        this.filter = filter;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    boolean isEmpty(){
        return setValues.isEmpty() && incrementedValues.isEmpty();
    }

    public DatabaseUpdate setValue(String fieldName, Object value){
        setValues.put(fieldName, value);
        return this;
    }

    public DatabaseUpdate incrementValue(String fieldName, int value){
        addIncrementedValue(fieldName, value, NumberUpdate.NumberType.INT);
        return this;
    }

    public DatabaseUpdate incrementValue(String fieldName, long value){
        addIncrementedValue(fieldName, value, NumberUpdate.NumberType.LONG);
        return this;
    }

    public DatabaseUpdate incrementValue(String fieldName, double value){
        addIncrementedValue(fieldName, value, NumberUpdate.NumberType.DOUBLE);
        return this;
    }

    public DatabaseUpdate incrementValue(String fieldName, float value){
        addIncrementedValue(fieldName, value, NumberUpdate.NumberType.FLOAT);
        return this;
    }

    private void addIncrementedValue(String fieldName, Number n, NumberUpdate.NumberType numberType){
        incrementedValues.put(fieldName, new NumberUpdate(n, numberType));
    }


    HashMap<String, Object> getSetValues(){
        return setValues;
    }

    HashMap<String, NumberUpdate> getIncrementedValues(){
        return incrementedValues;
    }

    Document getFilter(){
        return filter;
    }

    public void update(){
        MongoUtils.update(this);
    }

    /**
     * Send the modifications in this update to the database, and apply the changes to a given document.
     * This works the same as using {@link  #apply(Document)}
     * @param document the document to apply update to.
     */
    public void update(Document document){
        MongoUtils.update(this);
        apply(document);
    }

    /**
     * Apply updated data to a given document. Should be done for cached data
     * @param document
     */
    public void apply(Document document){
        for (Map.Entry<String, Object> entry : setValues.entrySet()){
            String field = entry.getKey();
            Object value = entry.getValue();
            Object[] trueObjects = getTrueObjects(document, field);

            Document trueDoc = (Document) trueObjects[0];
            String trueField = (String) trueObjects[1];
            trueDoc.put(trueField, value);
        }

        //Increment Value
        for (Map.Entry<String, NumberUpdate> entry : incrementedValues.entrySet()){
            String field = entry.getKey();
            NumberUpdate number = entry.getValue();
            Object[] trueObjects = getTrueObjects(document, field);
            Document trueDoc = (Document) trueObjects[0];
            String trueField = (String) trueObjects[1];
            switch(number.getNumberType()){
                case INT -> {
                    trueDoc.put(trueField, number.intValue()+trueDoc.getInteger(trueField));
                }
                case LONG -> {
                    trueDoc.put(trueField, number.longValue()+trueDoc.getLong(trueField));
                }
                case FLOAT -> {
                    trueDoc.put(trueField, number.floatValue()+trueDoc.getDouble(trueField));
                }
                case DOUBLE -> {
                    trueDoc.put(trueField, number.doubleValue()+trueDoc.getDouble(trueField));
                }
            }
        }
    }

    //Get possible nested documents and the true field based on the "field" paramater
    private Object[] getTrueObjects(Document document, String field){
        if (!field.contains(".")){
            return new Object[]{document, field};
        }
        Document trueDoc = document;
        String[] split = field.split("\\.");
        for (int i = 0; i < split.length-1; i++){
            if (trueDoc.get(split[i]) instanceof Document d) {
                trueDoc = d;
            }
            else {
                break;
            }
        }
        return new Object[]{trueDoc, split[split.length-1]};
    }
}
