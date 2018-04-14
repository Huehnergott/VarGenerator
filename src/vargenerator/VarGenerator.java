/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vargenerator.Parser.ParserResult;
import vargenerator.domain.MBT;
import vargenerator.domain.Stueckliste;
import vargenerator.domain.StueliEintrag;
import vargenerator.domain.Variante;
import vargenerator.util.GeneratorOptions;
import vargenerator.util.Log;

/**
 * Hauptklasse für Variantengenerator. Start der Generierung mit run().
 *
 * @author Huehnergott
 */
public class VarGenerator {

  private final Stueckliste stueLi;
  private final MBT mbt;
  private final GeneratorOptions options;

  public VarGenerator(MBT mbt, Stueckliste stueLi, GeneratorOptions options) {
    this.mbt = mbt;
    this.stueLi = stueLi;
    this.options = options;
  }

  /**
   * Generierung starten. Szenario wird im Konstruktor übergeben.
   */
  public void run() throws IOException {
    Log.write("VARIANTEN-GENERATOR start");

    int startZS = 0;
    int endZS = stueLi.zeitscheibeMax();

    Map<Integer, Collection<Variante>> variantenMap = new HashMap<>();
    // iteriere Zeitscheiben
    for (int i = startZS; i < endZS - 1; i++) {
      Collection<Variante> variantenImZeitraum = rechneVarianten(i);
      variantenMap.put(i, variantenImZeitraum);
      Log.write("Anzahl Varianten " + variantenImZeitraum.size());
    }
    if (options.isGenerateHtml()) {
      Log.write("Generiere HTML ...");
      generiereHtmlAusgabe(variantenMap);
      Log.write("... fertig");
    }
  }

  private void generiereHtmlAusgabe(Map<Integer, Collection<Variante>> variantenMap) throws IOException {
    // Vorbereitung
    HashMap<Integer, Variante> variantenAlle = new HashMap<>();
    variantenMap.values().forEach((vars) -> {
      vars.forEach((v) -> {
        variantenAlle.put(v.hashCode(), v);
      });
    });

    FileWriter w = new FileWriter("output.html", false);
    StringBuilder sb = new StringBuilder();

    sb.append("<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"styles.css\"></head><body>");
    sb.append("<h1>Übersicht</h1>");
    sb.append("<h2>Zeiträume</h2>");
    sb.append("<table><tr><th>Index</th><th>Zeitraum</th><th>#Varianten</th></tr>");
    List<String> zeiten = stueLi.getEinsatzEntfallReihenfolge();
    for (int index = 0; index < stueLi.zeitscheibeMax() - 1; index++) {
      String start = stueLi.getEinsatzEntfallName(index);
      String ende = stueLi.getEinsatzEntfallName(index + 1);
      long size = (variantenMap.get(index) != null) ? variantenMap.get(index).size() : 0;
      sb.append("<tr><td>").append(index).append("</td><td>").append(start).append(" - ").append(ende).append("</td><td>").append(size).append("</td></tr>");
    }
    sb.append("<tr><td></td><td>").append(stueLi.getEinsatzEntfallName(0)).append(" - ").append(stueLi.getEinsatzEntfallName(stueLi.zeitscheibeMax() - 1)).append("</td><td>").append(variantenAlle.size()).append("</td></tr>");
    sb.append("</table>");

    sb.append("<h2>PR Familien</h2>");
    sb.append("<table>");
    sb.append("<tr><th>Familie</th><th>PR-Nummern</th></tr>");
    mbt.getFamilien().forEach((fam) -> {
      sb.append("<tr><td>").append(fam.getName()).append("</td><td>").append(fam.getMembers()).append("</td></tr>");
    });
    sb.append("</table>");

    sb.append("<h2>Knoten</h2>");
    sb.append("<table><tr><th>Knoten</th><th>TeilNr</th><th>Gültigkeit</th><th>Einsatz</th><th>Entfall</th></tr>");
    for (int i = stueLi.getKnotenMin(); i <= stueLi.getKnotenMax(); i++) {
      sb.append("<tr>");
      sb.append("<td colspan=\"4\">").append(i).append("</td>");
      sb.append("</tr>");
      for (StueliEintrag e : stueLi.getEintrag()) {
        if (i == e.getKnotenNr()) {
          sb.append("<tr><td></td><td>").append(e.getTeileNr()).append("</td><td>").append(e.getTeGue())
                  .append("</td><td>").append(e.getEinsatz()).append("</td><td>").append(e.getEntfall()).append("</td></tr>");
        }
      }
    }
    sb.append("</table>");

    sb.append("<h2>Teile im Zeitraum</h2>");
    sb.append("<table>");
    sb.append("<tr><th>Teil</th>");
    zeiten.forEach((zeit) -> {
      sb.append("<th>").append(zeit).append("</th>");
    });
    sb.append("</tr>");
    for (StueliEintrag e : stueLi.getEintrag()) {
      sb.append("<tr><td>").append(e.getTeileNr()).append("</td>");
      zeiten.forEach((zeit) -> {
        if ((zeiten.indexOf(zeit) >= e.getEinsatzZeitscheibe()) && (zeiten.indexOf(zeit) < e.getEntfallZeitscheibe())) {
          sb.append("<td>X</td>");
        } else {
          sb.append("<td></td>");
        }
      });
      sb.append("</tr>");
    }
    sb.append("</table>");

    sb.append("<h2>Varianten</h2>");
    sb.append("<table>");

    sb.append("<tr><th>Variante</th><th>Teilegültigkeit</th>");
    for (int zeitvgl = 0; zeitvgl < zeiten.size() - 1; zeitvgl++) {
      sb.append("<th>").append(stueLi.getEinsatzEntfallName(zeitvgl)).append(" bis ").append(stueLi.getEinsatzEntfallName(zeitvgl + 1)).append("</th>");
    }
    sb.append("<th>Teile</th>" + "</tr>");
    for (Variante v : variantenAlle.values()) {
      sb.append("<tr><td>").append(v.getName()).append("</td><td>").append(v.getTegue()).append("</td>");
      for (int zeitvgl = 0; zeitvgl < zeiten.size() - 1; zeitvgl++) {
        if (variantenMap.get(zeitvgl).contains(v)) {
          sb.append("<td>X</td>");
        } else {
          sb.append("<td></td>");
        }
      }
      sb.append("<td>").append(v.getTeileListe()).append("</td></tr>");

    }
    sb.append("</table>");
    sb.append("</body></html>");

    w.write(sb.toString());
    w.flush();

    w.close();
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
    leer = leer.kombiniereMit(stueLi.getEintrag(0));
    varianten.put(leer.hashCode(), leer); // leere Variante mit Grundknoten

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
        if (this.options.isShowVariants()) {
          for (Variante v : varianten.values()) {
            Log.write(v.toString());
          }
        }
        Log.write("Anzahl Varianten ist " + varianten.size());
        variantenSpeicher = new HashMap<>();
      }
    }
    return varianten.values();
  }

  /**
   * Wertet die Parameter der Kommandozeile aus und füttert mit dem angegebenen
   * Szenario den Generator. Startet den Lauf und entsprechende Auswertungen.
   *
   * @param args -v=Varianten auf Console, -h=Html ausgeben, -X=Xml ausgeben
   * (nur Varianten)
   */
  public static void main(String[] args) {

    if (args.length == 0) {
      Log.write("Kein Filename angegeben. Bitte Programm starten mit: VarGenerator filename");
      return;
    }
    boolean showVariants = false;
    boolean genXml = false;
    boolean genHtml = false;

    for (String arg : args) {
      if ("-v".equals(arg)) {
        showVariants = true;
      }
      if ("-x".equals(arg)) {
        genXml = true;
      }
      if ("-h".equals(arg)) {
        genHtml = true;
      }

    }
    GeneratorOptions options = new GeneratorOptions(showVariants, genHtml, genXml);

    String fileName = args[0];
    try {
      // Szenario laden
      FileReader fr = new FileReader(fileName);
      ParserResult result = new Parser().parse(fr);

      // MBT und Stuückliste aufbauen
      VarGenerator vg = new VarGenerator(result.mbt, result.StueLi, options);

      // laufen lassen
      vg.run();
    } catch (FileNotFoundException ex) {
      Log.write("Die angegebene Datei '" + fileName + "'wurde nicht gefunden.");
    } catch (IOException ex) {
      Log.write("Fehler bei Ausgabe: " + ex.toString());
    }

  }
}
