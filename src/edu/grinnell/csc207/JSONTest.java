package edu.grinnell.csc207;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.Test;

public class JSONTest
{
  @Test
  public void parseConstanttest()
  {
    assertTrue((JSONUtils.parse("null") == null));
    assertTrue(((Boolean) JSONUtils.parse("true") == true));
    assertTrue(((Boolean) JSONUtils.parse("false") == false));
  }// parseConstanttest()

  @Test
  public void parseIntegertest()
  {
    /*
     * These tests will cover all the different representations of number values
     * in JSON
     */
    Integer[] values = { 10, 20, 30, 40, 50, 60 };
    String[] strs = new String[6];
    for (int i = 0; i < 6; i++)
      {
        strs[i] = values[i].toString();
        assertEquals((BigDecimal) (JSONUtils.parse(strs[i])),
                     BigDecimal.valueOf(values[i]));
      }// for
    values = new Integer[] { 1, 10, 100, 1000, 10000, 100000 };
    strs = new String[] { "1e0", "1e1", "1e2", "1e3", "1e4", "1e5" };
    for (int i = 0; i < 6; i++)
      {
        strs[i] = values[i].toString();
        assertEquals((BigDecimal) (JSONUtils.parse(strs[i])),
                     BigDecimal.valueOf(values[i]));
      }// for
    double[] vals = { 1.5, -10.5, -11.2, 2.56, 3.67, -100 };
    strs = new String[] { "1.5", "-10.5", "-11.2", "2.56", "3.67", "-1e2" };
    for (int i = 0; i < 6; i++)
      {
        strs[i] = String.valueOf(vals[i]);
        assertEquals((BigDecimal) (JSONUtils.parse(strs[i])),
                     BigDecimal.valueOf(vals[i]));
      }// for
  }// parseIntegertest()

  @Test
  public void parseStringtest()
  {
    /*
     * Parsing a JSONString should return the same string, without the quotation
     * marks at the start and the end.
     */
    String values[] = { "\"HELLO\"", "\"Ashwin\"", "\"Phineas\"", "\"BYE\"" };
    String values2[] = { "HELLO", "Ashwin", "Phineas", "BYE" };
    for (int i = 0; i < 4; i++)
      {
        assertEquals((JSONUtils.parse(values[i])), values2[i]);
      }// for
  }// parseStringtest()

  @Test
  public void parseArraytest()
  {
    /*
     * An array should be able to accept all four types of values from the JSON
     * string given.
     */
    String str = "[10,20,30,40]";
    String str2 = "[212,\"HELLO\",3e2]";

    ArrayList<Object> arrLst = new ArrayList<Object>();
    arrLst.add(BigDecimal.valueOf(10));
    arrLst.add(BigDecimal.valueOf(20));
    arrLst.add(BigDecimal.valueOf(30));
    arrLst.add(BigDecimal.valueOf(40));
    assertEquals((JSONUtils.parse(str)), arrLst);

    arrLst = new ArrayList<Object>();
    arrLst.add(BigDecimal.valueOf(212));
    arrLst.add("HELLO");
    arrLst.add(BigDecimal.valueOf(300));
    assertEquals((JSONUtils.parse(str2)), arrLst);

    str = "[212,\"a\",[10,20]]";

    arrLst = new ArrayList<Object>();
    arrLst.add(BigDecimal.valueOf(212));
    arrLst.add("a");
    ArrayList<Object> arrLst2 = new ArrayList<Object>();
    arrLst2.add(BigDecimal.valueOf(10));
    arrLst2.add(BigDecimal.valueOf(20));
    arrLst.add(arrLst2);
    assertEquals((JSONUtils.parse(str)), arrLst);

    /* JSONExpt has more tests for Arrays */

  }// parseArraytest()

  @Test
  public void parseObjecttest()
  {
    /*
     * Testing a String that represents a Hashtable with BigDecimals, Strings
     * and other Hashtables
     */
    String str =
        "{\"Department\":\"CSC\",\"Number\":207,\"Prof\":"
            + "{\"LName\":\"Rebelsky\",\"FName\":\"Sam\"}}";

    Hashtable hash2 = new Hashtable<Object, String>();

    hash2.put("LName", "Rebelsky");
    hash2.put("FName", "Sam");

    Hashtable hash3 = (Hashtable<Object, String>) JSONUtils.parse(str);
    assertEquals("CSC", hash3.get("Department"));
    assertEquals(BigDecimal.valueOf(207), hash3.get("Number"));
    assertEquals(hash2, hash3.get("Prof"));

    /* JSONExpt has more tests for Hashtables */
  }// parseObjecttest()

  @Test
  public void ToBigDecimal()
  {
    BigDecimal input = (BigDecimal) JSONUtils.parse("2e2");
    BigDecimal input2 = (BigDecimal) JSONUtils.parse("300");
    BigDecimal input3 = (BigDecimal) JSONUtils.parse("-11.4");

    String str = "200";
    String str2 = "300";
    String str3 = "-11.4";

    assertEquals(JSONUtils.toJSONString(input), str);
    assertEquals(JSONUtils.toJSONString(input2), str2);
    assertEquals(JSONUtils.toJSONString(input3), str3);

  }// ToBigDecimal()

  @Test
  public void ToString()
  {
    String input = (String) JSONUtils.parse("\"HELLO\"");
    String input2 = (String) JSONUtils.parse("\"STRING\"");

    String str = "\"HELLO\"";
    String str2 = "\"STRING\"";

    assertEquals(JSONUtils.toJSONString(input), str);
    assertEquals(JSONUtils.toJSONString(input2), str2);
  }// ToString()

  @Test
  public void ToArray()
  {
    String str = "[1,[12,23,545,30000],545]";
    ArrayList<Object> arr = (ArrayList<Object>) JSONUtils.parse(str);
    String result = JSONUtils.toJSONString(arr);

    assertEquals(result, str);

    str = "[[212,\"a\",{\"ids\":[32,20]}],true]";
    arr = (ArrayList<Object>) JSONUtils.parse(str);
    result = JSONUtils.toJSONString(arr);

    assertEquals(result, str);

  }// ToArray()

  @Test
  public void ToObj()
  {
    String str = "{\"Words\":[\"Hello\"]}";
    Hashtable<String, Object> hash =
        (Hashtable<String, Object>) JSONUtils.parse(str);
    String result = JSONUtils.toJSONString(hash);

    assertEquals(result, str);

    str = "{\"Age\":25}";

    hash = (Hashtable<String, Object>) JSONUtils.parse(str);
    result = JSONUtils.toJSONString(hash);
    assertEquals(result, str);

  }// ToObj()

}// class JSONTest()
