package range;

import sheet.api.Layout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RangesManager implements Serializable {

    private final Map<String,Range> name2Range=new HashMap<>();;
    private final Layout layout;
    private  Map<String,Integer> rangesInUse =new HashMap();;

    public RangesManager(Layout layout ){
        this.layout = layout;
    }

    public Range getRange(String name){
        if(!name2Range.containsKey(name)){
            throw new RuntimeException("No such range "+name);
        }
        return name2Range.get(name);
    }

    public  void createRange(String name, String from, String to){
        if(name2Range.containsKey(name)){
            throw new RuntimeException("Range "+name+" already exists");
        }

        Boundaries boundaries= BoundariesFactory.createBoundaries(from, to ,layout);
        Range range = new Range(name, boundaries);
        name2Range.put(name, range);
    }

    public  void deleteRange(String name){
        if(name2Range.containsKey(name)== false) {
            //fix thee measage to be more good
            throw new RuntimeException("Cannot delete range '" + name + "' because no such range exists.");
        }
        Integer count = rangesInUse.getOrDefault(name, 0);
        if (count == 0) {
            rangesInUse.remove(name);
            name2Range.remove(name);
        } else {
            throw new RuntimeException("Cannot delete range '" + name + "' because it is still in use by " + count + " functions.");
        }
    }

    public  void addNewRangeInUse(String name){
        rangesInUse.put(name, rangesInUse.getOrDefault(name, 0) + 1);
    }

    public void resetAllRangeInUse(){
        rangesInUse.clear();
    }



    public Map<String,Range> getAllRanges(){
        return name2Range;
    }


}
