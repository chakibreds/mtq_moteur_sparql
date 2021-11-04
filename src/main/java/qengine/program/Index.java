package qengine.program;

import java.util.ArrayList;
import java.util.List;

public class Index {
    private String order ;
    private List<int[]> indexMat = new ArrayList<int[]>();

    public Index(String order){
        if(order.length() == 3)
            this.order = order.toLowerCase();
        else 
            System.err.println("format de l'ordre incorrect");
        
    }

    public String getOrder() {
        return order;
    }

    public int getPositionOfSubject(){
        return this.order.indexOf('s');
    }
    public int getPositionOfObject(){
        return this.order.indexOf('o');
    }
    public int getPositionOfPredicate(){
        return this.order.indexOf('p');
    }



    public void add(int s, int p, int o){
        int[] tuple = new int[3];
        tuple[this.getPositionOfSubject()] = s;
        tuple[this.getPositionOfPredicate()] = p;
        tuple[this.getPositionOfObject()] = o;
        int i = 0;
        while(i < this.indexMat.size() && tuple[0] > this.indexMat.get(i)[0])
            i++;
        while(i< this.indexMat.size() && tuple[0] == this.indexMat.get(i)[0] && tuple[1] > this.indexMat.get(i)[1])
            i++;
        while(i< this.indexMat.size() &&  tuple[0] == this.indexMat.get(i)[0] && tuple[1] == this.indexMat.get(i)[1] && tuple[2] > this.indexMat.get(i)[2])
            i++;
        
        this.indexMat.add(i,tuple);

    }
    @Override
    public String toString(){
        String str =  this.order + "\n";
        for(int[] e : this.indexMat)
        {
            str+= "[" + e[0] + ", " + e[1] + ", " + e[2] + "]\n"; 
        }
        return str;
    } 

    
}
