package qengine.program;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Index {
    private String order;

    private HashMap<Integer, HashMap<Integer, SortedSet<Integer>>> indexMat = new HashMap<Integer, HashMap<Integer, SortedSet<Integer>>>();
    public Index(String order){
        if(order.length() == 3)
            this.order = order.toLowerCase();
        else 
            System.err.println("format de l'ordre incorrect");
        
    }

    public String getOrder() {
        return order;
    }

    public int getPositionOfSubject() {
        return this.order.indexOf('s');
    }

    public int getPositionOfObject() {
        return this.order.indexOf('o');
    }

    public int getPositionOfPredicate() {
        return this.order.indexOf('p');
    }

    public SortedSet<Integer> get(int first, int second) {
        if (this.indexMat.get(first) != null && this.indexMat.get(first).get(second) != null)
            return new TreeSet<Integer>(this.indexMat.get(first).get(second));
        else
            return new TreeSet<Integer>();
    }

    public void add(int s, int p, int o) {
        int[] tuple = new int[3];
        tuple[this.getPositionOfSubject()] = s;
        tuple[this.getPositionOfPredicate()] = p;
        tuple[this.getPositionOfObject()] = o;

        if (this.indexMat.get(tuple[0]) != null) {
            if (this.indexMat.get(tuple[0]).get(tuple[1]) != null) {
                this.indexMat.get(tuple[0]).get(tuple[1]).add(tuple[2]);
            } else {
                SortedSet<Integer> list = new TreeSet<Integer>();
                list.add(tuple[2]);
                this.indexMat.get(tuple[0]).put(tuple[1], list);
            }
        } else {
            SortedSet<Integer> list = new TreeSet<Integer>();
            list.add(tuple[2]);

            HashMap<Integer, SortedSet<Integer>> map = new HashMap<Integer, SortedSet<Integer>>();
            map.put(tuple[1], list);
            this.indexMat.put(tuple[0],map);
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (Map.Entry<Integer, HashMap<Integer, SortedSet<Integer>>> entry1 : this.indexMat.entrySet()) {
            for (Map.Entry<Integer, SortedSet<Integer>> entry2 : entry1.getValue().entrySet() ) {
                for (Integer value : entry2.getValue()) 
                    str += "[" + entry1.getKey() + ", " + entry2.getKey() + ", " + value.intValue() + "]\n";
            }
        }
        return str;
    }

}
