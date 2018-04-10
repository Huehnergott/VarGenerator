/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import vargenerator.util.Log;

/**
 * Repräsentiert einen Ausschnitt aus der Stückliste mit
 * Rahmenbedingungen MBT und Liste der Einsatz- und Entfalltermine =
 * Zeitscheiben.
 *
 * @author Huehnergott
 */
public class Stueckliste {

  private final List<StueliEintrag> eintraege = new ArrayList<>();
  private final MBT mbt;
  private final String aenderungsReihenfolgeText;
  private final List<String> aenderungsReihenfolge = new ArrayList<>();
  
  private int knotenMin = Integer.MAX_VALUE;
  private int knotenMax = 0;

  public List<StueliEintrag> add(StueliEintrag neu) {
    boolean gueltig = checkeEintrag(neu);
    if (gueltig) {
      knotenMin = Math.min(knotenMin, neu.getKnotenNr());
      knotenMax = Math.max(knotenMax, neu.getKnotenNr());
      this.eintraege.add(neu);
      return this.eintraege;
    }
    return null;
  }

  public StueliEintrag getEintrag(int index) {
    return this.eintraege.get(index);
  }

  /**
   * Default ctor.
   * @param Reihenfolge String mit Reihenfolge der Einsatzschlüssel getrennt durch Leerzeichen.
   * @param mbt PRNummern und -familien definiert im MBT
   */
  public Stueckliste(String Reihenfolge, MBT mbt) {
    aenderungsReihenfolgeText = Reihenfolge;
    this.mbt = mbt;
    init();
  }

  private void init() {
    Log.write("Initialisiere Stückliste");
    String[] parts = aenderungsReihenfolgeText.trim().split(" ");
    aenderungsReihenfolge.addAll(Arrays.asList(parts));
    Log.write("Änderungsreihenfolge: " + aenderungsReihenfolge.toString());
  }

  public int zeitscheibeMax() {
    return aenderungsReihenfolge.size();
  }

  private boolean checkeEintrag(StueliEintrag eintrag) {
    Log.write("checke neuen Eintrag");

    // checke Einsatz
    String einsatz = eintrag.getEinsatz();
    if (!aenderungsReihenfolge.contains(einsatz)) {
      Log.write("Einsatztermin *" + einsatz + "* unbekannt.");
      return false;
    } else {
      eintrag.setEinsatzZeitscheibe(aenderungsReihenfolge.indexOf(einsatz));
    }

    //checke Entfall
    String entfall = eintrag.getEntfall();
    if (entfall.length() == 0) {
      eintrag.setEntfallZeitscheibe(aenderungsReihenfolge.size());
    } else {
      if (!aenderungsReihenfolge.contains(entfall)) {
        Log.write("Entfalltermin *" + entfall + "* unbekannt.");
        return false;
      } else {
        eintrag.setEntfallZeitscheibe(aenderungsReihenfolge.indexOf(entfall));
      }
    }

    boolean tegueOK = mbt.checkeTegue(eintrag.getTeGue());
    if (!tegueOK) {
      return false;
    }
    Log.write("Eintrag " + eintrag.toString());
    return true;
  }

  public String getEinsatzEntfallName(int index) {
    return aenderungsReihenfolge.get(index);
  }

  public List<String> getEinsatzEntfallReihenfolge() {
    return aenderungsReihenfolge;
  }

  public int getKnotenMin() {
    return knotenMin;
  }

  public int getKnotenMax() {
    return knotenMax;
  }

  public List<StueliEintrag> getEintrag() {
    return eintraege;
  }
  
}
