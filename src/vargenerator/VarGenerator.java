/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import vargenerator.Parser.ParserResult;
import vargenerator.domain.MBT;
import vargenerator.domain.Stueckliste;
import vargenerator.domain.StueliEintrag;
import vargenerator.domain.Variante;
import vargenerator.util.Log;

/**
 * Hauptklasse für Variantengenerator. Start der Generierung mit run().
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

  /**
   * Generierung starten. Parameter werden im Konstruktor übergeben.
   */
  public void run() {
    Log.write("VARIANTEN-GENERATOR start");

    int startZS = 0;
    int endZS = stueLi.zeitscheibeMax();

    // iteriere Zeitscheiben
    for (int i = startZS; i < endZS - 1; i++) {
      Collection<Variante> variantenImZeitraum = rechneVarianten(i);
      Log.write("Anzahl Varianten " + variantenImZeitraum.size());
    }
  }

  /**
   * Berechnet alle Varianten für den Zeitraum start, start+1
   *
   * @param start Zeitraum
   * @return liste aller gültigen Varianten im Zeitraum
   */
  private Collection<Variante> rechneVarianten(int start) {
    int end = start + 1;
    Log.write("Rechne Varianten für Zeitraum " + stueLi.getEinsatzEntfallName(start) + " bis " + stueLi.getEinsatzEntfallName(end));

    // Schritt 1: alles ausfiltern was nicht im Zeitraum liegt
    List<StueliEintrag> imZeitraum = new ArrayList<>();
    for (StueliEintrag eintrag : stueLi.getEintrag()) {
      if ((eintrag.getEinsatzZeitscheibe() <= start) && (eintrag.getEntfallZeitscheibe() >= end)) {
        imZeitraum.add(eintrag);
      }
    }

    HashMap<Integer, Variante> varianten = new HashMap<>();
    Variante leer = new Variante(this.mbt);
    varianten.put(leer.hashCode(), leer); // leere Variante

    // iteriere Knoten
    HashMap<Integer, Variante> variantenSpeicher = new HashMap<>();
    for (int aktuellerKnoten = stueLi.getKnotenMin(); aktuellerKnoten <= stueLi.getKnotenMax(); aktuellerKnoten++) {
      Log.write("Rechne Knoten " + aktuellerKnoten + "/" + stueLi.getKnotenMax());
      // iteriere Einträge
      for (StueliEintrag kandidat : imZeitraum) {
        if (kandidat.getKnotenNr() == aktuellerKnoten) {
          // iteriere alle vorhandenen varianten
          for (Variante vorhandene : varianten.values()) {
            Variante kombinierte = vorhandene.kombiniereMit(kandidat);
            if (kombinierte != null) {
              variantenSpeicher.put(kombinierte.hashCode(), kombinierte);
            }
          }
        }
      }
      if (variantenSpeicher.isEmpty()) {
        Log.write("Keine Auskombinierten für  Kombinationen in Knoten Nr " + aktuellerKnoten);
      } else {
        varianten.putAll(variantenSpeicher);
        Log.write("Anzahl Varianten ist " + varianten.size());
        variantenSpeicher = new HashMap<>();
      }
    }
    return varianten.values();
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      Log.write("Kein Filename angegeben. Bitte Programm starten mit: VarGenerator filename");
      return;
    }
    String fileName = args[0];
    try {
      // Szenario laden
      FileReader fr = new FileReader(fileName);
      ParserResult result = new Parser().parse(fr);

      // MBT und Stuückliste aufbauen
      VarGenerator vg = new VarGenerator(result.mbt, result.StueLi);

      // laufen lassen
      vg.run();
    } catch (FileNotFoundException ex) {
      Log.write("Die angegebene Datei '" + fileName + "'wurde nicht gefunden.");
    }

  }
}
