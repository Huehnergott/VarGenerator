/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
package vargenerator.util;

/**
 * Hilfsklasse zum Kapseln der Optionen für Erzeugung von Varianten.
 *
 * @author Huehnergott
 */
public class GeneratorOptions {

  private final boolean showVariants;
  private final boolean generateHtml;
  private final boolean generateXml;

  public GeneratorOptions(boolean showVariants, boolean generateHtml, boolean generateXml) {
    this.showVariants = showVariants;
    this.generateHtml = generateHtml;
    this.generateXml = generateXml;
  }

  /**
   * @return the showVariants
   */
  public boolean isShowVariants() {
    return showVariants;
  }

  /**
   * @return the generateHtml
   */
  public boolean isGenerateHtml() {
    return generateHtml;
  }

  /**
   * @return the generateXml
   */
  public boolean isGenerateXml() {
    return generateXml;
  }

}
