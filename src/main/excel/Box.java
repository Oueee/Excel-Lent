package excel;


import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;


public class Box {

  public List<TreeMap<String, Integer>> l = new ArrayList<TreeMap<String, Integer>>(3);

	public int nCds = 0;
	public int nCdsNot = 0;
  public int nbNucleotides = 0;

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

  public Box() {};
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
