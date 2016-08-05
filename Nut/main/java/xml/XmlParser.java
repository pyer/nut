package nut.xml;

import java.io.IOException;
import java.io.StringReader;

/**
 * Absolutely minimal implementation of XML parsing. Encoding handling done with XmlReader
 * https://www.w3.org/TR/2008/REC-xml-20081126/
 */

public class XmlParser
{
    // ----------------------------------------------------------------------------
    // global parser state
    protected String location;
    protected int lineNumber;
    protected int columnNumber;
    protected boolean reachedEnd;
    protected boolean emptyElementTag;

    protected boolean seenEndTag;

    // element stack
    protected static final int MAX_DEPTH = 128;
    protected int depth;

    protected String elName[];
    protected String text;

    // input buffer management
    protected static final int READ_CHUNK_SIZE = 8*1024; //max data chars in one read() call
    protected StringReader reader;

    protected static final int BUF_SIZE = 1024;
    protected char buf[] = new char[BUF_SIZE];
    protected int pos;

    // protected String xmlDeclVersion;
    // protected String xmlDeclContent;

    /**
     * Class constructor
     */
    public XmlParser( StringReader sReader ) {
        reader = sReader;
        location = null;
        lineNumber = 1;
        columnNumber = 0;
        reachedEnd = false;
        emptyElementTag = false;

        depth = 0;
        elName = new String[MAX_DEPTH];
        elName[0] = "";
        text = "";

        pos = 0;
        //xmlDeclVersion = null;
        //xmlDeclContent = null;
    }

    /* ************************************************************************* */
    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public boolean endOfDocument() {
        return reachedEnd || depth<1;
    }

    public boolean endOfTag() {
        return seenEndTag;
    }

    public String getName() {
      return elName[ depth ];
    }

    public String getText() {
      return text;
    }

    /* ************************************************************************* */

    public void parseXmlDecl() throws XmlParserException, IOException {
      // [2] prolog: ::= XMLDecl? Misc* (doctypedecl Misc*)? and look for [39] element
      char ch = more();
      // bootstrap parsing with getting first character input!
      // deal with BOM
      // detect BOM and crop it (Unicode int Order Mark)
      if(ch == '\uFFFE') {
        throw new XmlParserException(
                  "first character in input was UNICODE noncharacter (0xFFFE)"+
                  "- input requires int swapping", this, null);
      }
      if(ch == '\uFEFF') {
        // skipping UNICODE int Order Mark (so called BOM)
        ch = more();
      }
      while(!reachedEnd) {
        pos = 0;
        // deal with Misc
        // [27] Misc ::= Comment | PI | S
        // deal with docdecl --> mark it!
        // else parseStartTag seen <[^/]
        if(ch == '<') {
          ch = more();
          if(ch == '?') {
            parsePI();
            break;
          } else {
            throw new XmlParserException(
                      "expected XML declaration and not <"+printable(ch), this, null);
          }
        } else {
          throw new XmlParserException(
                    "expected XML declaration and not "+printable(ch), this, null);
        }
      }
    }

    /**
    * return true if the parser is at the start of a tag
    * return false at the end of a tag or document
    */
    public boolean nextTag() throws XmlParserException, IOException {
      char ch;
      emptyElementTag = false;
      seenEndTag = false;
      text = "";

      //println("nextTag entry");
      pos = 0;
      while(!reachedEnd) {
            ch = more();
            if(ch == '<') {
                pos = 0;
            } else if(ch == '!') {
                  ch = more();
                  if(ch == '-') {
                    parseComment();
                  }
            } else if(isSpace(ch)) {
                continue;
            } else if(ch == '/') {
                parseEndTag();
                //System.out.println("\nnextTag end: </"+getName()+">");
                return false;
            } else if(isNameChar(ch)){
                parseStartTag();
                parseText();
                //System.out.println("\nnextTag start: <"+getName()+">  text: ["+text+"]");
                return true;
            } else if(!reachedEnd) {
                throw new XmlParserException(
                        "unexpected character in tag: "+printable(ch), this, null);
            }
      }
      if(depth>0) {
        throw new XmlParserException(
                "unexpected end of file (depth "+depth+")", this, null);
      }
      //System.out.println("nextTag end of document");
      return false;
    }

    /* ************************************************************************* */
    private void parseStartTag() throws XmlParserException, IOException {
        //ASSUMPTION ch is past <T
        char ch;
        do {
            ch = more();
        } while(isNameChar(ch) && !reachedEnd);

        depth++;
        elName[ depth ] = new String(buf, 0, pos-1);
        // seek end of tag, '>' or '/>'
        do {
            if(ch == '>') {
              break;
            } else if(ch == '/') {
                if(emptyElementTag)
                    throw new XmlParserException("repeated / in tag declaration", this, null);
                ch = more();
                if(ch != '>')
                    throw new XmlParserException("expected > to end empty tag, not "+printable(ch), this, null);
                depth--;
                emptyElementTag = true;
                seenEndTag = true;
                break;
            } else if(ch == '!') {
                  ch = more();
                  if(ch == '-') {
                    parseComment();
                    break;
                  } else {
                    throw new XmlParserException(
                              "unexpected character in markup "+printable(ch), this, null);
                  }
            } else if(isSpace(ch)) {
              pos = 0;
            } else if(!isNameChar(ch)) {
                throw new XmlParserException(
                    "unexpected character in tag "+printable(ch), this, null);
            }
            ch = more();
        } while(!reachedEnd);
    }

    private void parseEndTag() throws XmlParserException, IOException {
        //ASSUMPTION ch is past "</"
        // [42] ETag ::=  '</' Name S? '>'
        char ch;
        //System.out.println("parseEndTag </" + elName[ depth ] + "> depth="+depth);
        pos = 0;
        do {
            ch = more();
        } while(!reachedEnd && isNameChar(ch));
        if(ch != '>') {
            throw new XmlParserException(
                "expected > to finish end tag not "+printable(ch)
                    +" from line "+elName[depth], this, null);
        }
        String tag = new String(buf, 0, pos-1);
        if( !tag.equals(elName[depth])) {
            throw new XmlParserException(
                "end tag name </"+tag+"> must be the same as start tag <"+elName[depth]+">",
                this, null);
        }
        depth--;
        seenEndTag = true;
    }

    private void parseText() throws XmlParserException, IOException {
      pos = 0;
      if(emptyElementTag) {
        text = "";
      } else {
        // scan until it hits <
        while(!reachedEnd && more() != '<');
        text = new String(buf, 0, pos-1);
      }
      //System.out.println("parseText " + text);
    }

    protected void parseComment() throws XmlParserException, IOException {
        // implements XML 1.0 Section 2.5 Comments
        //ASSUMPTION: seen <!-
        char ch = more();
        //System.out.println("parseComment");
        if(ch != '-') throw new XmlParserException(
                "expected <!-- for comment start", this, null);

        pos = 0;
        final int curLine = lineNumber;
        final int curColumn = columnNumber;
        boolean seenDash = false;
        boolean seenDashDash = false;
        while(true) {
                // scan until it hits -->
                ch = more();
                if(reachedEnd) {
                    // detect EOF and create meaningful error ...
                    throw new XmlParserException(
                        "comment started on line "+curLine+" and column "+curColumn+" was not closed",
                        this, null);
                }
                if(seenDashDash && ch != '>') {
                    throw new XmlParserException(
                        "in comment after two dashes (--) next character must be >"
                            +" not "+printable(ch), this, null);
                }
                if(ch == '-') {
                    if(!seenDash) {
                        seenDash = true;
                    } else {
                        seenDashDash = true;
                        seenDash = false;
                    }
                } else if(ch == '>') {
                    if(seenDashDash) {
                        break;  // found end sequence!!!!
                    } else {
                        seenDashDash = false;
                    }
                    seenDash = false;
                } else {
                    seenDash = false;
                }
        }
        //System.out.println("End of comment");
    }

    protected void parsePI() throws XmlParserException, IOException {
        // implements XML 1.0 Section 2.6 Processing Instructions
        // <?xml version="1.0" encoding="UTF-8"?>

        // [16] PI ::= '<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
        // [17] PITarget         ::=    Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
        //ASSUMPTION: seen <?
        boolean seenQ = false;
        pos = 0;
        /*            char ch = more();
            if(isSpace(ch)) {
                throw new XmlParserException(
                    "processing instruction PITarget must be exactly after <? and not white space character",
                    this, null);
            }
            */
        while(!reachedEnd) {
            // scan until it hits ?>
            char ch = more();
            if(ch == '?') {
                seenQ = true;
            } else if(ch == '>' && seenQ) {
                break;  // found end sequence!!!!
            } else {
                // [17] PITarget ::= Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
                if(pos == 3) {
                    if( (buf[0] != 'x' && buf[0] != 'X')
                     || (buf[1] != 'm' && buf[1] != 'M')
                     || (buf[2] != 'l' && buf[2] != 'L') ) {
                        throw new XmlParserException(
                                  "processing instruction must begin with xml or XML",
                                  this, null);
                    // TO DO: check version and encoding
                    /*
                                if(piTargetStart > 3) {  //<?xml is allowed as first characters in input ...
                                    throw new XmlParserException(
                                        "processing instruction can not have PITarget with reserveld xml name",
                                        this, null);
                                } else {
                                    if(buf[piTargetStart] != 'x'
                                           && buf[piTargetStart+1] != 'm'
                                           && buf[piTargetStart+2] != 'l')
                                    {
                                        throw new XmlParserException(
                                            "XMLDecl must have xml name in lowercase",
                                            this, null);
                                    }
                                }
                                parseXmlDecl(ch);
                                final int off = piTargetStart - bufAbsoluteStart + 3;
                                final int len = pos - 2 - off;
                                xmlDeclContent = new String(buf, off, len);
                                return;
                                */
                    }
                }
            }
        }
    }


    //    protected final static char[] VERSION = {'v','e','r','s','i','o','n'};
    //    protected final static char[] NCODING = {'n','c','o','d','i','n','g'};

    // protected final static char[] VERSION = "version".toCharArray();
    // protected final static char[] NCODING = "ncoding".toCharArray();


    protected char more() throws IOException {
      final int ret = reader.read();
      //System.out.print(" ("+(char)ret+") ");
      if(ret == -1) {
        reachedEnd = true;
      } else {
        buf[pos++]=(char)ret;
        //line/columnNumber
        if(ret == '\n') {
          ++lineNumber;
          columnNumber = 1;
        } else {
          ++columnNumber;
        }
      }
      return (char)ret;
    }

    //private final static boolean isNameChar(char ch) {
    protected boolean isNameChar(char ch) {
        return ( (ch >= 'A' && ch <= 'Z')
              || (ch >= 'a' && ch <= 'z') );
    }

    protected boolean isSpace(char ch) {
        return (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t');
    }

    //protected char printable(char ch) { return ch; }
    protected String printable(char ch) {
        if(ch == '\n') {
            return "\\n";
        } else if(ch == '\r') {
            return "\\r";
        } else if(ch == '\t') {
            return "\\t";
        } else if(ch == '\'') {
            return "\\'";
        } if(ch > 127 || ch < 32) {
            return "\\u"+Integer.toHexString((int)ch);
        }
        return ""+ch;
    }

    protected String printable(String s) {
        if(s == null) return null;
        final int sLen = s.length();
        StringBuffer buf = new StringBuffer(sLen + 10);
        for(int i = 0; i < sLen; ++i) {
            buf.append(printable(s.charAt(i)));
        }
        return buf.toString();
    }

}
