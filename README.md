README
This is the readme for Version .1 of The SparcerParser

Version Notes
----------
.01 -> Initial release of The SparcerParser Library

Purpose
----------
SparcerParser is a JSON Parser/Unparser library. It's primary capabilities are too
turn a Java Object into a string of JSON, or take a string of JSON and
turn it into a Java Object.

Features
----------
Ignores incorrectly formatted whitespace
Accepts unicode
Can input JSON from file
Can take multiple lines of JSON at once, from file or strings.

Download
----------
The current version of SparcerParser can be accessed from
 https://github.com/sivash/JSONProject

Documentation
----------
JavaDoc documentation for each menthod can be found within the library's .java files

How to Use
----------
Import the SparcerParser library to gain access to its methods.
To parse a string of JSON into a java object, use parse(String str)
To parse a Java Object into a string of JSON, use toJsonString (Object obj)
To parse JSON from a file, use parseFile (String Filename)
To access the interactive object builder, run the iParserTest file.
For additional information check in-file documentation.

Developement
----------
The SparcerParser was developed by Phineas Schlossberg and Ashwin
Sivaramakrishnan.
The SparcerParser is protected under the GPL v3 License

Contact
----------
The developers can be contacted for further information or bug reporting at 
schlossb@grinnell.edu or sivarama@grinnell.edu
