/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator;

import java.util.ArrayList;
import java.util.List;
import vargenerator.domain.MBT;
import vargenerator.domain.Stueckliste;
import vargenerator.domain.StueliEintrag;
import vargenerator.domain.Variante;
import vargenerator.util.Log;

/**
 * Hauptklasse für Variantengenerator.
 * Start der Generierung mit run().
 * 
 * @author Huehnergott
 */
public class VarGenerator {

  private final Stueckliste stueLi;
  private final MBT mbt;

  public VarGenerator(MBT mbt, Stueckliste stueLi) {
    this.mbt = mbt;
    this.stueLi = stueLi;
  }

  public void run() {
    Log.write("VARIANTEN-GENERATOR start");

    int startZS = 0;
    int endZS = stueLi.zeitscheibeMax();

    // iteriere Zeitscheiben
    for (int i = startZS; i < endZS - 1; i++) {
      List<Variante> variantenImZeitraum = rechneVarianten(i);
      Log.write("Anzahl Varianten " + variantenImZeitraum.size());
      for (Variante v : variantenImZeitraum) {
        Log.write(v.toString());
      }
    }
  }

  /**
   * Berechnet alle Varianten für den Zeitraum start, start+1
   * @param start
   * @return liste aller gültigen Varianten im Zeitraum
   */
  private List<Variante> rechneVarianten(int start) {
    int end = start + 1;
    Log.write("Rechne Varianten für Zeitraum " + stueLi.getEinsatzEntfallName(start) + " bis " + stueLi.getEinsatzEntfallName(end));

    // Schritt 1: alles ausfiltern was nicht im Zeitraum liegt
    List<StueliEintrag> imZeitraum = new ArrayList<>();
    for (StueliEintrag eintrag : stueLi.getEintrag()) {
      if ((eintrag.getEinsatzZeitscheibe() <= start) && (eintrag.getEntfallZeitscheibe() >= end)) {
        imZeitraum.add(eintrag);
      }
    }

    List<Variante> varianten = new ArrayList<>();
    varianten.add(new Variante(this.mbt)); // leere Variante

    // iteriere Knoten
    List<Variante> variantenSpeicher = new ArrayList<>();
    for (int aktuellerKnoten = stueLi.getKnotenMin(); aktuellerKnoten <= stueLi.getKnotenMax(); aktuellerKnoten++) {
      // iteriere Einträge
      for (StueliEintrag kandidat : imZeitraum) {
        if (kandidat.getKnotenNr() == aktuellerKnoten) {
          // iteriere die varianten
          for (Variante v : varianten) {
            Variante kombinierte = v.kombiniereMit(kandidat);
            if (kombinierte != null) {
              variantenSpeicher.add(kombinierte);
            }
          }
        }
      }
      if (variantenSpeicher.isEmpty()) {
        Log.write("Keine gültigen Kombinationen in Knoten Nr " + aktuellerKnoten);
        return variantenSpeicher;
      }
      varianten = variantenSpeicher;
      variantenSpeicher = new ArrayList<>();
    }
    return varianten;
  }

}
