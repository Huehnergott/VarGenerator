/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vargenerator.util.Log;

/**
 * Definiert Regeln unter Benutzung von PR-Nummern und PR-Familien. Kapselt
 * Methoden zum Arbeiten mit PRN, PRF und TeGue.
 *
 * @author Huehnergott
 */
public class MBT {

  /**
   * Checkt ob die angegebene TeGue gültig ist gegen den MBT.
   *
   * @param teGue
   * @return true/false
   */
  boolean checkeTegue(String teGue) {
    teGue = teGue.replace(" ", "");
    if (teGue.length() == 0) {
      return true;
    }

    String[] terms = teGue.split("\\+");
    for (String term : terms) {
      String[] prns = term.split("/");
      for (String prn : prns) {
        if (getFamilie(prn) == null) {
          Log.write("Unbekannte PR-Nummer '" + prn + "' in term '" + term + "' !");
          return false;
        }
      }
    }
    return true;
  }

  List<PRfamilie> familie = new ArrayList<>();

  public MBT(PRfamilie... familien) {
    familie.addAll(Arrays.asList(familien));
  }

  public MBT(List<PRfamilie> familien) {
    familie.addAll(familien);
  }

  public List<PRfamilie> getFamilien() {
    return familie;
  }

  Map<String, PRfamilie> prnCache = new HashMap<>();

  /**
   * Liefert die PR familie zur PR nummer
   *
   * @param prn PR Nummer
   * @return PR-Familie
   */
  public PRfamilie getFamilie(String prn) {
    if (prnCache.containsKey(prn)) {
      return prnCache.get(prn);
    }

    for (PRfamilie f : familie) {
      int index = f.indexOf(prn);
      if (index >= 0) {
        prnCache.put(prn, f);
        return f;
      }
    }
    return null;
  }

  /**
   * Gibt alle Familien zu den PRN aus prn zurück
   *
   * @param prn PR Nummern. Erlaubt sind alle Formen einer TeGue, also z.b.
   * 'L0L/L0R+1DX+2DY/2DZ'
   * @return Liste der Familien aus PRN
   */
  public List<PRfamilie> findeFamilien(String prn) {
    List<PRfamilie> ret = new ArrayList<>();

    if ("".equals(prn)) {
      return ret;
    }

    prn = prn.replace(" ", "");
    prn = prn.replace("+", " ");
    prn = prn.replace("/", " ");
    // PR Nummern sind jetzt mit Leerzeichen getrennt
    String[] parts = prn.split(" ");
    for (String part : parts) {
      PRfamilie fam = getFamilie(part);
      if (!ret.contains(fam)) {
        ret.add(fam);
      }
    }
    return ret;
  }

  /**
   * Entfernt die PR-Nummern aus dem Term die NICHT zu den Familien "exclude"
   * gehören.
   *
   * @param term TeGue Term
   * @param exclude Familien die zu entfernen sind
   *
   * @return bereinigte Tegue mit Termen die nicht in exclude zu finden sind
   */
  public String entferneFamilien(String term, List<PRfamilie> exclude) {
    String ret = "";

    if ("".equals(term)) {
      return term;
    }

    // PR Nummern sind jetzt mit Leerzeichen getrennt
    String[] groups = term.split("\\+");
    for (String group : groups) {
      String[] prns = group.split("/");
      if (prns.length > 0) {
        PRfamilie find = getFamilie(prns[0]);
        if (!exclude.contains(find)) {
          ret += (ret != "" ? "+" : "") + group;
        }
      }
    }
    return ret;
  }

  /**
   * Entfernt die PR-Nummern aus dem Term die zu den Familien "exclude" gehören.
   *
   * @param term TeGue Term
   * @param exclude Familien die NICHT zu behalten sind
   *
   * @return bereinigte Tegue
   */
  public String behalteFamilien(String term, List<PRfamilie> exclude) {
    String ret = "";

    if ("".equals(term)) {
      return term;
    }

    // PR Nummern sind jetzt mit Leerzeichen getrennt
    String[] groups = term.split("\\+");
    for (String group : groups) {
      String[] prns = group.split("/");
      if (prns.length > 0) {
        PRfamilie find = getFamilie(prns[0]);
        if (exclude.contains(find)) {
          ret += (ret != "" ? "+" : "") + group;
        }
      }
    }
    return ret;
  }

  /**
   * Bilde Liste aller Kombinationen von PR Nummern aus dem prn
   *
   * @param prTerm Term aus PR-Nummern
   * @return Liste aller Kombinationen
   */
  List<PRkombination> findeKombination(String prTerm) {
    prTerm = prTerm.replace(" ", "");
    String[] groups = prTerm.split("\\+"); // nach plus-zeichen trennen
    // jede Gruppe hat jetzt nur noch PR-Nummern aus einer familie

    // hier die Rekursion zum Ausmultiplizieren
    // aus Term "A+B/C+D/E" wird: (A) -> (AB, AC) -> (ABD, ABE, ACD, ACE) -> ret
    List<PRkombination> kombi = new ArrayList<>();
    kombi.add(new PRkombination(this));
    List<PRkombination> kombiNeu = new ArrayList<>();

    for (String group : groups) {
      String[] prFaktoren = group.split("/");
      for (String prFaktor : prFaktoren) {
        for (PRkombination alt : kombi) {
          PRkombination multipliziert = new PRkombination(alt, prFaktor);
          if (!kombiNeu.contains(multipliziert)) {
            kombiNeu.add(multipliziert);
          }
        }
      }
      kombi = kombiNeu;
      kombiNeu = new ArrayList<>();
    }

    return kombi;
  }

  /**
   * Liefert die Schnittmenge der Kombinationen zurück, also alle Kombinationen
   * die jeweils in 1 und 2 sind.
   *
   * @param kombi1 Liste mit Kombinationen von PR-Nummern (verknüpft durch UND)
   * @param kombi2 Liste mit Kombinationen von PR-Nummern (verknüpft durch UND)
   * @return Schnittmenge
   */
  List<PRkombination> bildeSchnittmenge(List<PRkombination> kombi1, List<PRkombination> kombi2) {
    List<PRkombination> ret = new ArrayList<>();
    for (PRkombination k1 : kombi1) {
      if (kombi2.contains(k1)) {
        ret.add(k1);
      }
    }
    return ret;
  }

  /**
   * Cache für Teilegültigkeit
   */
  Map<String, String> tegueCache = new HashMap<>();

  /**
   * Kombiniert Teilegültigkeiten tg1 und tg2 zu einer Schnittmenge.
   *
   * @param tg1
   * @param tg2
   * @return
   */
  public String kombiniereTeGue(String tg1, String tg2) {
    String ret = "";
    String u1 = tg1;
    String u2 = tg2;

    if (tegueCache.containsKey(u1 + u2)) {
      return tegueCache.get(u1 + u2);
    }

    // Sonderfälle zuerst:
    List<PRfamilie> fam1 = findeFamilien(tg1);
    List<PRfamilie> fam2 = findeFamilien(tg2);

    if (fam1.isEmpty() && fam2.isEmpty()) {
      // nichts zu komnbinieren. Teilegültigkeit schränkt nichts ein
      return "";
    }

    if (fam1.isEmpty() && !fam2.isEmpty()) {
      return tg2;
    }
    if (fam2.isEmpty() && !fam1.isEmpty()) {
      return tg1;
    }

    // Familien finden die sich schneiden:
    List<PRfamilie> famSchnitt = new ArrayList<>();
    for (PRfamilie f : fam1) {
      if (fam2.contains(f)) {
        famSchnitt.add(f);
      }
    }

    if (famSchnitt.isEmpty()) {
      // keine Überschneidung, d.h. das Ergebnis ist einfach die Summe der Terme
      return tg1 + "+" + tg2;
    }

    // Für jede PR-Familie nicht im Schnitt ist die PRN-Terme finden und zum Ergebnis tun
    ret += entferneFamilien(tg1, famSchnitt);
    ret += (!"".equals(ret) ? "+" : "") + entferneFamilien(tg2, famSchnitt);

    // Für jede PR-Familie im Schnitt die gemeinsamen PRN finden
    tg1 = behalteFamilien(tg1, famSchnitt);
    tg2 = behalteFamilien(tg2, famSchnitt);

    // AUSKOMBINIEREN für alle Schnitte:
    //Für jede PR-Familie im Schnitt die gemeinsamen PRN finden und als Term an die endgültige TeGue hängen
    for (PRfamilie f : famSchnitt) {
      String prnSchnitt = "";
      List<PRfamilie> aktuell = new ArrayList<>();
      aktuell.add(f);
      String t1 = behalteFamilien(tg1, aktuell);
      String t2 = behalteFamilien(tg2, aktuell);
      String[] parts = t1.split("/");
      for (String prn : parts) {
        if (t2.contains(prn)) {
          prnSchnitt = (!"".equals(prnSchnitt) ? "/" : "") + prn;
        }
      }
      if (prnSchnitt.length() > 0) {
        ret += (!"".equals(ret) ? "+" : "") + prnSchnitt;
        ret = ret.replace("++", "+");
      } else {
        // leerer Schnitt in der PR-Familie -> ungültige Kombination
        return null;
      }
    }

    tegueCache.put(u1 + u2, ret);
    tegueCache.put(u2 + u1, ret);
    return ret;
  }

  @Override
  public String toString() {
    return "PRfamilien=" + familie.toString();
  }

}
