package edu.grinnell.csc207;

import java.math.BigDecimal;
import java.lang.Character;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.BufferedReader;

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
  {
    // Return the inputted string minus the surrounding quotes.
    return str.substring(1, (str.length() - 1));
  }// parseString

  /**
   * parseArray
   * 
   * @param str
   *          , a String of JSON
   * @return ArrayList, the array represented by the line of JSON
   * @pre TBA
   * @post TBA
   */
  public static ArrayList<Object> parseArray(String str)
  {
    // Remove outside quotes
    str = parseString(str);
    // Initialize the output array
    ArrayList<Object> output = new ArrayList<Object>();
    // Tracks current char as we iterate through the string
    char current;
    // Tracks the start of each element as we encounter them
    int start = 0;
    String parsed;
    // Keeps track of the last Paren in the JSON string
    int lastParen;
    // Iterate through the string char by char
    for (int i = 0; i < str.length(); i++)
      {
        current = str.charAt(i);
        // If current is a number
        if ((current == '-') || (Character.isDigit(current)))
          {
            lastParen = str.lastIndexOf(']');
            start = i;
            while ((i < str.length()) && (str.charAt(i) != ',')
                   && (i != lastParen))
              {
                i++;
              }// Find the end of the number
            // Add the number to the output
            parsed = str.substring(start, i);
            output.add(parseInteger(parsed));
          } // If digit
        // If current is a String
        else if (current == '\"')
          {
            start = i;
            current = str.charAt(i);
            while ((i < str.length()) && (str.charAt(i) != ','))
              {
                i++;
              } // find the end of the String
            // Add the String to the output
            parsed = str.substring(start, i);
            output.add(parseString(parsed));
          } // If String
        // If current is an array
        else if (current == '[')
          {
            lastParen = str.lastIndexOf(']');
            start = i;
            while ((i < str.length()) && (i != lastParen))
              {
                i++;
              } // Find the end of the array
            i++;
            parsed = str.substring(start, i);
            // Add the array to output
            output.add(parseArray(parsed));
          } // if Array
        // if Current is an Object
        else if (current == '{')
          {
            lastParen = str.lastIndexOf('}');
            start = i;
            while ((i < str.length()) && (i != lastParen))
              {
                i++;
              }// Find the end of the object
            i++;
            parsed = str.substring(start, i);
            // add the object to the Output
            output.add(parseObject(parsed));
          } // if Object
        // if Current is a symbolic Constant
        else if (Character.isAlphabetic(str.charAt(i)))
          {
            start = i;
            while ((i < str.length()) && (str.charAt(i) != '}'))
              {
                i++;
              }// Find the end of the constant
            // and add it to the output
            parsed = str.substring(start, i);
            output.add(parseConstant(parsed));
          }// If Constant
      } // End Parsing input string
    return output;
  } // end parseArray

  /**
   * parseObject
   * 
   * @param str
   *          , a line of JSON code
   * @return Hashtable, the fields and values of an object stored in a hashtable
   * @pre TBA
   * @post TBA
   */
  public static Hashtable<String, Object> parseObject(String str)
  {
    // Declare variables to keep track of output, current position,
    // current key,start of current object,
    Hashtable<String, Object> output = new Hashtable<String, Object>();
    str = parseString(str);
    char current;
    int start = 0;
    int lastParen;
    String key = null;
    String parsed;
    // Iterate through the input string char by char
    for (int i = 0; i < str.length(); i++)
      {
        current = str.charAt(i);
        // if current is a Key (string)
        if (current == '\"')
          {
            i++;
            start = i;
            current = str.charAt(i);
            while ((i < str.length()) && (str.charAt(i) != '\"'))
              {
                i++;
              }// find the end of the Key and store it
            key = str.substring(start, i);
          } // if Key
        // if the next char after key is a :, figure out what value is after
        // that
        else if (current == ':')
          {
            i++;
            start = i;
            // if the next value is a number
            if ((str.charAt(i) == '-') || (Character.isDigit(str.charAt(i))))
              {
                lastParen = str.lastIndexOf(']');
                while ((i < str.length()) && (str.charAt(i) != ',')
                       && (i != lastParen))
                  {
                    i++;
                  }// find the end of the number
                parsed = str.substring(start, i);
                if (key != null)
                  {
                    output.put(key, parseInteger(parsed));
                    key = null;
                  }// Add the number and its key to the output
              } // if Number
            // if the next value is a String
            else if (str.charAt(i) == '\"')
              {
                while ((i < str.length()) && (str.charAt(i) != ','))
                  {
                    i++;
                  }// find the end of the String
                parsed = str.substring(start, i);
                if (key != null)
                  {
                    output.put(key, parseString(parsed));
                    key = null;
                  }// Add the String and its key to the output
              } // if String
            // if the next Value is an Array
            else if (str.charAt(i) == '[')
              {
                lastParen = str.lastIndexOf(']');
                // while ((i < str.length()) && (str.charAt(i) != ']'))
                while ((i < str.length()) && (i != lastParen))
                  {
                    i++;
                  }// find the end of the Array
                i++;
                parsed = str.substring(start, i);
                if (key != null)
                  {
                    output.put(key, parseArray(parsed));
                    key = null;
                  }// Add the array and its key to the output
              } // if Array
            // if the next value is another object
            else if (str.charAt(i) == '{')
              {
                lastParen = str.lastIndexOf('}');
                while ((i < str.length()) && (i != lastParen))
                  {
                    i++;
                  }// find the end of the object
                i++;
                parsed = str.substring(start, i);
                if (key != null)
                  {
                    output.put(key, parseObject(parsed));
                    key = null;
                  } // and add the object and it's key to the output
              } // if Object
            // Else if the next value is a symbolic constant
            else if (Character.isAlphabetic(str.charAt(i)))
              {
                while ((i < str.length()) && (str.charAt(i) != '}'))
                  {
                    i++;
                  }// find the end of the constant
                parsed = str.substring(start, i);
                if (key != null)
                  {
                    output.put(key, parseConstant(parsed));
                    key = null;
                    /*
                     * MUST FIX: Hashtables throw exception when we try to put
                     * null
                     */
                  }// and add the constant and it's key to the output
              } // if Constant
          }// If the character after the key is :
      } // end parsing input string
    return output;
  } // end parseObject

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
   * @pre TBA
   * @post TBA
   */
  public static Object parse(String str)
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
  public static void parse(BufferedReader read, String filename)
    throws Exception
  {
    String line;
    java.io.File infile = new java.io.File(filename);
    java.io.FileReader istream = new java.io.FileReader(infile);
    read = new BufferedReader(istream);
    // get a line from the file
    while ((line = read.readLine()) != null)
      {
        parse(line);
      } // parse until there are no more lines
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
