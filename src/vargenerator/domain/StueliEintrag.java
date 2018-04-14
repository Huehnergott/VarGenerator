/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator.domain;

import java.util.Objects;

/**
 * Repräsentiert einen Eintrag in die Teileliste mit den benötigten
 * Eigenschaften für Variantenrechnung.
 *
 * @author Huehnergott
 */
public class StueliEintrag {

  private final int KnotenNr;
  private final String Einsatz;
  private final String Entfall;
  private final String TeGue;
  private final String TeileNr;
  private final String Bemerkung;

  private int einsatzZeitscheibe = 0;
  private int entfallZeitscheibe = Integer.MAX_VALUE;

  public StueliEintrag(int KnotenNr, String Einsatz, String Entfall, String TeGue, String TeileNr, String Bemerkung) {
    this.KnotenNr = KnotenNr;
    this.Einsatz = Einsatz;
    this.Entfall = Entfall;
    this.TeGue = TeGue;
    this.TeileNr = TeileNr;
    this.Bemerkung = Bemerkung;
  }

  public void setEinsatzZeitscheibe(int einsatzZeitscheibe) {
    this.einsatzZeitscheibe = einsatzZeitscheibe;
  }

  public void setEntfallZeitscheibe(int entfallZeitscheibe) {
    this.entfallZeitscheibe = entfallZeitscheibe;
  }

  public int getKnotenNr() {
    return KnotenNr;
  }

  public String getEinsatz() {
    return Einsatz;
  }

  public String getEntfall() {
    return Entfall;
  }

  public String getTeGue() {
    return TeGue;
  }

  public String getTeileNr() {
    return TeileNr;
  }

  public String getBemerkung() {
    return Bemerkung;
  }

  public int getEinsatzZeitscheibe() {
    return einsatzZeitscheibe;
  }

  public int getEntfallZeitscheibe() {
    return entfallZeitscheibe;
  }

  @Override
  public String toString() {
    return "Teil{" + "KNr " + KnotenNr + "," + TeileNr + ", von " + Einsatz + ", bis " + Entfall + ", TeGu =" + TeGue + "}";
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 43 * hash + this.KnotenNr;
    hash = 43 * hash + Objects.hashCode(this.TeileNr);
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
    final StueliEintrag other = (StueliEintrag) obj;
    if (this.KnotenNr != other.KnotenNr) {
      return false;
    }
    if (!Objects.equals(this.TeileNr, other.TeileNr)) {
      return false;
    }
    return true;
  }

}
