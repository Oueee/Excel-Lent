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
    this.nCds = nCds;
    this.nCdsNot = nCdsNot;
    this.nbNucleotides = nbNucleotides;
  }

  public Box() {
    l = new ArrayList<TreeMap<String, Integer>>();

    for(int i = 0; i < 3; i++)
      l.add(new TreeMap<String, Integer>());


  	nCds = 0;
  	nCdsNot = 0;
    nbNucleotides = 0;

  };
  public void add(Box b) {
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
}
