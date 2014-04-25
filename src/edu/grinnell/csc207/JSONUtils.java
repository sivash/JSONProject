package edu.grinnell.csc207;

import java.math.BigDecimal;
import java.lang.Character;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.StringBuilder;

/**
 * @authors Ashwin Sivaramakrishnan
 *          Phineas Schlossberg
 */
/**
 * @Sources For enumeration inspiration
 *          http://stackoverflow.com/questions/2048131
 *          /get-an-enumeration-for-the -keys-of-a-map-hashmap-in-java
 */
public class JSONUtils
{

  /**
   * removeOutsideChars removes the characters surrounding a string, if they
   * match the inputted first and last chars.
   * 
   * @param str
   *          , a string
   * @param first
   *          , a char
   * @param last
   *          , a char
   * @return str without outside characters
   * @pre TBA
   * @post TBA
   * @throws Exception
   */
  public static String removeOutsideChars(String str, char first, char last)
    throws Exception
  {
    if (str.charAt(0) == first && str.charAt(str.length() - 1) == last)
      {
        return str.substring(1, str.length() - 1);
      } // if brackets are correct
    else
      // otherwise throw an exception
      throw new Exception("Unexpected Outside chars around" + str);
  } // removeOutsideChars​

  /*
   * --------JSON String -> Java Object Methods--------
   */

  /**
   * ParseInteger
   * 
   * @param str
   *          , a string of JSON
   * @return BigDecimal, the number represented by Str
   * @Pre TBA
   * @Post TBA
   */
  public static BigDecimal parseInteger(String str)
  {
    // Split the string at e, so if the number is in the form
    // #e# (ie: 12e5 AKA 12 * 10^12), we have an array with the
    // base number and the exponent
    String[] input = str.split("e");
    // Initialized output and exponent variables
    BigDecimal output = BigDecimal.ZERO;
    int power = 0;
    if (input.length == 1)
      {
        output = new BigDecimal(input[0]);
      } // If there is no e
    else if (input.length == 2)
      {
        output = new BigDecimal(input[0]);
        power = Integer.parseInt(input[1]);
        output = output.multiply(BigDecimal.TEN.pow(power));
      } // If there is an e
    return output;
  } // parseInteger(String)

  /**
   * parseString
   * 
   * @param str
   *          , a String of JSON
   * @return String, the string represented by the line of JSON
   * @pre TBA
   * @post TBA
   */
  public static String parseString(String str)
    throws Exception
  {
    str = removeOutsideChars(str, '"', '"');
    StringBuilder parsed = new StringBuilder();
    char ch;
    for (int i = 0; i < str.length(); i++)
      {
        ch = str.charAt(i);
        if (ch == '\\')
          {
            switch (ch = str.charAt(++i))
              {
                case '"':
                case '\\':
                case '/':
                  parsed.append((char) ch);
                  break;
                case 'b':
                  parsed.append('\b');
                  break;
                case 'f':
                  parsed.append('\f');
                  break;
                case 'n':
                  parsed.append('\n');
                  break;
                case 'r':
                  parsed.append('\r');
                  break;
                case 't':
                  parsed.append('\t');
                  break;
                case 'u':
                  throw new Exception("Unicode unsupported");
                default:
                  throw new Exception("Invalid backslash character: \\" + ch);
              } // switch
          } // if backslash
        parsed.append(ch);
      } // for

    return parsed.toString();
  }// parseString

  /**
   * parseArray
   * 
   * @param str
   *          , a String of JSON
   * @return ArrayList, the array represented by the line of JSON
   * @throws Exception
   * @pre TBA
   * @post TBA
   */
  public static ArrayList<Object> parseArray(String str)
    throws Exception
  {
    str = removeOutsideChars(str, '[', ']');
    ArrayList<Object> output = new ArrayList<Object>();
    StringBuilder parsed = new StringBuilder();
    char current;
    int countBrackets = 0;
    for (int i = 0; i < str.length(); i++)
      {
        current = str.charAt(i);
        if ((current == '-') || (Character.isDigit(current)))
          {
            while ((i < str.length())
                   && (!(str.charAt(i) == ',') && (!(str.charAt(i) == ']'))))
              {
                parsed.append(str.charAt(i));
                i++;
              }
            output.add(parseInteger(parsed.toString()));
            parsed.setLength(0);
          }
        else if (current == '\"')
          {
            parsed.append(str.charAt(i));
            i++;
            while ((i < str.length()) && (!(str.charAt(i) == ',')))
              {
                parsed.append(str.charAt(i));
                i++;
              }
            output.add(parseString(parsed.toString()));
            parsed.setLength(0);
          }
        else if (current == '[')
          {
            parsed.append(str.charAt(i));
            countBrackets++;
            i++;
            while ((i < str.length()) && (countBrackets != 0))
              {
                if (str.charAt(i) == '[')
                  {
                    countBrackets++;
                  }
                else if (str.charAt(i) == ']')
                  {
                    countBrackets--;
                  }
                parsed.append(str.charAt(i));
                i++;
              }
            output.add(parseArray(parsed.toString()));
            parsed.setLength(0);
          }
        else if (current == '{')
          {
            parsed.append(str.charAt(i));
            countBrackets++;
            i++;
            while ((i < str.length()) && (countBrackets != 0))
              {
                if (str.charAt(i) == '{')
                  {
                    countBrackets++;
                  }
                else if (str.charAt(i) == '}')
                  {
                    countBrackets--;
                  }
                parsed.append(str.charAt(i));
                i++;
              }
            output.add(parseObject(parsed.toString()));
            parsed.setLength(0);
          }
        else if (Character.isAlphabetic(str.charAt(i)))
          {
            while ((i < str.length()) && (!(str.charAt(i) == ']')))
              {
                parsed.append(str.charAt(i));
                i++;
              }
            output.add(parseConstant(parsed.toString()));
            parsed.setLength(0);
          }
      }
    return output;
  } // end parseArray

  /**
   * parseObject
   * 
   * @param str
   *          , a line of JSON code
   * @return Hashtable, the fields and values of an object stored in a hashtable
   * @throws Exception
   * @pre TBA
   * @post TBA
   */
  public static Hashtable<String, Object> parseObject(String str)
    throws Exception
  {
    str = removeOutsideChars(str, '{', '}');
    Hashtable<String, Object> output = new Hashtable<String, Object>();
    String key = null;
    StringBuilder parsed = new StringBuilder();
    char current;
    int countBrackets = 0;

    for (int i = 0; i < str.length(); i++)
      {
        current = str.charAt(i);
        if (current == '\"')
          {
            i++;
            while ((i < str.length()) && (!(str.charAt(i) == '\"')))
              {
                parsed.append(str.charAt(i));
                i++;
              }
            key = parsed.toString();
            parsed.setLength(0);
          }
        else if (current == ':')
          {
            i++;
            if ((str.charAt(i) == '-') || (Character.isDigit(str.charAt(i))))
              {
                while ((i < str.length())
                       && (!(str.charAt(i) == ',') && (!(str.charAt(i) == ']'))))
                  {
                    parsed.append(str.charAt(i));
                    i++;
                  }
                output.put(key, parseInteger(parsed.toString()));
                key = null;
                parsed.setLength(0);
              }
            else if (str.charAt(i) == '\"')
              {
                parsed.append(str.charAt(i));
                i++;
                while ((i < str.length()) && (!(str.charAt(i) == ',')))
                  {
                    parsed.append(str.charAt(i));
                    i++;
                  }
                output.put(key, parseString(parsed.toString()));
                key = null;
                parsed.setLength(0);
              }
            else if (str.charAt(i) == '[')
              {
                parsed.append(str.charAt(i));
                countBrackets++;

                i++;
                while ((i < str.length()) && (countBrackets != 0))
                  {
                    if (str.charAt(i) == '[')
                      {
                        countBrackets++;
                      }
                    else if (str.charAt(i) == ']')
                      {
                        countBrackets--;
                      }
                    parsed.append(str.charAt(i));
                    i++;
                  }
                output.put(key, parseArray(parsed.toString()));
                key = null;
                parsed.setLength(0);
              }
            else if (str.charAt(i) == '{')
              {
                parsed.append(str.charAt(i));
                countBrackets++;
                i++;
                while ((i < str.length()) && (countBrackets != 0))
                  {
                    if (str.charAt(i) == '{')
                      {
                        countBrackets++;
                      }
                    else if (str.charAt(i) == '}')
                      {
                        countBrackets--;
                      }
                    parsed.append(str.charAt(i));
                    i++;
                  }
                output.put(key, parseObject(parsed.toString()));
                key = null;
                parsed.setLength(0);
              }
            else if (Character.isAlphabetic(str.charAt(i)))
              {
                while ((i < str.length()) && (!(str.charAt(i) == ']')))
                  {
                    parsed.append(str.charAt(i));
                    i++;
                  }
                output.put(key, parseConstant(parsed.toString()));
                key = null;
                parsed.setLength(0);
              }
          }
      }
    return output;
  }

  /**
   * parseConstant
   * 
   * @param str
   *          , a String of JSON code
   * @return Boolean, either true, false, or null
   * @pre TBA
   * @post TBA
   */
  public static Boolean parseConstant(String str)
  {
    // Determine whether true false or null and return that
    Boolean value = null;
    if (str.equals("true"))
      value = true;
    else if (str.equals("false"))
      value = false;
    else if (str.equals("null"))
      return value;
    return value;
  } // end parseConstant

  /**
   * Parse a JSON string and return an object that corresponds to the value
   * described in that string. See README.md for further details.
   * 
   * @param str
   *          , a String of JSON code
   * @return the java Object represented by the inputted string
   * @throws Exception
   * @pre TBA
   * @post TBA
   */
  public static Object parse(String str)
    throws Exception
  {
    char first = str.charAt(0);

    if ((first == '-') || (Character.isDigit(first)))
      {
        return parseInteger(str);
      } // if JSON string is a Number
    else if (first == '\"')
      {
        return parseString(str);
      } // if JSON string is a String
    else if (first == '[')
      {
        return parseArray(str);
      } // if JSON string is an Array
    else if (first == '{')
      {
        return parseObject(str);
      } // if JSON string is an Object
    else if (Character.isAlphabetic(first))
      {
        return parseConstant(str);
      } // if JSON string is a Symbolic Constant
    return str;
  } // parse(String)

  /**
   * parse Parses JSON from a file
   * 
   * @param read
   *          , a BufferedReader, filename, the location of a file with JSON
   *          code
   * @return object, a Java object represented by the JSON input
   **/
  public static void parseFile(String filename)
    throws Exception
  {
    String line;
    StringBuilder input = new StringBuilder();
    BufferedReader read = new BufferedReader(new FileReader(filename));
    // get a line from the file
    while ((line = read.readLine()) != null)
      {
        input.append(line);
      } // parse until there are no more lines
    // parse(input.toString());
    System.out.println(parse(input.toString()));
    read.close();
  } // parse(BufferedReader, String)

  /*
   * --------Java Object -> JSON String Methods--------
   */

  /**
   * toBigDecimal
   * 
   * @param obj
   *          , a number
   * @return str, a line of JSON code representing obj
   * @pre TBA
   * @post TBA
   */
  public static String toBigDecimal(Object obj)
  {
    BigDecimal number = (BigDecimal) obj;
    return number.toString();
  } // toBigDecimal(Object obj)

  /**
   * toStr
   * 
   * @param obj
   *          , a String
   * @return str, a line of JSON code representing obj
   * @pre TBA
   * @post TBA
   */
  public static String toStr(Object obj)
  {
    String input = (String) obj;
    return "\"" + input + "\"";
  } // toStr(Object obj)

  /**
   * toArrayList
   * 
   * @param obj
   *          , an ArrayList
   * @return str, a line of JSON code representing obj
   * @pre TBA
   * @post TBA
   */
  public static String toArrayList(Object obj)
  {
    String input = "[";
    ArrayList<?> arr = (ArrayList<?>) obj;
    Object[] values = arr.toArray();
    // Iterate through the array til no more elements
    for (int i = 0; i < values.length; i++)
      {
        // if next value is a BigDecimal, call procedure to convert to JSON
        // String
        if (values[i] instanceof BigDecimal)
          {
            values[i] = toBigDecimal(values[i]);
          }
        // if next value is a String, call procedure to convert to JSON String
        else if (values[i] instanceof String)
          {
            values[i] = toStr(values[i]);
          }
        // if next value is an Array, recursively call procedure to convert to
        // JSON String
        else if (values[i] instanceof ArrayList<?>)
          {
            values[i] = toArrayList(values[i]);
          }
        // if next value is an Object, call procedure to convert to JSON String
        else if (values[i] instanceof Hashtable<?, ?>)
          {
            values[i] = toHash(values[i]);
          }
        // Add last element
        if (i == values.length - 1)
          {
            input = input + values[i];
          }
        else
          input = input + values[i] + ",";
      } // end parsing Array
    input = input + "]";
    return input;
  } // end toArrayList

  /**
   * toHash
   * 
   * @param obj
   *          , a hashtable representing an object
   * @return Str, a string representing that object
   * @pre TBA
   * @post TBA
   */
  public static String toHash(Object obj)
  {
    // Initialize current key, value, hash table, and enumeration to
    // check if we have more in the table.
    String input = "{";
    Object value;
    Object key;
    @SuppressWarnings("unchecked")
    Hashtable<String, Object> hash = (Hashtable<String, Object>) obj;
    Enumeration<String> keys = Collections.enumeration(hash.keySet());
    Enumeration<Object> values = Collections.enumeration(hash.values());
    // Loop through hashtable while there is more in it
    while (keys.hasMoreElements())
      {
        // Set key to next key
        key = toStr(keys.nextElement());
        // add to output
        input = input + key + ":";
        // Set value to next value
        value = values.nextElement();
        // if value is a number
        if (value instanceof BigDecimal)
          {
            value = toBigDecimal(value);
            input = input + value + ",";
          }// add it to the output
        // if value is a String
        else if (value instanceof String)
          {
            value = toStr(value);
            input = input + value + ",";
          }// add it to the output
        // if value is an array
        else if (value instanceof ArrayList<?>)
          {
            value = toArrayList(value);
            input = input + value + ",";
          }// add it to the output
        // if value is another array
        else if (value instanceof Hashtable<?, ?>)
          {
            value = toHash(value);
            input = input + value + ",";
          }// add it to the output
      } // end Parsing hashTable
    // remove extraneous comma at end of output and add end brace
    input = input.substring(0, input.length() - 1);
    input = input + "}";
    return input;
  } // toHash (Object obj)

  /**
   * toConstant
   * 
   * @param obj
   *          , a Symbolic Constant null, false, or true
   * @return str, a JSON line representing that constant
   * @pre TBA
   * @post TBA
   */
  public static String toConstant(Object obj)
  {
    // figure out what constant it is
    String result;
    if ((Boolean) obj == null)
      result = "null";
    else if ((Boolean) obj == true)
      result = "true";
    else
      result = "false";
    // and return it
    return result;
  } // toConstant(Object obj)

  /**
   * Given an object created by parse, generate the JSON string that corresponds
   * to the object.
   * 
   * @exception Exception
   *              If the object cannot be converted, e.g., if it does not
   *              correspond to something created by parse.
   */
  public static String toJSONString(Object obj)
  {
    String output = null;
    // Determine what the object we're dealing with is
    // if number, call the toBigDecimal
    if (obj instanceof BigDecimal)
      {
        return toBigDecimal(obj);
      } // if Number
    // if a string, call toStr
    else if (obj instanceof String)
      {
        return toStr(obj);
      } // if String
    // if an Array, call toArrayList
    else if (obj instanceof ArrayList<?>)
      {
        return toArrayList(obj);
      } // if Array
    // if a Hashtable, call toHash
    else if (obj instanceof Hashtable<?, ?>)
      {
        return toHash(obj);
      } // if Hash (object)
    // if a symbolic constant, call toConstant
    else if (obj instanceof Boolean)
      {
        return toConstant(obj);
      } // if constant
    return output;
  } // toJSONString(Object)
}// class JSONUtils()
