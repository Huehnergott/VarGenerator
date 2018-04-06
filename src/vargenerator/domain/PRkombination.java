/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert eine Kombination von PR-Nummern (nur UND-Verknüpfung)
 * @author Huehnergott
 */
public class PRkombination {

  private MBT mbt;
  List<String> prns = new ArrayList<>();

  /**
   * Default ctor.
   * 
   * @param mbt 
   */
  PRkombination(MBT mbt) {  }

  /**
   * Erzeugt eine neue Kombination aus einer alten durch hinzufügen der PRN.
   *
   * @param copyFrom Ursprungskombi
   * @param prn zuzufügende PRnummer
   */
  PRkombination(PRkombination copyFrom, String prn) {
    this.mbt = copyFrom.mbt;
    this.prns = new ArrayList<>(copyFrom.prns);
    if (!prns.contains(prn)) {
      prns.add(prn);
    }
  }

  /**
   * Fügt weitere PR-Nummer dazu wenn sie nicht beriets in der Kombination ist.
   * @param prn neue PR-Nummer
   * @return neue Kombination falls PRN noch nicht drin war, andernfalls die ungeänderte Kombination
   */
  public PRkombination addPRN(String prn) {
    if (!prns.contains(prn)) {
      prns.add(prn);
    }
    return this;
  }

  @Override
  public int hashCode() {
    // Dieser Hash ist kein wirklicher Hash, weil wir auch Vertauschungen von PR-Nummern als gleichwertig ansehen wollen.
    // Beispiel: ABC = ACB = ... = BAC = CBA für PRnummern A,B,C
    int hash = 1;
    for (String prn : prns)
    {
      hash = hash * prn.hashCode();
    }
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PRkombination other = (PRkombination) obj;
    return this.hashCode() == other.hashCode();
  }

  @Override
  public String toString() {
    String ret = "";
    for (String prn : prns) {
      ret += (ret.length() == 0) ? prn : "+" + prn;
    }
    return ret;
  }
}
