package excel;


import java.util.Map;
import java.util.TreeMap;

public class Box {

  public Map<String, Integer>[] p = new TreeMap<String, Integer>[]();

	public int nCds = 0;
	public int nCdsNot = 0;
  public int nbNucleotides = 0;

  public Box(TreeMap<String,Integer>[] p,
             int noCdsTraitees,
           	 int noCdsNonTraitees,
             int nbNucleotides)
  {
    this.p = p;
    this.nCds = nCds;
    this.nCdsNot = nCdsNot;
    this.nbNucleotides = nbNucleotides;
  }

  public void add(Box b) {
    this.nCds += b.nCds;
    this.CdsNot += b.nCdsNot;
    this.nbNucleotides += b.nbNucleotides;
  }
}
