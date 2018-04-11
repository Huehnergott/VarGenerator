/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vargenerator.domain.MBT;
import vargenerator.domain.PRfamilie;
import vargenerator.domain.Stueckliste;
import vargenerator.domain.StueliEintrag;
import vargenerator.util.Log;

/**
 * Ein Parser zum Einlesen von PR-Definitionen und Stücklisten
 *
 * @author Huehnergott
 */
class Parser {

  private MBT bildeFamilien(ParserResult ret) {
    MBT mbt;
    List<PRfamilie> familien = new ArrayList<>();

    for (String famName : ret.prList.keySet()) {
      List<String> prNums = ret.prList.get(famName);
      PRfamilie neueFam = new PRfamilie(famName, prNums);
      familien.add(neueFam);
    }
    mbt = new MBT(familien);
    return mbt;
  }

  class ParserResult {

    public MBT mbt;
    public Stueckliste StueLi;
    public String zeiten = "";

    private final Map<String, List<String>> prList;
    private final Map<String, Integer> knotenMap;
    private final List<StueliEintrag> eintraege = new ArrayList<>();

    ParserResult() {
      this.knotenMap = new HashMap<>();
      this.prList = new HashMap<>();
    }
  }

  private final int MODE_FIND_KEYWORD = 0;
  private final int MODE_PR_DEFINITION = 1;
  private final int MODE_STUECKLISTE = 2;
  private final int MODE_ZEITSCHEIBE = 3;

  /**
   * Liest die Datei ein und erzeugt die benötigten Daten für ein Szenario.
   *
   * @param in filereader
   * @return ParserResult
   */
  ParserResult parse(Reader in) {

    ParserResult ret = new ParserResult();

    int mode = MODE_FIND_KEYWORD;

    try {
      BufferedReader r = new BufferedReader(in);
      while (true) {
        String line = r.readLine();
        if (line == null) {
          break;
        } else {
          line = line.trim();
        }

        // omit comments and empty lines
        if (line.startsWith("--") || line.length() == 0) {
          continue;
        }

        switch (mode) {
          case MODE_FIND_KEYWORD:
            if ("PR-DEFINITION".equals(line)) {
              mode = MODE_PR_DEFINITION;
              Log.write("Scanne nach PR Definitionen");
            }
            break;
          case MODE_PR_DEFINITION:
            if (!"ZEITSCHEIBEN".equals(line)) {
              parsePRNummer(line, ret);
            } else {
              mode = MODE_ZEITSCHEIBE;
            }
            break;
          case MODE_ZEITSCHEIBE: {
            if (!"STUECKLISTE".equals(line)) {
              parseZeitschluessel(line, ret);
            } else {
              mode = MODE_STUECKLISTE;
            }
            break;
          }
          case MODE_STUECKLISTE: {
            parseStueLiEintrag(line, ret);
          }
        }
      }
    } catch (IOException ex) {
      Log.write("Error reading file: " + ex.toString());
    }

    ret.mbt = bildeFamilien(ret);
    // Log.write(ret.mbt.toString());
    ret.zeiten = bildeZeitscheiben(ret);
    ret.StueLi = new Stueckliste(ret.zeiten, ret.mbt);
    for (StueliEintrag e : ret.eintraege) {
      ret.StueLi.add(e);
    }
    return ret;
  }

  /**
   * Liest eine PR-Nummer aus der Zeile und packt sie in das ErgebnisSet.
   *
   * @param line, Zeile, wobei erster String die PRN und zweiter String die
   * Familie ist.
   * @param ret
   */
  private void parsePRNummer(String line, ParserResult ret) {
    String[] parts = line.split(";");
    String PRfam = parts[1].trim();
    String PRnum = parts[0].trim();

    // PRN abhängig von der PRfamilie in richtige Liste packen
    Map<String, List<String>> prList = ret.prList;
    List<String> prns;
    if (prList.containsKey(PRfam)) {
      prns = prList.get(PRfam);
    } else {
      prns = new ArrayList<>();
      prList.put(PRfam, prns);
    }
    if (!prns.contains(PRnum)) {
      prns.add(PRnum);
    }
  }

  /**
   * Eintrag der Form
   * "Knoten;Teilegültigkeit;Einsatz;Entfall;Teilenummer;Kurztext;..." einlesen
   *
   * @param line
   * @param ret
   */
  private void parseStueLiEintrag(String line, ParserResult ret) {
    String[] parts = line.split(";");
    String Knoten = parts[0].trim();
    String Tegue = parts[1].trim();
    String Einsatz = parts[2].trim();
    String Entfall = parts[3].trim();
    String TeileNr = parts[4].trim() + "(" + parts[5].trim() + ")";

    int knotenNr = ret.knotenMap.size();
    if (ret.knotenMap.containsKey(Knoten)) {
      knotenNr = ret.knotenMap.get(Knoten);
    } else {
      ret.knotenMap.put(Knoten, knotenNr);
    }
    if (Tegue.startsWith("+")) {
      Tegue = Tegue.substring(1, Tegue.length());
    }
    StueliEintrag neu = new StueliEintrag(knotenNr, Einsatz, Entfall, Tegue, TeileNr);
    ret.eintraege.add(neu);
  }

  /**
   * Neue Zeit in Liste einfügen.
   *
   * @param line
   * @param ret
   */
  private void parseZeitschluessel(String line, ParserResult ret) {
    String zeit = line.trim();
    ret.zeiten += ("".equals(ret.zeiten) ? "" : " ") + zeit;
  }

  /**
   * Zeitscheiben aus den Stuecklisten-Einträgen (Einsatz/Entfall) bilden.
   *
   * @param ret
   * @return Terminschlüssel in sortierter Reihenfolge
   */
  private String bildeZeitscheiben(ParserResult ret) {
    // Zeitscheiben bilden aus Stuecklisteneintraegen
    String zeiten = ret.zeiten;
    for (StueliEintrag sle : ret.eintraege) {
      String einsatz = sle.getEinsatz();
      String entfall = sle.getEntfall();
      // checke einsatz
      if (!zeiten.contains(einsatz)) {
        zeiten += " " + einsatz;
        Log.write(zeiten);
      }
      // checke entfall
      if (entfall.length() > 0) {
        if (!zeiten.contains(entfall)) {
          zeiten += " " + entfall;
          Log.write(zeiten);
        }

        // checke Reihenfolge
        int einsatzPos = zeiten.indexOf(einsatz);
        int entfallPos = zeiten.indexOf(entfall);
        if (einsatzPos > entfallPos) {
          // ggf position tauschen
          zeiten = zeiten.replaceAll(einsatz, "swap");
          zeiten = zeiten.replaceAll(entfall, einsatz);
          zeiten = zeiten.replaceAll("swap", entfall);
          Log.write("swap " + einsatz + " " + entfall);
          Log.write(zeiten);
        }
      }
    }
    return zeiten;
  }
}
