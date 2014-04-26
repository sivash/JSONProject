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
 * @author Ashwin Sivaramakrishnan
 * @author Phineas Schlossberg
 */
/**
 * @Sources For enumeration inspiration
 * 
 *          http://stackoverflow.com/questions/2048131
 *          /get-an-enumeration-for-the -keys-of-a-map-hashmap-in-java
 * 
 *          parseString code modified from
 *          https://github.com/Grinnell-CSC207/sample-json-parser
 * 
 *          Objects' class
 *          http://stackoverflow.com/questions/541749/how-to-determine
 *          -an-objects-class-in-java
 */
public class JSONUtils
{
  /**
   * removeOutsideChars removeOutsideChars removes the characters surrounding a
   * string, if they match the inputted first and last chars.
   * 
   * @param str
   *          , a string
   * @param first
   *          , a char
   * @param last
   *          , a char
   * @return str without outside characters
   * @pre str.length > 2,
   * @post output.length = str.length - 2, output is str minus the first
   *       character (if it matches first) and last character (if it matches
   *       last)
   * @throws Exception
   *           "Unexpected outside chars around str"
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
      throw new Exception("Unexpected outside chars around " + str);
  } // removeOutsideChars(String, char, char)

  /**
   * removeWhiteSpace removeWhiteSpace removes the whitespace starting from a
   * given index until the end of consecutive whitespace characters in the
   * string
   * 
   * @param str
   *          , a string
   * @param index
   *          , an int
   * @return str without consecutive whitespace starting from index
   * @pre 0 < index < str.length - 1
   * @post output is str without spaces after the index'th char
   * @throws Exception
   *           "Cannot parse empty strings"
   */

  public static Object[] removeWhiteSpace(String str, int index)
    throws Exception
  {
    int lastSpace = index;
    Object[] output = new Object[2];
    if (str.charAt(lastSpace) != ' ')
      {
        output[0] = str;
        output[1] = index;
        return output;
      }// if current char is space
    else
      {
        while ((lastSpace < str.length()) && (str.charAt(lastSpace) == ' '))
          {
            lastSpace++;
          }// while
        if (lastSpace == str.length())
          throw new Exception("Cannot parse empty strings");
        else
          {
            output[0] = str.substring(lastSpace, str.length());
            output[1] = lastSpace;
            return output;
          }// else
      }// else
  }// removeWhiteSpace(String, int)

  /*
   * --------JSON String -> Java Object Methods--------
   */

  /**
   * ParseInteger
   * 
   * @param str
   *          , a string of JSON
   * @return BigDecimal, the number represented by Str
   * @throws Exception
   * @Pre str is a properly formatted JSON number
   * @Post Output is the number represented by str in BigDecimal form
   */

  public static BigDecimal parseInteger(String str)
    throws Exception
  {
    try
      {
        char first;
        if ((first = str.charAt(0)) != '.' && first != '-'
            && (Character.isDigit(first) == false))
          {
            throw new Exception("Input" + str + " is not a proper number");
          } // If char number isn't a number
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
      }// try
    catch (Exception e)
      {
        throw new Exception("Input " + str + " is not a proper number");
      } // if we try to construct a big decimal out of a non-number

  } // parseInteger(String)​

  /**
   * parseString
   * 
   * @param str
   *          , a String of JSON
   * @return String, the string represented by the line of JSON
   * @pre String is a properly formatted JSON string
   * @post Output is the string represented by the JSON string
   */

  public static String parseString(String str)
    throws Exception
  {
    str = removeOutsideChars(str, '"', '"');
    StringBuilder parsed = new StringBuilder();
    char ch;
    int end = 0;
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
                  parsed.append('\\');
                  end = i + 4;
                  while (i < end)
                    {
                      parsed.append(str.charAt(i));
                      i++;
                    }// while
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
   * @pre str is a properly JSON array
   * @post Output is the array represented by the string of JSON
   */

  public static ArrayList<Object> parseArray(String str)
    throws Exception
  {
    str = removeOutsideChars(str, '[', ']');
    ArrayList<Object> output = new ArrayList<Object>();
    StringBuilder parsed = new StringBuilder();
    char current;
    // countBrackets tracks the number of brackets encountered when dealing with
    // arrays and objects. When an opening bracket is added, count is
    // incremented. When a
    // closing bracket is encountered, countBrackets is decremented
    // when countBrackets reaches zero before the end of the input, we know
    // brackets match up.
    int countBrackets = 0;
    // Iterate through str char by char and check first letters to determine
    // type
    for (int i = 0; i < str.length(); i++)
      {
        current = str.charAt(i);
        // if number
        if ((current == '-') || (Character.isDigit(current)))
          {
            // iterate through value til the end and append
            while ((i < str.length())
                   && (!(str.charAt(i) == ',') && (!(str.charAt(i) == ']'))))
              {
                parsed.append(str.charAt(i));
                i++;
              } // while within the value
            // add the send the parsed value to parseInteger and add it to
            // output
            output.add(parseInteger(parsed.toString()));
            parsed.setLength(0);
          } // if first char is - or a number
        // if string
        else if (current == '\"')
          {
            // iterate through value til the end and append
            while ((i < str.length())
                   && (!((str.charAt(i) == ',') && (str.charAt(i - 1) == '\"'))))
              {
                parsed.append(str.charAt(i));
                i++;
              } // while not at end of value
            // send value to parseString and add to output
            output.add(parseString(parsed.toString()));
            parsed.setLength(0);
          }// if first char is a double quote
        // if array
        else if (current == '[')
          {
            // append the opening bracket
            parsed.append(str.charAt(i));
            countBrackets++;
            i++;
            // iterate through the rest of the array
            while ((i < str.length()) && (countBrackets != 0))
              {

                if (str.charAt(i) == '[')
                  {
                    countBrackets++;
                  } // if opening bracket
                else if (str.charAt(i) == ']')
                  {
                    countBrackets--;
                  } // if closing bracket
                parsed.append(str.charAt(i));
                i++;
              }// while
            // loop ends when countBrackets is reduced to 0, or the input ends
            // send parsed value to parseArray and add to output
            output.add(parseArray(parsed.toString()));
            parsed.setLength(0);
          } // if first char is an opening square bracket
        else if (current == '{')
          {
            // append the opening bracket
            parsed.append(str.charAt(i));
            countBrackets++;
            i++;
            // iterate through the rest of the object
            while ((i < str.length()) && (countBrackets != 0))
              {
                if (str.charAt(i) == '{')
                  {
                    countBrackets++;
                  } // if opening {
                else if (str.charAt(i) == '}')
                  {
                    countBrackets--;
                  } // if closing }
                parsed.append(str.charAt(i));
                i++;
              } // loop ends when countBrackets is reduced to 0, or the input
                // ends
            // send parsed value to parseArray and add to output
            output.add(parseObject(parsed.toString()));
            parsed.setLength(0);
          } // if first char is an opening curly bracket
        else if (Character.isAlphabetic(str.charAt(i)))
          {
            // iterate through the constant
            while ((i < str.length()) && (!(str.charAt(i) == ',')))
              {
                parsed.append(str.charAt(i));
                i++;
              } // while in the value
            // send parsed value to parseConstant and append it to the output
            output.add(parseConstant(parsed.toString()));
            parsed.setLength(0);
          } // if first char is a letter
      } // end parsing
    return output;
  } // end parseArray​

  /**
   * parseObject
   * 
   * @param str
   *          , a line of JSON code
   * @return Hashtable, the fields and values of an object stored in a hashtable
   * @throws Exception
   * @pre str is a properly formatted JSON object
   * @post output is a hashtable representing the object described by the JSON
   *       string
   */

  public static Hashtable<String, Object> parseObject(String str)
    throws Exception
  {
    // remove outside brackets, and initialize temp storage values and bracket
    // count
    str = removeOutsideChars(str, '{', '}');
    Hashtable<String, Object> output = new Hashtable<String, Object>();
    String key = null;
    StringBuilder parsed = new StringBuilder();
    char current;
    int countBrackets = 0;
    // iterate through the input
    try
      {
        for (int i = 0; i < str.length(); i++)
          {
            current = str.charAt(i);
            // Find the key
            if (current == '\"')
              {
                i++;
                // iterate through the key and store it.
                while ((i < str.length()) && (!(str.charAt(i) == '\"')))
                  {
                    parsed.append(str.charAt(i));
                    i++;
                  } // while finding key
                key = parsed.toString();
                parsed.setLength(0);
              } // if double quote
            // if char is a colon
            else if (current == ':')
              {
                // skip over it and determine what type of value the next object
                // is.
                i++;
                Object[] hashSpaces = removeWhiteSpace(str, i);
                i = (int) hashSpaces[1];
                // if digit
                if ((str.charAt(i) == '-')
                    || (Character.isDigit(str.charAt(i))))
                  {
                    // find the end of the digit
                    while ((i < str.length())
                           && (!(str.charAt(i) == ',') && (!(str.charAt(i) == ']'))))
                      {
                        parsed.append(str.charAt(i));
                        i++;
                      } // while within digit
                    // send the digit to parseInteger, and add it to the output.
                    output.put(key, parseInteger(parsed.toString()));
                    key = null;
                    parsed.setLength(0);
                  } // if digit
                // if string
                else if (str.charAt(i) == '\"')
                  {
                    // find the end of the string
                    while ((i < str.length())
                           && !((str.charAt(i) == ',') && (str.charAt(i - 1) == '\"')))
                      {
                        parsed.append(str.charAt(i));
                        i++;
                      } // while within string
                    // send the string to parseString and add it to the output
                    output.put(key, parseString(parsed.toString()));
                    key = null;
                    parsed.setLength(0);
                  } // else if string
                // if array
                else if (str.charAt(i) == '[')
                  {
                    // add the first bracket, and increment countBrackets
                    parsed.append(str.charAt(i));
                    countBrackets++;
                    i++;
                    // find the end of the array (at end of string, or when all
                    // brackets have been cancelled out)
                    while ((i < str.length()) && (countBrackets != 0))
                      {
                        if (str.charAt(i) == '[')
                          {
                            countBrackets++;
                          } // if opening bracket count it
                        else if (str.charAt(i) == ']')
                          {
                            countBrackets--;
                          } // else if closing bracket decrement count
                        // otherwise add the char to parsed
                        parsed.append(str.charAt(i));
                        i++;
                      } // while within the array
                    // send the array to parseArray and add it to output
                    output.put(key, parseArray(parsed.toString()));
                    key = null;
                    parsed.setLength(0);
                  } // else if array
                // if object
                else if (str.charAt(i) == '{')
                  {
                    // add the first bracket to count and increment
                    // countBrackets
                    parsed.append(str.charAt(i));
                    countBrackets++;
                    i++;
                    // find the end of the array
                    while ((i < str.length()) && (countBrackets != 0))
                      {
                        if (str.charAt(i) == '{')
                          {
                            countBrackets++;
                          } // if opening bracket count it
                        else if (str.charAt(i) == '}')
                          {
                            countBrackets--;
                          } // else if closing bracket decrement count
                        // otherwise add the char to parsed
                        parsed.append(str.charAt(i));
                        i++;
                      } // while within the array
                    // send the array to parseArray and add to output
                    output.put(key, parseObject(parsed.toString()));
                    key = null;
                    parsed.setLength(0);
                  } // else if array
                // if Symbolic Constant
                else if (Character.isAlphabetic(str.charAt(i)))
                  {
                    // find the end of the constant
                    while ((i < str.length()) && (!(str.charAt(i) == ',')))
                      {
                        parsed.append(str.charAt(i));
                        i++;
                      } // while within the constant
                    // send the constant to parseConstant and add it to output
                    output.put(key, parseConstant(parsed.toString()));
                    key = null;
                    parsed.setLength(0);
                  } // if object
              } // if after a :
          } // for parsing input
      }
    catch (Exception e)
      {
        throw new Exception("Input " + str + " is not a proper object.");
      }// catch
    return output;
  } // parseArray (String str)

  /**
   * parseConstant
   * 
   * @param str
   *          , a String of JSON code
   * @return Boolean, either true, false, or null
   * @throws Exception
   * @pre str is a properly formatted JSON Symbolic Constant
   * @post output is the constant represented by the JSON string
   */

  public static Boolean parseConstant(String str)
    throws Exception
  {
    // Determine whether true false or null and return that
    if (str.equals("true"))
      return (true);
    else if (str.equals("false"))
      return (false);
    else if (str.equals("null"))
      return (null);
    else
      {
        throw new Exception(
                            "Input "
                                + str
                                + " is not a Symbolic Constant (Improper JSON String)");
      }// else
  } // end parseConstant​

  /**
   * parse
   * 
   * Parse a JSON string and return an object that corresponds to the value
   * described in that string. See README.md for further details.
   * 
   * @param str
   *          , a String of JSON code
   * @return the java Object represented by the inputted string
   * @throws Exception
   * @pre Str is a properly formatted string of JSON representing a number,
   *      string, array, object, or symbolic constant
   * @post Output is the object represented by the JSON string
   */

  public static Object parse(String str)
    throws Exception
  {
    if (str.length() == 0)
      throw new Exception("Cannot parse empty strings");
    Object[] parseSpaces = removeWhiteSpace(str, 0);
    str = (String) parseSpaces[0];
    char first = str.charAt(0);
    Object result = null;
    Boolean hasNull = false;

    if ((first == '-') || (Character.isDigit(first)))
      {
        result = parseInteger(str);
      } // if JSON string is a Number
    else if (first == '\"')
      {
        result = parseString(str);
      } // else if JSON string is a String
    else if (first == '[')
      {
        result = parseArray(str);
      } // else if JSON string is an Array
    else if (first == '{')
      {
        result = parseObject(str);
      } // else if JSON string is an Object
    else if (Character.isAlphabetic(first))
      {
        result = parseConstant(str);
        if (result == null)
          {
            hasNull = true;
          }// if
      } // else if JSON string is a Symbolic Constant
    if (result == null && hasNull == false)
      {
        throw new Exception("Input " + str + " is not a proper JSON String");
      } // If inputed String is not any of the above
    return result;
  } // parse(String)

  /**
   * Parse an array of JSON strings and return an array of objects that
   * correspond to the values described in the strings.
   * 
   * @param String
   *          [] strs , an array of Strings of JSON code
   * @return the Object[] represented by the inputed strings
   * @throws Exception
   * @pre String[] strs, an array of Strings that represent JSONStrings
   * @post Returns an array of Objects that correspond to the inputed
   *       JSONStrings.
   */

  public static Object[] parse(String[] strs)
    throws Exception
  {
    Object[] output = new Object[strs.length];
    for (int i = 0; i < strs.length; i++)
      {
        output[i] = parse(strs[i]);
      }// Parses each string into an object and adds it to output array
    return output;
  }// parse(Object[])

  /**
   * parse Parses JSON from a file
   * 
   * @param read
   *          , a BufferedReader, filename, the location of a file with JSON
   *          code
   * @return object, a Java object represented by the JSON input
   * @pre String filename, which must be a valid existing file
   * @post Returns an object corresponding to the JSONString in the file
   **/

  public static Object parseFile(String filename)
    throws Exception
  {
    String line;
    StringBuilder input = new StringBuilder();
    try
      {
        BufferedReader read = new BufferedReader(new FileReader(filename));
        // get a line from the file
        while ((line = read.readLine()) != null)
          {
            input.append(line);
          } // parse until there are no more lines
        read.close();
        return parse(input.toString());
      } // try
    catch (Exception e)
      {
        throw new Exception("Inputted file\"" + filename + "\" not found.");
      } // catch
  } // parse(BufferedReader, String)

  /**
   * parse Parses JSON from a file
   * 
   * @param filename
   *          filenames, and array of strings with the location of files with
   *          JSON code
   * @return object[], an array of Java objects represented by the JSON inputs
   * 
   * @pre String[] of filenames
   * @post Returns an Array of Objects[] based on the contents of the files.
   **/

  public static Object[] parseFile(String[] filenames)
    throws Exception
  {
    try
      {
        Object[] parsedArray = new Object[filenames.length];
        for (int i = 0; i < filenames.length; i++)
          {
            parsedArray[i] = parseFile(filenames[i]);
          } // for
        // parses JSON from each file and adds it to output array
        return parsedArray;
      }// try
    catch (Exception e)
      {
        throw new Exception("Inputted files not found.");
      }// catch
  }// parseFile(String[])

  /*
   * --------Java Object -> JSON String Methods--------
   */

  /**
   * bigDecimaltoJSONString
   * 
   * @param obj
   *          , a number
   * @return str, a line of JSON code representing obj
   * @pre Object obj, a BigDecimal
   * @post Returns the corresponding JSONString for obj.
   */
  public static String bigDecimaltoJSONString(Object obj)
  {
    BigDecimal number = (BigDecimal) obj;
    return number.toString();
  } // bigDecimaltoJSONString(Object obj)

  /**
   * stringToJSONString
   * 
   * @param obj
   *          , a String
   * @return str, a line of JSON code representing obj
   * @pre Object obj, a String
   * @post Returns the corresponding JSONString for obj.
   */
  public static String stringToJSONString(Object obj)
  {
    StringBuilder input = new StringBuilder();
    input.append('\"');
    input.append(((String) obj));
    input.append('\"');

    return input.toString();
  } // stringToJSONString(Object obj)

  /**
   * arrayListToJSONString
   * 
   * @param obj
   *          , an ArrayList
   * @return str, a line of JSON code representing obj
   * @pre Object obj, an ArrayList
   * @post Returns the corresponding JSONString for obj.
   */
  public static String arrayListToJSONString(Object obj)
  {
    StringBuilder input = new StringBuilder();
    input.append("[");
    ArrayList<?> arr = (ArrayList<?>) obj;
    Object[] values = arr.toArray();
    // Iterate through the array til no more elements
    for (int i = 0; i < values.length; i++)
      {
        // if next value is a BigDecimal, call procedure to convert to JSON
        // String
        if (values[i] instanceof BigDecimal)
          {
            values[i] = bigDecimaltoJSONString(values[i]);
          }// if
        // if next value is a String, call procedure to convert to JSON String
        else if (values[i] instanceof String)
          {
            values[i] = stringToJSONString(values[i]);
          }// else if
        // if next value is an Array, recursively call procedure to convert to
        // JSON String
        else if (values[i] instanceof ArrayList<?>)
          {
            values[i] = arrayListToJSONString(values[i]);
          }// else if
        // if next value is an Object, call procedure to convert to JSON String
        else if (values[i] instanceof Hashtable<?, ?>)
          {
            values[i] = hashToJSONString(values[i]);
          }// else if
        // Add last element
        if (i == values.length - 1)
          {
            input.append(values[i]);
          }// if
        else
          {
            input.append(values[i]);
            input.append(",");
          }// else
      }// for
    // end parsing Array
    input.append("]");
    return input.toString();
  } // arrayListToJSONString(Object)

  /**
   * hashToJSONString
   * 
   * @param obj
   *          , a hashtable representing an object
   * @return Str, a string representing that object
   * @pre Object obj, a HashTable
   * @post Returns the corresponding JSONString for obj.
   */
  public static String hashToJSONString(Object obj)
  {
    // Initialize current key, value, hash table, and enumeration to
    // check if we have more in the table.
    StringBuilder input = new StringBuilder();
    input.append("{");

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
        key = stringToJSONString(keys.nextElement());
        // add to output
        input.append(key);
        input.append(":");
        // Set value to next value
        value = values.nextElement();
        // if value is a number
        if (value instanceof BigDecimal)
          {
            value = bigDecimaltoJSONString(value);
            input.append(value);
            input.append(",");
          }// if value is a String
        // add it to the output
        else if (value instanceof String)
          {
            value = stringToJSONString(value);
            input.append(value);
            input.append(",");
          }// if value is an array
        // add it to the output
        else if (value instanceof ArrayList<?>)
          {
            value = arrayListToJSONString(value);
            input.append(value);
            input.append(",");

          }// if value is another array
        // add it to the output
        else if (value instanceof Hashtable<?, ?>)
          {
            value = hashToJSONString(value);
            input.append(value);
            input.append(",");
          }// if
        // add it to the output
      } // end Parsing hashTable
    // remove extraneous comma at end of output and add end brace
    input.setLength(input.length() - 1);
    input.append("}");
    return input.toString();
  } // hashToJSONString(Object obj)

  /**
   * constantToJSONString
   * 
   * @param obj
   *          , a Symbolic Constant null, false, or true
   * @return str, a JSON line representing that constant
   * @pre Object obj, a Boolean
   * @post Returns the corresponding JSONString for obj.
   */
  public static String constantToJSONString(Object obj)
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
  } // constantToJSONString(Object obj)

  /**
   * toJSONString
   * 
   * Given an object created by parse, generate the JSON string that corresponds
   * to the object.
   * 
   * @exception Exception
   *              If the object cannot be converted, e.g., if it does not
   *              correspond to something created by parse.
   * @pre An object that is either a BigInteger, a String, an ArrayList, a
   *      Hashtable or a Boolean
   * @post Returns the corresponding JSONString for the object
   */
  public static String toJSONString(Object obj)
    throws Exception
  {
    String output = null;
    // Determine what the object we're dealing with is
    // if number, call the toBigDecimal
    if (obj instanceof BigDecimal)
      {
        output = bigDecimaltoJSONString(obj);
      } // if Number
    // if a string, call toStr
    else if (obj instanceof String)
      {
        output = stringToJSONString(obj);
      } // if String
    // if an Array, call toArrayList
    else if (obj instanceof ArrayList<?>)
      {
        output = arrayListToJSONString(obj);
      } // if Array
    // if a Hashtable, call toHash
    else if (obj instanceof Hashtable<?, ?>)
      {
        output = hashToJSONString(obj);
      } // if Hash (object)
    // if a symbolic constant, call toConstant
    else if (obj instanceof Boolean)
      {
        output = constantToJSONString(obj);
      } // if constant
    if (output == null)
      {
        throw new Exception("Object " + obj.getClass()
                            + " is not supported by this method.");
      }// if
    return output;
  } // toJSONString(Object)

  /**
   * iParser
   * 
   * Guides the user through making an object from a JSONString and prints the
   * returned object. Useful for testing specific sets of strings without having
   * to print them manually
   * 
   * @pre none
   * @post Prints the string built and operates until Quit command is issued
   */
  public static void iParser()
    throws Exception
  {
    java.io.BufferedReader eyes;
    eyes = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

    java.io.PrintWriter pen;
    pen = new java.io.PrintWriter(System.out, true);
    String input = null;
    String value = null;
    Object result = null;
    Object[] values = new Object[5];
    int index = 0;

    // Prints options, and performs actions based on the options selected.
    while (true)
      {
        pen.println("Enter the Object you would like to build: ");
        pen.print("[Options: ");
        pen.println("Integer, String, Array, Object, Constant]");
        pen.print("[Other Available Commands: ");
        pen.println("Quit, Print]");

        input = eyes.readLine();
        if (input.equalsIgnoreCase("Quit"))
          {
            break;
          }// if
        else if (input.equalsIgnoreCase("Print"))
          {
            pen.print("[ ");
            for (int i = 0; i < index; i++)
              {
                pen.print(values[i] + " ");
              }// for
            pen.println("]");
          }// else if
        else
          {
            pen.println("Enter the JSONString: ");
            value = eyes.readLine();
            try
              {
                switch (input)
                  {
                    case "Integer":
                      result = parseInteger(value);
                      values[index] = result;
                      break;
                    case "String":
                      result = parseString(value);
                      values[index] = result;
                      break;
                    case "Array":
                      result = parseArray(value);
                      values[index] = result;
                      break;
                    case "Object":
                      result = parseObject(value);
                      values[index] = result;
                      break;
                    case "Constant":
                      result = parseConstant(value);
                      values[index] = result;
                      break;
                  }// switch
                index++;
                if (index > 4)
                  index = 0;
              }// try
            catch (Exception e)
              {
                pen.println("Your string was incorrectly formatted, please try again.");
              }// catch
            pen.println("\tResult = " + result);
            result = null;
          }// else
      }// while
    eyes.close();
    return;
  }// iParser()
}// class JSONUtils()
