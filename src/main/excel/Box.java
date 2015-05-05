package excel;


import java.util.Map;
import java.util.TreeMap;

public class Box {

  public Map<String, Integer> p1 = new TreeMap<String, Integer>();
	public Map<String, Integer> p2 = new TreeMap<String, Integer>();
	public Map<String, Integer> p3 = new TreeMap<String, Integer>();

	public int nCds = 0;
	public int nCdsNot = 0;
  public int nbNucleotides = 0;

  public Box(TreeMap<String,Integer> p1,
             TreeMap<String,Integer> p2,
             TreeMap<String,Integer> p3,
             int noCdsTraitees,
           	 int noCdsNonTraitees,
             int nbNucleotides)
  {
    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
    this.nCds = nCds;
    this.nCdsNot = nCdsNot;
    this.nbNucleotides = nbNucleotides;
  }
}
