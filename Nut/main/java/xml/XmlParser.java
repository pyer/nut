package nut.xml;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Absolutely minimal implementation of XMLPULL V1 API. Encoding handling done with XmlReader
 */

public class XmlParser
{

/**
 * XML Pull Parser is an interface that defines parsing functionality provided
 * in <a href="http://www.xmlpull.org/">XMLPULL V1 API</a> (visit this website to
 * learn more about API and its implementations).
 *
 */

    // ----------------------------------------------------------------------------
    // global parser state
    protected String location;
    protected int lineNumber;
    protected int columnNumber;
    protected boolean seenRoot;
    protected boolean reachedEnd;
    protected boolean emptyElementTag;

    protected boolean seenStartTag;
    protected boolean seenEndTag;

    // element stack
    protected static final int MAX_DEPTH = 128;
    protected int depth;

    protected String elName[];

    // input buffer management
    protected static final int READ_CHUNK_SIZE = 8*1024; //max data chars in one read() call
    protected StringReader reader;

    protected static final int BUF_SIZE = 1024;
    protected char buf[] = new char[BUF_SIZE];
    protected int pos;

    protected String text;

    protected String xmlDeclVersion;
    protected String xmlDeclContent;

    /**
     * Class constructor
     */
    public XmlParser( StringReader sReader ) {
        reader = sReader;
        location = null;
        lineNumber = 1;
        columnNumber = 0;
        seenRoot = false;
        reachedEnd = false;
        emptyElementTag = false;

        depth = 0;
        elName = new String[MAX_DEPTH];
        elName[0] = "";
        text = "";

        pos = 0;
        xmlDeclVersion = null;
        xmlDeclContent = null;
    }

    /* ************************************************************************* */
    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public boolean endOfDocument() {
        return reachedEnd;
    }

    public boolean startTag() {
        return seenStartTag;
    }

    public boolean endOfTag() {
        return seenEndTag;
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
      int current = depth;
      seenStartTag = false;
      seenEndTag = false;
      text = "";

      //System.out.println("nextTag entry");
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
            } else if(isS(ch)) {
                continue;
            } else if(ch == '/') {
                parseEndTag();
                seenEndTag = true;
                //System.out.println("\nnextTag end: </"+getName()+">");
                depth--;
                return false;
            } else if(isNameChar(ch)){
                depth++;
                parseStartTag();
                parseText();
                seenStartTag = true;
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

    public String getName() {
      return elName[ depth ];
    }

    public String getText() {
      return text;
    }

    /* ************************************************************************* */
    private void parseStartTag() throws XmlParserException, IOException {
        //ASSUMPTION ch is past <T
        emptyElementTag = false;
        char ch;
        do {
            ch = more();
            //System.out.print(ch);
        } while(isNameChar(ch) && !reachedEnd);

        elName[ depth ] = new String(buf, 0, pos-1);
        //System.out.println("parseStartTag <" + elName[ depth ] + "> depth="+depth);
        // seek end of tag, '>' or '/>'
        do {
            if(ch == '>') {
              break;
        /*    } else if(ch == '/') {
                if(emptyElementTag) throw new XmlParserException(
                        "repeated / in tag declaration", this, null);
                emptyElementTag = true;
                ch = more();
                if(ch != '>') throw new XmlParserException(
                        "expected > to end empty tag not "+printable(ch), this, null);
                break;
          */  } else if(ch == '!') {
                  ch = more();
                  if(ch == '-') {
                    parseComment();
                    break;
                  } else {
                    throw new XmlParserException(
                              "unexpected character in markup "+printable(ch), this, null);
                  }
            } else if(isS(ch)) {
              pos = 0;
            } else if(!isNameStartChar(ch)) {
                throw new XmlParserException(
                    "start tag unexpected character "+printable(ch), this, null);
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
        String startTag = elName[depth];
        String endTag = new String(buf, 0, pos-1);
        if( !endTag.equals(startTag)) {
            throw new XmlParserException(
                "end tag name </"+endTag+"> must be the same as start tag <"+startTag+">",
                this, null);
        }
    }

    private void parseText() throws XmlParserException, IOException {
      pos = 0;
      // scan until it hits <
      while(!reachedEnd && more() != '<');
      text = new String(buf, 0, pos-1);
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
        try {
            boolean seenDash = false;
            boolean seenDashDash = false;
            while(true) {
                // scan until it hits -->
                ch = more();
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
        } catch(EOFException ex) {
            // detect EOF and create meaningful error ...
            throw new XmlParserException(
                "comment started on line "+curLine+" and column "+curColumn+" was not closed",
                this, ex);
        }
        //System.out.println("End of comment");
    }

    protected void parsePI() throws XmlParserException, IOException {
        // implements XML 1.0 Section 2.6 Processing Instructions

        // [16] PI ::= '<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
        // [17] PITarget         ::=    Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
        //ASSUMPTION: seen <?
        final int curLine = lineNumber;
        final int curColumn = columnNumber;
        boolean seenQ = false;
        pos = 0;
        /*            char ch = more();
            if(isS(ch)) {
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
                    if(  (buf[0] == 'x' || buf[0] == 'X')
                        && (buf[1] == 'm' || buf[1] == 'M')
                        && (buf[2] == 'l' || buf[2] == 'L') )
                    {
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
                    } else {
                        throw new XmlParserException(
                                  "processing instruction must begin with xml or XML",
                                  this, null);
                    }
                }
            }
        }
    }


    //    protected final static char[] VERSION = {'v','e','r','s','i','o','n'};
    //    protected final static char[] NCODING = {'n','c','o','d','i','n','g'};

    protected final static char[] VERSION = "version".toCharArray();
    protected final static char[] NCODING = "ncoding".toCharArray();


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

    // nameStart / name lookup tables based on XML 1.1 http://www.w3.org/TR/2001/WD-xml11-20011213/
    protected static final int LOOKUP_MAX = 0x400;
    protected static final char LOOKUP_MAX_CHAR = (char)LOOKUP_MAX;
    protected static boolean lookupNameStartChar[] = new boolean[ LOOKUP_MAX ];
    protected static boolean lookupNameChar[] = new boolean[ LOOKUP_MAX ];

    private static final void setName(char ch) {
      lookupNameChar[ ch ] = true;
    }

    private static final void setNameStart(char ch) {
      lookupNameStartChar[ ch ] = true; setName(ch);
    }

    static {
        setNameStart(':');
        for (char ch = 'A'; ch <= 'Z'; ++ch) setNameStart(ch);
        setNameStart('_');
        for (char ch = 'a'; ch <= 'z'; ++ch) setNameStart(ch);
        for (char ch = '\u00c0'; ch <= '\u02FF'; ++ch) setNameStart(ch);
        for (char ch = '\u0370'; ch <= '\u037d'; ++ch) setNameStart(ch);
        for (char ch = '\u037f'; ch < '\u0400'; ++ch) setNameStart(ch);

        setName('-');
        setName('.');
        for (char ch = '0'; ch <= '9'; ++ch) setName(ch);
        setName('\u00b7');
        for (char ch = '\u0300'; ch <= '\u036f'; ++ch) setName(ch);
    }


    //private final static boolean isNameStartChar(char ch) {
    protected boolean isNameStartChar(char ch) {
        return (ch < LOOKUP_MAX_CHAR && lookupNameStartChar[ ch ])
            || (ch >= LOOKUP_MAX_CHAR && ch <= '\u2027')
            || (ch >= '\u202A' &&  ch <= '\u218F')
            || (ch >= '\u2800' &&  ch <= '\uFFEF')
            ;
    }

    //private final static boolean isNameChar(char ch) {
    protected boolean isNameChar(char ch) {
        return (ch < LOOKUP_MAX_CHAR && lookupNameChar[ ch ])
            || (ch >= LOOKUP_MAX_CHAR && ch <= '\u2027')
            || (ch >= '\u202A' &&  ch <= '\u218F')
            || (ch >= '\u2800' &&  ch <= '\uFFEF')
            ;
    }

    protected boolean isS(char ch) {
        return (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t');
        // || (supportXml11 && (ch == '\u0085' || ch == '\u2028');
    }

    //protected boolean isChar(char ch) { return (ch < '\uD800' || ch > '\uDFFF')
    //  ch != '\u0000' ch < '\uFFFE'


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
        s = buf.toString();
        return s;
    }

}
