package edu.grinnell.csc207;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Hashtable;

//import java.util.Hashtable;
public class JSONExpt
{
  public static void main(String[] args)
    throws Exception
  {
    System.out.println("____________________________");
    System.out.println("parseConstant");
    System.out.println("----------------------------");

    System.out.println(JSONUtils.parse("null"));
    System.out.println(JSONUtils.parse("false"));
    System.out.println(JSONUtils.parse("true"));

    System.out.println("____________________________");
    System.out.println("parseInteger");
    System.out.println("----------------------------");

    System.out.println(JSONUtils.parse("-11.23"));
    System.out.println(JSONUtils.parse("2e2"));

    System.out.println("____________________________");
    System.out.println("parseString");
    System.out.println("----------------------------");

    System.out.println(JSONUtils.parse("\"Hello\""));

    System.out.println("____________________________");
    System.out.println("parseArray");
    System.out.println("----------------------------");

    System.out.println(JSONUtils.parse("[]"));
    System.out.println(JSONUtils.parse("[20]"));
    System.out.println(JSONUtils.parse("[1,[12,[23],[],545,30000],545]"));
    System.out.println(JSONUtils.parse("[212,\"ASHWIN\",3e2]"));
    System.out.println(JSONUtils.parse("[212,\"HELLO\",3e2,[3000]]"));
    System.out.println("--------");
    System.out.println(JSONUtils.parse("[[212,\"a\",{\"id\":32}],546]"));
    System.out.println(JSONUtils.parse("[[212,\"a\",{\"ids\":[32,20]}],true]"));

    System.out.println("____________________________");
    System.out.println("parseObject");
    System.out.println("----------------------------");

    System.out.println((JSONUtils.parse("{\"Age\":\"ten\"}")).toString());
    System.out.println((JSONUtils.parse("{\"Department\":\"CSC\",\"Number\":207}")).toString());

    System.out.println((JSONUtils.parse("{\"Words\":[\"Hello\"]}")).toString());
    System.out.println((JSONUtils.parse("{\"Ages\":[2,3,4,2e1]}")).toString());

    System.out.println((JSONUtils.parse("{\"Department\":\"CSC\",\"Number\":207,\"Prof\":"
                                        + "{\"LName\":\"Rebelsky\",\"FName\":\"Sam\"}}")).toString());
    System.out.println((JSONUtils.parse("{\"Constant\":false}")).toString());
    System.out.println(JSONUtils.parse("[[212,\"a\",{\"ids\":[32,20]}],true]"));
    System.out.println(JSONUtils.parse("{\"a\":[],\"b\":[]}"));

    System.out.println("____________________________");
    System.out.println("toString Tests");
    System.out.println("----------------------------");

    System.out.println();
    System.out.println("toBigDecimal");
    System.out.println("----------------------------");

    BigDecimal input = (BigDecimal) JSONUtils.parse("2e2");
    System.out.println(JSONUtils.toJSONString(input));

    input = BigDecimal.valueOf(2.2);
    System.out.println(JSONUtils.toJSONString(input));

    System.out.println();
    System.out.println("toStr");
    System.out.println("----------------------------");

    String str = "\"Hello\"";
    System.out.println(JSONUtils.toJSONString(str));

    System.out.println();
    System.out.println("toArrayList");
    System.out.println("----------------------------");

    ArrayList<Object> test = new ArrayList<Object>();
    test.add(20);
    test.add("Hello");
    test.add(40);
    test.add(60);
    System.out.println(JSONUtils.toJSONString(test));

    System.out.println();
    System.out.println("toHash");
    System.out.println("----------------------------");

    @SuppressWarnings("unchecked")
    Hashtable<String, Object> hashTest =
        (Hashtable<String, Object>) (JSONUtils.parse("{\"Department\":\"CSC\",\"Number\":[207,300],\"Prof\""
                                                     + ":{\"LName\":\"Rebelsky\",\"FName\":\"Sam\"}}"));

    System.out.println(JSONUtils.toJSONString(hashTest));
    System.out.println(JSONUtils.toJSONString(false));
    System.out.println(JSONUtils.parse("[1,[12,23,545,30000],[20]]"));
    System.out.println(JSONUtils.parse("[{\"a\":[{},{}]},[]]"));

    JSONUtils.parseFile("/home/sivarama/FileTest");

  }// main
}// class JSONExpt
