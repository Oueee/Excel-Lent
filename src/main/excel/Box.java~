package excel;


import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;


public class Box {

  public List<TreeMap<String, Integer>> l;

	public int nCds;
	public int nCdsNot;
    public int nbNucleotides;
    
  public Box(List<TreeMap<String, Integer>> l,
             int noCdsTraitees,
           	 int noCdsNonTraitees,
             int nbNucleotides)
  {
    this.l = l;
    this.nCds = noCdsTraitees;
    this.nCdsNot = noCdsNonTraitees;
    this.nbNucleotides = nbNucleotides;
  }

  public Box() {
    this.nbNucleotides = 0; //Means that the box is empty
  };
  
  public Box add(Box b) {

    if(this.nbNucleotides == 0) //If this box is empty, return the box to add
        return b;
    
    if(b != null) { //If the other box is not null, add it to this one
        this.nCds += b.nCds;
        this.nCdsNot += b.nCdsNot;
        this.nbNucleotides += b.nbNucleotides;
        TreeMap<String, Integer> t;
        TreeMap<String, Integer> a;

        for(int i = 0; i < l.size(); i++) {
          t = b.l.get(i);
          a = this.l.get(i);

          for(String key : a.keySet())
            a.put(key, a.get(key) + t.get(key));
        }
    }
    
    return this; //then return this object
  }
}
