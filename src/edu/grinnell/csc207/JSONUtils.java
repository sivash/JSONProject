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
      throw new Exception("Unexpected outside chars around " + str);
  } // removeOutsideChars

  /**
   * removeWhiteSpace removes the whitespace starting from a given index until
   * the end of consecutive whitespace characters in the string
   * 
   * @param str
   *          , a string
   * @param index
   *          , an int
   * @return str without consecutive whitespace starting from index
   * @pre TBA
   * @post TBA
   * @throws Exception
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
      }
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
          }
      }// else
  }// removeWhiteSpace(String, index)

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
   * @Pre TBA
   * @Post TBA
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
      }
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
   * @pre TBA
   * @post TBA
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
              }
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
   * @pre TBA
   * @post TBA
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
                  } // if string
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
                          } // if closing bracket decrement count
                        // otherwise add the char to parsed
                        parsed.append(str.charAt(i));
                        i++;
                      } // while within the array
                    // send the array to parseArray and add it to output
                    output.put(key, parseArray(parsed.toString()));
                    key = null;
                    parsed.setLength(0);
                  } // if array
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
                          } // if closing bracket decrement count
                        // otherwise add the char to parsed
                        parsed.append(str.charAt(i));
                        i++;
                      } // while within the array
                    // send the array to parseArray and add to output
                    output.put(key, parseObject(parsed.toString()));
                    key = null;
                    parsed.setLength(0);
                  } // if array
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
      }
    return output;
  } // parseArray (String str)

  /**
   * parseConstant
   * 
   * @param str
   *          , a String of JSON code
   * @return Boolean, either true, false, or null
   * @throws Exception
   * @pre TBA
   * @post TBA
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
      } // if JSON string is a String
    else if (first == '[')
      {
        result = parseArray(str);
      } // if JSON string is an Array
    else if (first == '{')
      {
        result = parseObject(str);
      } // if JSON string is an Object
    else if (Character.isAlphabetic(first))
      {
        result = parseConstant(str);
        if (result == null)
          {
            hasNull = true;
          }
      } // if JSON string is a Symbolic Constant
    if (result == null && hasNull == false)
      {
        throw new Exception("Input " + str + " is not a proper JSON String");
      } // If inputted String is not any of the above
    return result;
  } // parse(String)

  public static Object[] parse(String[] strs)
    throws Exception
  {
    Object[] output = new Object[strs.length];
    for (int i = 0; i < strs.length; i++)
      {
        output[i] = parse(strs[i]);
      }// Parses each string into an object and add it to output array
    return output;
  }

  /**
   * parse Parses JSON from a file
   * 
   * @param read
   *          , a BufferedReader, filename, the location of a file with JSON
   *          code
   * @return object, a Java object represented by the JSON input
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
        // parse(input.toString());
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
   **/

  public static Object[] parseFile(String[] filenames)
    throws Exception
  {
    Object[] parsedArray = new Object[filenames.length];
    for (int i = 0; i < filenames.length; i++)
      {
        parsedArray[i] = parseFile(filenames[i]);
      } // parses JSON from each file and adds it to output array
    return parsedArray;
  }

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

  /**
   * iParser
   * 
   * Guides the user through making an object from a JSONString and prints the
   * returned object. Useful for testing specific sets of strings without having
   * to print them manually
   * 
   * @pre TBA
   * @post TBA
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
                  }
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
