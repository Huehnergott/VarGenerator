/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repräsentiert eine Menge von PR Nummern zusammengefasst zu einer Familie.
 * Enthält hauptsächlich Hilfsfunktionen.
 *
 * @author Huehnergott
 */
public class PRfamilie {

  List<String> prNummer = new ArrayList<>();
  String name;

  /**
   * Default ctor.
   *
   * @param name Name der Familie z.B "LEA"
   * @param prNummern String der Nummern durch SPACE getrennt, z.b. "L0L L0R"
   */
  public PRfamilie(String name, String prNummern) {
    String[] parts = prNummern.split(" ");
    prNummer.addAll(Arrays.asList(parts));
    this.name = name;
  }

  /**
   * Default ctor.
   *
   * @param name Name der Familie z.B "LEA"
   * @param prNummern pr Nummern als Liste
   */
  public PRfamilie(String name, List<String> prNummern) {
    prNummer.addAll(prNummern);
    this.name = name;
  }

  /**
   * @param prn PRNummer
   * @return Index der PRN innerhalb der Familie.
   */
  public int indexOf(String prn) {
    return prNummer.indexOf(prn);
  }

  /**
   * @param index Index innerhalb der Familie.
   * @return Die PRnummer zum Index.
   */
  public String prn(int index) {
    return prNummer.get(index);
  }

  /**
   * Liefert alle PR-Nummern der Familie in der Form PR1/PR2/.../PRn
   *
   * @return PR1/PR2/.../PRn
   */
  public String getMembers() {
    String ret = "";
    for (String prn : prNummer) {
      ret += (ret.length() == 0) ? prn : "/" + prn;
    }

    return ret;
  }

  @Override
  public String toString() {
    return name + ": " + getMembers() + "\n";
  }

  public String getName() {
    return this.name;
  }

}
