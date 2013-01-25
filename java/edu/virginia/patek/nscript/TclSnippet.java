/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */

package edu.virginia.patek.nscript;

import edu.virginia.patek.nscript.NSObject;
import edu.virginia.patek.nscript.NSRelation;
import edu.virginia.patek.nscript.Messages;

import java.util.*;

/** Represents a template or class of objects in the simulation. A snippet
 *  contains meta-information about a class of objects, such as the attributes
 *  that it contains, their default values and options.
 *  It also contains information on how to translate this information into
 *  a Tcl script.
 */
public class TclSnippet extends Object {
  /** The name of the snippet. */
  String name;
  /** Name of the base class. A snippet belongs to a given category, to
    *  provide more flexibility. For example, TCP, TCPSink, UDP, and Null
    *  share the 'agent' base class.
    */
  String base;

  /** Tells if a given class is a relation or an entity. */
  public boolean isRelation;
  /** Tells if the given class should be uniquely instantiated. For example,
    *  there can only be one ns environment class per simulation model.
    *  Also there can only be a unique application attached to an agent.
    */
  public boolean isUnique;

  /** The icon used to represent objects of this class in the graphic
    *  environment.
    */
  int icon;
  /** Describes the class base required to create a relation of this type.
    *  It applies only to relation objects. For example a DuplexLink object
    *  can only connect to node objects. In this case describes the base class
    *  of the object where the relation starts.
    */
  String fromBase;
  /** Describes the class base of the object where the relation ends. */
  String toBase;

  /** Flags if the relation start must be unique. This is that the start
    *  object can only accept one relation of this class, and applies only
    *  to relation objects.
    */
  public boolean isFromBaseUnique;
  /** Flags if the relation end must be unique. This means that the end
    *  object can only accept one relation of this class, and applies only
    *  to relation objects.
    */
  public boolean isToBaseUnique;

  /** The style of the base of the relation (start of the relation), so
    *  that objects can be drawn with different arrow styles.
    */
  int baseStyle;

  /** The style of the line for a relation object. This is if the line
    *  should be drawn solid, dotted, or dashed.
    */
  int lineStyle;

  /** The width of the line for relation objects. */
  int lineWidth;

  /** The style of the end of the relation, so that relation objects can
    *  be drawn with different arrows styles.
    */
  int endStyle;

  /** The collection of attributes of the snippet. */
  ArrayList<TclAttribute> attributes;
  /** The collection of patterns of the snippet. */
  ArrayList<TclPattern> patterns;

  /** Constant indicating a SOLID line style (for relation objects, only). */
  static final int SOLID = 0;
  /** Constant indicating a DASHED line style (for relation objects, only). */
  static final int DASHED = 1;
  /** Constant indating a DOTTED line style (for relations objects, only). */
  static final int DOTTED = 2;

  /** Constructor. Creates the snippet by parsing itself from a given string.
   *  @param s the string from which to read the snippet definition. */
  public TclSnippet(String s)
  {
    attributes = new ArrayList<TclAttribute>();
    patterns = new ArrayList<TclPattern>();
    parseSelf(s);
  }

  /** Method that parses the attributes and patterns for the snippet
   *  from a String object.
   *
   *  @param s the String containing the information.
   */
  void parseSelf(String s)
  {
    int headEndIdx, beginIdx, endIdx;
    String head, attr, patt, a;
    headEndIdx = s.indexOf(':');
    beginIdx = s.indexOf("begin");
    endIdx = s.indexOf("end");

    head = s.substring(0, headEndIdx).trim();
    // System.err.println("HEAD:\n"+head);
    attr = s.substring(headEndIdx + 1, beginIdx).trim();
    //  System.err.println("ATTRIBUTES:\n"+attr);
    patt = s.substring(beginIdx + 5, endIdx).trim();
    //  System.err.println("PATTERNS:\n"+patt);

    StringTokenizer st = new StringTokenizer(head);
    a = st.nextToken();
    if (a.indexOf('!') >= 0) {
      isUnique = true;
      a = a.substring(1);
    } else {
      isUnique = false;
    }

    isRelation = a.equals("relation");
    st.nextToken();
    setName(st.nextToken());
    if (isRelation) {
      a = st.nextToken();
      if (a.indexOf('!') >= 0) {
        a = a.substring(1);
        isFromBaseUnique = true;
      } else {
        isFromBaseUnique = false;
      }
      setFromBase(a);
      a = st.nextToken();
      if (a.indexOf('!') >= 0) {
        a = a.substring(1);
        isToBaseUnique = true;
      } else {
        isToBaseUnique = false;
      }
      setToBase(a);
      setBaseStyle(Integer.parseInt(st.nextToken()));
      setLineStyle(Integer.parseInt(st.nextToken()));
      setLineWidth(Integer.parseInt(st.nextToken()));
      setEndStyle(Integer.parseInt(st.nextToken()));
      //   System.err.println("Read relation snippet: " + baseStyle + "," + lineStyle + "," + lineWidth + "," + endStyle );
    } else {
      setBase(st.nextToken());
      setIcon(Integer.parseInt(st.nextToken()));
    }

    // Now, read the attribute definitions.

    int iStart = 0, iEnd;
    while ((iEnd = attr.indexOf(';', iStart)) >= 0) {
      attributes.add(new TclAttribute(attr.substring(iStart, iEnd)));
      iStart = iEnd + 1;
    }

    // Finally, read the patterns.

    iStart = 0;
    while ((iEnd = patt.indexOf(';', iStart)) >= 0) {
      patterns.add(new TclPattern(patt.substring(iStart, iEnd)));
      iStart = iEnd + 1;
    }
  }

  // Gets & sets for all members
  /** Sets the name of the snippet.
    *  @param inName the new name for the snippet. */
  public void setName(String inName)
  {
    name = inName;
  }

  /** Reports back its name.
   *  @return the name as a String object. */
  public String getName()
  {
    return name;
  }

  /** Gets the base class.
    *  @param inBase a string containing the class name of the
    *                base class object.
    */
  public void setBase(String inBase)
  {
    base = inBase;
  }

  /** Obtains the name of the base class object. Applies only to
   *  relation objects.
   *  @return the name of the base class. */
  public String getBase()
  {
    return base;
  }

  /** Sets the icon to be used by the objects of this class.
   *  Applies only to entity objects.
   *  @param inIcon the index of the icon to be used. */
  public void setIcon(int inIcon)
  {
    icon = inIcon;
  }

  /** Obtains the icon being used by the object. Applies only to entity objects.
   *  @return the index of the icon being used. */
  public int getIcon()
  {
    return icon;
  }

  /** Sets the base class for the objects where an instance of this
   *  class can start. Applies only to relation objects.
   *
   *  @param inFromBase the name of the base class.
   */
  public void setFromBase(String inFromBase)
  {
    fromBase = inFromBase;
  }

  /** Obtains the name of the base class for the start of a relation object.
   *  @return the name of the base class as a String. */
  public String getFromBase()
  {
    return fromBase;
  }

  /** Sets the base class of the destination object in a relation object.
   *  @param inToBase the name of the base class for the destination. */
  public void setToBase(String inToBase)
  {
    toBase = inToBase;
  }

  /** Obtains the base class of the destination object in a relation object.
   *  @return the name of the base class for the destination object. */
  public String getToBase()
  {
    return toBase;
  }

  /** Obtains the style of the base of the line (relation objects only).
   *  @return the index of the style. */
  public int getBaseStyle()
  {
    return baseStyle;
  }

  /** Sets the style of the base of the line (relation objects only).
    *  @param inBaseStyle the bew style. */
  public void setBaseStyle(int  inBaseStyle)
  {
    baseStyle = inBaseStyle;
  }

  /** Obtains the style of the line for a relation object.
   *  @return the line style. */
  public int getLineStyle()
  {
    return lineStyle;
  }

  /** Sets the line style for a relation class.
    *  @param inLineStyle the new style for the line style. */
  public void setLineStyle(int  inLineStyle)
  {
    lineStyle = inLineStyle;
  }

  /** Gets the line width used to draw a given relation class.
   *  @return the width of the line. */
  public int getLineWidth()
  {
    return lineWidth;
  }

  /** Sets the line width to be used to draw a relation class.
   *  @param inLineWidth the new line width. */
  public void setLineWidth(int  inLineWidth)
  {
    lineWidth = inLineWidth;
  }

  /** Obtains the style used to draw the end of a relation line.
   *  @return the style used to draw the end of the line. This style
   *  can be none, an arrow, a box, or a circle. */
  public int getEndStyle()
  {
    return endStyle;
  }

  /** Sets the style used to draw the end of a relation line.
   *  @param inEndStyle the new style of the line.
   */
  public void setEndStyle(int inEndStyle)
  {
    endStyle = inEndStyle;
  }

  /** Returns the number of attributes for this snippet.
   *  @return the number of attributes.
   */
  public int getAttributeCount()
  {
    return attributes.size();
  }

  /** Returns a particular attribute.
   *  @param inIndex the index of the attribute of interest.
   *  @return a reference to the attribute if the index is within limits, NULL
   *  otherwise.
   */
  public TclAttribute getAttribute(int inIndex)
  {
    if (inIndex >= 0 && inIndex < attributes.size())
      return attributes.get(inIndex);
    else
      return null;
  }

  /** Converts the snippet to a text representation.
   *  @return a string with the representation of the snippet. */
  public String toString()
  {
    String s;
    int i;

    s = getName();
    for (i = 0; i < attributes.size(); i++)
      s = s + "  " + attributes.get(i).toString() + "\n";
    s = s + "begin\n";
    for (i = 0; i < patterns.size(); i++)
      s = s + "  " + patterns.get(i).toString() + "\n";
    s = s + "end\n";
    return s;
  }

  /** Instantiates an object by copying the default values of the
   *  attributes to it.
   *
   *  @param o a reference to the object to be initialized.
   */
  public void instantiateNSObject(NSObject o)
  {
    int i;

    for (i = 0; i < attributes.size(); i++) {
      o.setAttribute(i, attributes.get(i).defaultValue);
    }
  }

  /** Converts an object to Tcl.
   *  @param w a reference to the world object. This reference is
   *           provided in case the object requires context information
   *           to resolve its translation.
   *  @param o the object to be rendered to Tcl.
   *  @param sep a character that separates the inline expressions in
   *             the patterns.
   *  @return the Tcl code as a String.
   */
  public String toTcl(NSWorld w, NSObject o, char sep)
  {
    String preamble = "", s, epilogue = "", aname;
    TclPattern p;
    NSRelation ro;
    NSArray a;
    int i, i2, asize;

    // Get the preamble and epilogue for the snippet.
    if (o.getArrayIndex() >= 0) {
      a = w.getArray(o.getArrayIndex());
      aname = a.name;
      asize = a.elements;
      preamble = "for {set " + aname + " 0} {$" + aname + "<" + Integer.toString(asize) + "} {incr " + aname + "} {\n";
      epilogue = "}\n";
    } else {
      if (isRelation) {
        ro = (NSRelation) o;
        i = ro.getFrom().getArrayIndex();
        i2 = ro.getTo().getArrayIndex();
        if (i >= 0 || i2 >= 0) {
          if (i == i2) {
            aname = w.getArray(i).name;
            asize = w.getArray(i).elements;
            preamble = "for {set " + aname + " 0} {$" + aname + "<" + Integer.toString(asize) + "} {incr " + aname + "} {\n";
            epilogue = "}\n";
          } else {
            if (i >= 0) {
              aname = w.getArray(i).name;
              asize = w.getArray(i).elements;
              preamble = "for {set " + aname + " 0} {$" + aname + "<" + Integer.toString(asize) + "} {incr " + aname + "} {\n";
              epilogue = "}\n";
            }
            if (i2 >= 0) {
              aname = w.getArray(i2).name;
              asize = w.getArray(i2).elements;
              preamble = preamble + "  for {set " + aname + " 0} {$" + aname + "<" + Integer.toString(asize) + "} {incr " + aname + "} {\n";
              epilogue = epilogue + "  }\n";
            }
          }
        }
      }
    }

    String sApp;
    // Now pattern substitution
    s = "";
    for (i = 0; i < patterns.size(); i++) {
      p = patterns.get(i);
      if (p.isConditional) {
        if (p.attributeValue.equals(valueOf(w, o, p.attribute)))
          sApp = patternToTcl(p.pattern, w, o, sep);
        else
          sApp = patternToTcl(p.alternativePattern, w, o, sep);
      } else {
        sApp = patternToTcl(p.pattern, w, o, sep);
      }
      if (!sApp.trim().equals(""))
        s = s + sApp + "\n";
    }

    if (s.trim().equals(""))
      return "";
    else
      return (preamble + s + epilogue).trim();
  }

  /** Converts a pattern to Tcl code.
   *  @param pattern a string with the pattern.
   *  @param w a reference to the simulation model.
   *  @param o a reference to the object being translated to Tcl.
   *  @param sep a character that marks the inline tags in the patterns.
   *  @return a string with Tcl code corresponding to the pattern. */
  String patternToTcl(String pattern, NSWorld w, NSObject o, char sep)
  {
    int i, iLast = 0;
    String sNew = "";

    while ((i = pattern.indexOf(sep, iLast)) >= 0) {
      sNew = sNew + pattern.substring(iLast, i);
      iLast = pattern.indexOf(sep, i + 1);
      if (iLast < 0)
        return Messages.tr("bad_formed_pattern");
      sNew = sNew + valueOf(w, o, pattern.substring(i + 1, iLast));
      iLast++;
    }
    if (iLast < pattern.length())
      sNew = sNew + pattern.substring(iLast);
    return sNew;
  }

  /** Finds the value of a given attribute for a particular object.
   *  @param w a reference to the simulation model.
   *  @param o a reference to the model being translated.
   *  @param attrName the name of the attribute whose value is wanted.
   *  @return the value of the attribute as a String. */
  String valueOf(NSWorld w, NSObject o, String attrName)
  {
    int i;

    // Special field values
    i = attrName.indexOf("env");
    if (i >= 0) {
      return w.getEnvironment().getSnippet().
             valueOf(w, w.getEnvironment(), attrName.substring(i + 4));
    }
    if (attrName.equals("from"))
      return arrayedName(((NSRelation) o).getFrom(), w);
    if (attrName.equals("to"))
      return arrayedName(((NSRelation) o).getTo(), w);

    //  It's an array variable
    if (attrName.equals("name")) {
      return arrayedName(o, w);
    }

    // Variable attributes
    for (i = 0; i < attributes.size(); i++) {
      if (attributes.get(i).name.equals(attrName))
        return o.getAttribute(i);
    }

    return attrName + ".NotFound";
  }

  /** Constructs the name for an object that is indexed by an array.
    *  @param o a reference to the object.
    *  @param w a reference to the simulation model.
    *  @return the name of the object as a String. */
  public String arrayedName(NSObject o, NSWorld w)
  {
    if (o.getArrayIndex() >= 0)
      return o.getName() + "($" + w.getArray(o.getArrayIndex()).name + ")";
    else
      return o.getName();
  }
}
