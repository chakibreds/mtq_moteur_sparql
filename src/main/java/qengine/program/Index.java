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

    public ArrayList<Integer> get(int first, int second){
        ArrayList<Integer> listOfDicho =  new ArrayList<Integer>();
        listOfDicho = this.dicho(first,second);
        if(listOfDicho.isEmpty()){
            return listOfDicho;
        }
        int iDebut = listOfDicho.get(0); 
        int iFin = listOfDicho.get(1);
        ArrayList<Integer> listOfResult =  new ArrayList<Integer>();

        for(int i = iDebut; i <= iFin ; i++){
            listOfResult.add(this.indexMat.get(i)[2]);
        }
        return listOfResult;
    }

    public ArrayList<Integer> dicho(int first, int second){
        ArrayList<Integer> listOfFirst =  new ArrayList<Integer>();
        ArrayList<Integer> listOfsecond =  new ArrayList<Integer>();
        listOfFirst = dichoByColonne(0, this.indexMat.size()-1, first, 0);
        if(listOfFirst.isEmpty()){
            return listOfFirst;
        }
        listOfsecond = dichoByColonne(listOfFirst.get(0), listOfsecond.get(1), first, 1);

        return listOfsecond;
    }

    public ArrayList<Integer> dichoByColonne(int deb, int fin, int val, int col){
        ArrayList<Integer> IDebutFin = new ArrayList<Integer>();
        boolean trouve = false;
        int Idebut = 0;
        int Ifin = 0;
        int med = -1;
        int debT = deb; 
        int finT = fin;
        // recherche de la première occurence de Val
        while (!trouve &&  debT < finT){
            med = (debT+finT)/2;
            if (this.indexMat.get(med)[col] == val ){
                if ((med-1) > debT){
                    if (this.indexMat.get(med-1)[col] < val){
                        Idebut = med; 
                        trouve = true;
                    }
                    else {
                        finT = med-1;
                    }
                }
                else {
                    Idebut = debT ; 
                    trouve = true;
                }
            }
            else {   
                if(this.indexMat.get(med)[col] > val){
                    finT = med-1;
                }
                else {
                    debT = med+1;
                }
            }
        }
        if (!trouve){
            return IDebutFin;
        }
        trouve = false;
        debT = Idebut;
        finT = fin;
        IDebutFin.add(Idebut);

        // recherche de la dernière occurence de Val
        while (!trouve &&  debT < finT){
            med = (debT+finT)/2;
            if (this.indexMat.get(med)[col] == val ){
                if ((med+1) < finT){
                    if (this.indexMat.get(med+1)[col] > val){
                        Ifin = med; 
                        trouve = true;
                    }
                    else {
                        debT = med+1;
                    }
                }
                else {
                    Ifin = finT ; 
                    trouve = true;
                }
            }
            else {   
                if(this.indexMat.get(med)[col] > val){
                    finT = med-1;
                }
                else {
                    debT = med+1;
                }
            }
        }
        IDebutFin.add(Ifin);
        return IDebutFin;
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
        String str =  "";
        for(int[] e : this.indexMat)
        {
            str+= "[" + e[0] + ", " + e[1] + ", " + e[2] + "]\n"; 
        }
        return str;
    } 

    
}
