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

  public List<PRfamilie> getFamilien() {
    return familie;
  }

  /**
   * Liefert die PR familie zur PR nummer
   *
   * @param prn PR Nummer
   * @return PR-Familie
   */
  public PRfamilie getFamilie(String prn) {
    for (PRfamilie f : familie) {
      int index = f.indexOf(prn);
      if (index >= 0) {
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
   * Kombiniert Teilegültigkeiten tg1 und tg2 zu einer Schnittmenge.
   *
   * @param mbt
   * @param tg1
   * @param tg2
   * @return
   */
  public String kombiniereTeGue(String tg1, String tg2) {
    List<PRfamilie> fam1 = findeFamilien(tg1);
    List<PRfamilie> fam2 = findeFamilien(tg2);

    if (fam1.isEmpty() && fam2.isEmpty()) {
      // nichts zu komnbinieren. Teilegültigkeit schränkt nicht ein
      return "";
    }

    if (fam1.isEmpty() && !fam2.isEmpty()) {
      return tg2;
    }
    if (fam2.isEmpty() && !fam1.isEmpty()) {
      return tg1;
    }

    // Normalform bilden für tg1
    for (PRfamilie f : fam2) {
      if (!fam1.contains(f)) {
        tg1 = tg1 + "+" + f.getMembers();
      }
    }
    // Normalform bilden für tg2
    for (PRfamilie f : fam1) {
      if (!fam2.contains(f)) {
        tg2 = tg2 + "+" + f.getMembers();
      }
    }

    // Ausmultiplizieren der Kombinationen
    List<PRkombination> kombi1 = findeKombination(tg1);
    List<PRkombination> kombi2 = findeKombination(tg2);

    List<PRkombination> schnitt = bildeSchnittmenge(kombi1, kombi2);

    if (schnitt.isEmpty()) {
      return null;
      // keine Überdeckung im Schnitt. Kombination ist leere Menge.
    }

    String tegue = "";
    for (PRkombination prk : schnitt) {
      tegue += (tegue.length() == 0) ? prk.toString() : "+" + prk.toString();
    }

    if (schnitt.size() > 1) {
      Log.write("WARNUNG ! Zusammenfassen fehlt noch.");
    }
    // TODO: zusammenfassen!
    return tegue;
  }

}
