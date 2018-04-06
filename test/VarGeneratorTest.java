/*
 * This software is licensed under the CC0 - Creative Commons Universal License. See
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode.txt 
 * Do whatever you want to do with this software.
 */
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vargenerator.VarGenerator;
import vargenerator.domain.MBT;
import vargenerator.domain.PRfamilie;
import vargenerator.domain.Stueckliste;
import vargenerator.domain.StueliEintrag;

/**
 * Testfälle. Baut Mini-Szenario und lässt den Variantengenerator darüber laufen.
 * @author Huehnergott
 */
public class VarGeneratorTest {

  public VarGeneratorTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  // TODO add test methods here.
  // The methods must be annotated with annotation @Test. For example:
  //
  @Test
  public void TestSzenario_1() {

    String aenderungsReihenfolge = "SOP MP1 MP2 MP3 MP4 EOP inf";
    MBT mbt = new MBT(
            new PRfamilie("L0x", "L0L L0R"),
            new PRfamilie("ICx", "IC1 IC2"), // ICAS Grundgerät
            new PRfamilie("C1x", "C1A C1B C1C")); // SWCL 1

    Stueckliste stueLi = new Stueckliste(aenderungsReihenfolge, mbt);
    stueLi.add(new StueliEintrag(0, "SOP", "MP2", "IC1", "ICAS Grundgerät"));
    stueLi.add(new StueliEintrag(0, "MP2", "", "IC1", "ICAS Grundgerät A2"));
    stueLi.add(new StueliEintrag(0, "MP3", "", "IC2", "ICAS Plus Grundgerät"));
    stueLi.add(new StueliEintrag(1, "SOP", "MP3", "IC1", "ICAS Basissoftware"));
    stueLi.add(new StueliEintrag(1, "MP3", "MP4", "IC1", "ICAS Basissoftware v2"));
    stueLi.add(new StueliEintrag(1, "MP4", "", "IC1", "ICAS Basissoftware v3"));
    stueLi.add(new StueliEintrag(1, "MP3", "", "IC1/IC2", "ICAS Plus Basissoftware"));
    stueLi.add(new StueliEintrag(2, "SOP", "MP1", "IC1", "Scheibenwischer KI"));
    stueLi.add(new StueliEintrag(2, "MP1", "MP3", "IC1", "Scheibenwischer KI fix"));
    stueLi.add(new StueliEintrag(2, "MP3", "", "IC1/IC2", "Scheibenwischer KI v3"));
    stueLi.add(new StueliEintrag(2, "MP4", "", "IC2", "Scheibenwischer KI plus"));

    VarGenerator vg = new VarGenerator(mbt, stueLi);

    vg.run();
  }
}
