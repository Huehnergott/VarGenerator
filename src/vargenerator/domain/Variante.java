/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Beschreibt eine Variante, d.h. eine Kombination aus Stuecklisteneinträgen die
 * miteinander kombiniert werden dürfen.
 *
 * @author Huehnergott
 */
public class Variante {

  List<StueliEintrag> element = new ArrayList<>();
  String teGue = "";
  private MBT mbt;

  private Variante(Variante copyFrom) {
    element.addAll(copyFrom.element);
    this.mbt = copyFrom.mbt;
  }

  public Variante(MBT mbt) {
    // leere Variante ohne Elemente
    this.mbt = mbt;
  }

  /**
   * Kombiniert den StüLi-Eintrag "neu" mit der bestehenden Variante.
   *
   * @param neu Eintrag der einzukombinieren ist.
   * @return Variante mit dem Eintrag oder null falls ungültige Kombi.
   */
  public Variante kombiniereMit(StueliEintrag neu) {
    // iteriere alle Elemente in der aktuellen Kombination, vergleiche Knotennummer
    for (StueliEintrag eintrag : element) {
      if (eintrag.getKnotenNr() == neu.getKnotenNr()) {
        // element ist im gleichen Knoten, d.h. Variante wäre ungültig
        return null;
      }
    }

    String variantenTegue = mbt.kombiniereTeGue(this.teGue, neu.getTeGue());
    if (variantenTegue == null) {
      return null;
    }

    // wenn bis hierher kein Fehler, dann ist die Variante baubar
    Variante ret = new Variante(this); // diese Instanz clonen
    ret.element.add(neu); //neues Element dazu
    ret.teGue = variantenTegue;
    return ret;
  }

  @Override
  public String toString() {
    String hash = Integer.toHexString(hashCode());
    return "Variante " + hash + "{" + element + '}';
  }

  @Override
  public int hashCode() {
    int hash = 0;
    // wir bilden hier einfach die Summe der hsashwerte der einzelnen StüLi-Elemente.
    // Die Reihenfolge ist dann egal.
    hash = this.element.stream().map((e) -> e.hashCode()).reduce(hash, Integer::sum);
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
    final Variante other = (Variante) obj;
    return this.hashCode() == Objects.hashCode(other);
  }

  public String getName() {
    String hash = Integer.toHexString(hashCode());
    return hash;
  }

  public String getTegue() {
    return this.teGue;
  }

  public String getTeileListe() {
    String ret = "";
    for (StueliEintrag e : element) {
      ret += (("".equals(ret)) ? "" : ",") + e.getTeileNr();
    }
    return ret;
  }

}
