package nut.xml.pull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Absolutely minimal implementation of XMLPULL V1 API. Encoding handling done with XmlReader
 */

public class XmlPullParser
{

/**
 * XML Pull Parser is an interface that defines parsing functionality provided
 * in <a href="http://www.xmlpull.org/">XMLPULL V1 API</a> (visit this website to
 * learn more about API and its implementations).
 *
 * <p>There are two key methods: next() and nextToken(). While next() provides
 * access to high level parsing events, nextToken() allows access to lower
 * level tokens.
 *
 * <p>The current event state of the parser
 * can be determined by calling the
 * <a href="#getEventType()">getEventType()</a> method.
 * Initially, the parser is in the <a href="#START_DOCUMENT">START_DOCUMENT</a>
 * state.
 *
 * <p>The method <a href="#next()">next()</a> advances the parser to the
 * next event. The int value returned from next determines the current parser
 * state and is identical to the value returned from following calls to
 * getEventType ().
 *
 * <p>The following event types are seen by next()<dl>
 * <dt><a href="#START_TAG">START_TAG</a><dd> An XML start tag was read.
 * <dt><a href="#TEXT">TEXT</a><dd> Text content was read;
 * the text content can be retrieved using the getText() method.
 *  (when in validating mode next() will not report ignorable whitespaces, use nextToken() instead)
 * <dt><a href="#END_TAG">END_TAG</a><dd> An end tag was read
 * <dt><a href="#END_DOCUMENT">END_DOCUMENT</a><dd> No more events are available
 * </dl>
 *
 * <p>after first next() or nextToken() (or any other next*() method)
 * is called user application can obtain
 * XML version, standalone and encoding from XML declaration
 * in following ways:<ul>
 * <li><b>version</b>:
 * <li><b>encoding</b>: obtained from getInputEncoding()
 *       null if stream had unknown encoding (not set in setInputStream)
 *           and it was not declared in XMLDecl
 * </ul>
 *
 * A minimal example for using this API may look as follows:
 * <pre>
 * import java.io.IOException;
 * import java.io.StringReader;
 *
 * import org.xmlpull.v1.XmlPullParser;
 * import org.xmlpull.v1.<a href="XmlPullParserException.html">XmlPullParserException.html</a>;
 * import org.xmlpull.v1.<a href="XmlPullParserFactory.html">XmlPullParserFactory</a>;
 *
 * public class SimpleXmlPullApp
 * {
 *
 *     public static void main (String args[])
 *         throws XmlPullParserException, IOException
 *     {
 *         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
 *         factory.setNamespaceAware(true);
 *         XmlPullParser xpp = factory.newPullParser();
 *
 *         xpp.<a href="#setInput">setInput</a>( new StringReader ( "&lt;foo>Hello World!&lt;/foo>" ) );
 *         int eventType = xpp.getEventType();
 *         while (eventType != xpp.END_DOCUMENT) {
 *          if(eventType == xpp.START_DOCUMENT) {
 *              System.out.println("Start document");
 *          } else if(eventType == xpp.END_DOCUMENT) {
 *              System.out.println("End document");
 *          } else if(eventType == xpp.START_TAG) {
 *              System.out.println("Start tag "+xpp.<a href="#getName()">getName()</a>);
 *          } else if(eventType == xpp.END_TAG) {
 *              System.out.println("End tag "+xpp.getName());
 *          } else if(eventType == xpp.TEXT) {
 *              System.out.println("Text "+xpp.<a href="#getText()">getText()</a>);
 *          }
 *          eventType = xpp.next();
 *         }
 *     }
 * }
 * </pre>
 *
 * <p>The above example will generate the following output:
 * <pre>
 * Start document
 * Start tag foo
 * Text Hello World!
 * End tag foo
 * </pre>
 *
 * <p>For more details on API usage, please refer to the
 * quick Introduction available at <a href="http://www.xmlpull.org">http://www.xmlpull.org</a>
 *
 * @see #getName
 * @see #getText
 * @see #next
 * @see #nextToken
 * @see #setInput
 * @see #START_DOCUMENT
 * @see #START_TAG
 * @see #TEXT
 * @see #END_TAG
 * @see #END_DOCUMENT
 *
 * @author <a href="http://www-ai.cs.uni-dortmund.de/PERSONAL/haustein.html">Stefan Haustein</a>
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */

    // ----------------------------------------------------------------------------
    // EVENT TYPES as reported by next()

    /**
     * Signalize that parser is at the very beginning of the document
     * and nothing was read yet.
     * This event type can only be observed by calling getEvent()
     * before the first call to next(), nextToken, or nextTag()</a>).
     *
     * @see #next
     * @see #nextToken
     */
    public static int START_DOCUMENT = 0;

    /**
     * Logical end of the xml document. Returned from getEventType, next()
     * and nextToken()
     * when the end of the input document has been reached.
     * <p><strong>NOTE:</strong> calling again
     * <a href="#next()">next()</a> or <a href="#nextToken()">nextToken()</a>
     * will result in exception being thrown.
     *
     * @see #next
     * @see #nextToken
     */
    public static int END_DOCUMENT = 1;

    /**
     * Returned from getEventType(),
     * <a href="#next()">next()</a>, <a href="#nextToken()">nextToken()</a> when
     * a start tag was read.
     * The name of start tag is available from getName()
     * @see #next
     * @see #nextToken
     * @see #getName
     */
    public static int START_TAG = 2;

    /**
     * Returned from getEventType(), <a href="#next()">next()</a>, or
     * <a href="#nextToken()">nextToken()</a> when an end tag was read.
     * The name of start tag is available from getName()
     * @see #next
     * @see #nextToken
     * @see #getName
     */
    public static int END_TAG = 3;


    /**
     * Character data was read and will is available by calling getText().
     * <p><strong>Please note:</strong> <a href="#next()">next()</a> will
     * accumulate multiple
     * events into one TEXT event, skipping IGNORABLE_WHITESPACE,
     * PROCESSING_INSTRUCTION and COMMENT events,
     * In contrast, <a href="#nextToken()">nextToken()</a> will stop reading
     * text when any other event is observed.
     * Also, when the state was reached by calling next(), the text value will
     * be normalized, whereas getText() will
     * return unnormalized content in the case of nextToken(). This allows
     * an exact roundtrip without changing line ends when examining low
     * level events, whereas for high level applications the text is
     * normalized appropriately.
     *
     * @see #next
     * @see #nextToken
     * @see #getText
     */
    public static int TEXT = 4;

    // ----------------------------------------------------------------------------
    // additional events exposed by lower level nextToken()

    /**
     * A CDATA sections was just read;
     * this token is available only from calls to <a href="#nextToken()">nextToken()</a>.
     * A call to next() will accumulate various text events into a single event
     * of type TEXT. The text contained in the CDATA section is available
     * by calling getText().
     *
     * @see #nextToken
     * @see #getText
     */
    int CDSECT = 5;

    /**
     * An entity reference was just read;
     * this token is available from <a href="#nextToken()">nextToken()</a>
     * only. The entity name is available by calling getName(). If available,
     * the replacement text can be obtained by calling getTextt(); otherwise,
     * the user is responsible for resolving the entity reference.
     * This event type is never returned from next(); next() will
     * accumulate the replacement text and other text
     * events to a single TEXT event.
     *
     * @see #nextToken
     * @see #getText
     */
    int ENTITY_REF = 6;

    /**
     * Ignorable whitespace was just read.
     * This token is available only from <a href="#nextToken()">nextToken()</a>).
     * For non-validating
     * parsers, this event is only reported by nextToken() when outside
     * the root element.
     * Validating parsers may be able to detect ignorable whitespace at
     * other locations.
     * The ignorable whitespace string is available by calling getText()
     *
     * <p><strong>NOTE:</strong> this is different from calling the
     *  isWhitespace() method, since text content
     *  may be whitespace but not ignorable.
     *
     * Ignorable whitespace is skipped by next() automatically; this event
     * type is never returned from next().
     *
     * @see #nextToken
     * @see #getText
     */
    int IGNORABLE_WHITESPACE = 7;

    /**
     * An XML processing instruction declaration was just read. This
     * event type is available only via <a href="#nextToken()">nextToken()</a>.
     * getText() will return text that is inside the processing instruction.
     * Calls to next() will skip processing instructions automatically.
     * @see #nextToken
     * @see #getText
     */
    int PROCESSING_INSTRUCTION = 8;

    /**
     * An XML comment was just read. This event type is this token is
     * available via <a href="#nextToken()">nextToken()</a> only;
     * calls to next() will skip comments automatically.
     * The content of the comment can be accessed using the getText()
     * method.
     *
     * @see #nextToken
     * @see #getText
     */
    int COMMENT = 9;

    /**
     * An XML document type declaration was just read. This token is
     * available from <a href="#nextToken()">nextToken()</a> only.
     * The unparsed text inside the doctype is available via
     * the getText() method.
     *
     * @see #nextToken
     * @see #getText
     */
    int DOCDECL = 10;

    /**
     * This array can be used to convert the event type integer constants
     * such as START_TAG or TEXT to
     * to a string. For example, the value of TYPES[START_TAG] is
     * the string "START_TAG".
     *
     * This array is intended for diagnostic output only. Relying
     * on the contents of the array may be dangerous since malicious
     * applications may alter the array, although it is final, due
     * to limitations of the Java language.
     */
    String [] TYPES = {
        "START_DOCUMENT",
            "END_DOCUMENT",
            "START_TAG",
            "END_TAG",
            "TEXT",
            "CDSECT",
            "ENTITY_REF",
            "IGNORABLE_WHITESPACE",
            "PROCESSING_INSTRUCTION",
            "COMMENT",
            "DOCDECL"
    };


    // ----------------------------------------------------------------------------
    private static final boolean TRACE_SIZING = false;

    // NOTE: features are not resetable and typically defaults to false ...
    protected boolean roundtripSupported;

    // global parser state
    protected String location;
    protected int lineNumber;
    protected int columnNumber;
    protected boolean seenRoot;
    protected boolean reachedEnd;
    protected int eventType;
    protected boolean emptyElementTag;
    // element stack
    protected int depth;
    protected char[] elRawName[];
    protected int elRawNameEnd[];
    protected int elRawNameLine[];

    protected String elName[];

    /**
     * Make sure that we have enough space to keep element stack if passed size.
     * It will always create one additional slot then current depth
     */
    protected void ensureElementsCapacity() {
        final int elStackSize = elName != null ? elName.length : 0;
        if( (depth + 1) >= elStackSize) {
            // we add at least one extra slot ...
            final int newSize = (depth >= 7 ? 2 * depth : 8) + 2; // = lucky 7 + 1 //25
            if(TRACE_SIZING) {
                System.err.println("TRACE_SIZING elStackSize "+elStackSize+" ==> "+newSize);
            }
            final boolean needsCopying = elStackSize > 0;
            String[] arr = null;
            // resue arr local variable slot
            arr = new String[newSize];
            if(needsCopying) System.arraycopy(elName, 0, arr, 0, elStackSize);
            elName = arr;

            //TODO: avoid using element raw name ...
            int[] iarr = new int[newSize];
            if(needsCopying) {
                System.arraycopy(elRawNameEnd, 0, iarr, 0, elStackSize);
            }
            elRawNameEnd = iarr;

            iarr = new int[newSize];
            if(needsCopying) {
                System.arraycopy(elRawNameLine, 0, iarr, 0, elStackSize);
            }
            elRawNameLine = iarr;

            final char[][] carr = new char[newSize][];
            if(needsCopying) {
                System.arraycopy(elRawName, 0, carr, 0, elStackSize);
            }
            elRawName = carr;
            //            arr = new String[newSize];
            //            if(needsCopying) System.arraycopy(elLocalName, 0, arr, 0, elStackSize);
            //            elLocalName = arr;
            //            arr = new String[newSize];
            //            if(needsCopying) System.arraycopy(elDefaultNs, 0, arr, 0, elStackSize);
            //            elDefaultNs = arr;
            //            int[] iarr = new int[newSize];
            //            if(needsCopying) System.arraycopy(elNsStackPos, 0, iarr, 0, elStackSize);
            //            for (int i = elStackSize; i < iarr.length; i++)
            //            {
            //                iarr[i] = (i > 0) ? -1 : 0;
            //            }
            //            elNsStackPos = iarr;
            //assert depth < elName.length;
        }
    }

    // entity replacement stack
    protected int entityEnd;

    protected String entityName[];
    protected char[] entityNameBuf[];
    protected String entityReplacement[];
    protected char[] entityReplacementBuf[];

    protected int entityNameHash[];

    // input buffer management
    protected static final int READ_CHUNK_SIZE = 8*1024; //max data chars in one read() call
    protected Reader reader;

    protected int bufLoadFactor = 95;  // 99%
    //protected int bufHardLimit;  // only matters when expanding

    protected char buf[] = new char[
        Runtime.getRuntime().freeMemory() > 1000000L ? READ_CHUNK_SIZE : 256 ];
    protected int bufSoftLimit = ( bufLoadFactor * buf.length ) /100; // desirable size of buffer
    protected boolean preventBufferCompaction;

    protected int bufAbsoluteStart; // this is buf
    protected int bufStart;
    protected int bufEnd;
    protected int pos;
    protected int posStart;
    protected int posEnd;

    protected char pc[] = new char[
        Runtime.getRuntime().freeMemory() > 1000000L ? READ_CHUNK_SIZE : 64 ];
    protected int pcStart;
    protected int pcEnd;


    // parsing state
    //protected boolean needsMore;
    //protected boolean seenMarkup;
    protected boolean usePC;


    protected boolean seenStartTag;
    protected boolean seenEndTag;
    protected boolean pastEndTag;
    protected boolean seenAmpersand;
    protected boolean seenMarkup;
    protected boolean seenDocdecl;

    // transient variable set during each call to next/Token()
    protected boolean tokenize;
    protected String text;
    protected String entityRefName;

    protected String xmlDeclVersion;
    protected Boolean xmlDeclStandalone;
    protected String xmlDeclContent;

    public XmlPullParser( StringReader sReader ) {
        reader = sReader;
        location = null;
        lineNumber = 1;
        columnNumber = 0;
        seenRoot = false;
        reachedEnd = false;
        eventType = START_DOCUMENT;
        emptyElementTag = false;

        depth = 0;

        entityEnd = 0;

        preventBufferCompaction = false;
        bufAbsoluteStart = 0;
        bufEnd = bufStart = 0;
        pos = posStart = posEnd = 0;

        pcEnd = pcStart = 0;

        usePC = false;

        seenStartTag = false;
        seenEndTag = false;
        pastEndTag = false;
        seenAmpersand = false;
        seenMarkup = false;
        seenDocdecl = false;

        xmlDeclVersion = null;
        xmlDeclStandalone = null;
        xmlDeclContent = null;
    }

    private static int findFragment(int bufMinPos, char[] b, int start, int end) {
        //System.err.println("bufStart="+bufStart+" b="+printable(new String(b, start, end - start))+" start="+start+" end="+end);
        if(start < bufMinPos) {
            start = bufMinPos;
            if(start > end) start = end;
            return start;
        }
        if(end - start > 65) {
            start = end - 10; // try to find good location
        }
        int i = start + 1;
        while(--i > bufMinPos) {
            if((end - i) > 65) break;
            final char c = b[i];
            if(c == '<' && (start - i) > 10) break;
        }
        return i;
    }



    private boolean isWhitespace() throws XmlPullParserException
    {
        if(eventType == TEXT || eventType == CDSECT) {
            if(usePC) {
                for (int i = pcStart; i <pcEnd; i++)
                {
                    if(!isS(pc[ i ])) return false;
                }
                return true;
            } else {
                for (int i = posStart; i <posEnd; i++)
                {
                    if(!isS(buf[ i ])) return false;
                }
                return true;
            }
        } else if(eventType == IGNORABLE_WHITESPACE) {
            return true;
        }
        throw new XmlPullParserException("no content available to check for whitespaces");
    }

    /* ************************************************************************* */
    public int getEventType()
        throws XmlPullParserException
    {
        return eventType;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public int getColumnNumber()
    {
        return columnNumber;
    }

    /**
     * Return string describing current position of parsers as
     * text 'STATE [seen %s...] @line:column'.
     */
    public String getPositionDescription ()
    {
        String fragment = null;
        if(posStart <= pos) {
            final int start = findFragment(0, buf, posStart, pos);
            //System.err.println("start="+start);
            if(start < pos) {
                fragment = new String(buf, start, pos - start);
            }
            if(bufAbsoluteStart > 0 || start > 0) fragment = "..." + fragment;
        }
        //        return " at line "+tokenizerPosRow
        //            +" and column "+(tokenizerPosCol-1)
        //            +(fragment != null ? " seen "+printable(fragment)+"..." : "");
        return " "+TYPES[ eventType ] +
            (fragment != null ? " seen "+printable(fragment)+"..." : "")
            +" "+(location != null ? location : "")
            +"@"+getLineNumber()+":"+getColumnNumber();
    }



    public String getName()
    {
        if(eventType == START_TAG) {
            //return elName[ depth - 1 ] ;
            return elName[ depth ] ;
        } else if(eventType == END_TAG) {
            return elName[ depth ] ;
        } else if(eventType == ENTITY_REF) {
            if(entityRefName == null) {
                entityRefName = new String(buf, posStart, posEnd - posStart);
            }
            return entityRefName;
        } else {
            return null;
        }
    }

    public String getText()
    {
        if(eventType == START_DOCUMENT || eventType == END_DOCUMENT) {
            //throw new XmlPullParserException("no content available to read");
            //      if(roundtripSupported) {
            //          text = new String(buf, posStart, posEnd - posStart);
            //      } else {
            return null;
            //      }
        } else if(eventType == ENTITY_REF) {
            return text;
        }
        if(text == null) {
            if(!usePC || eventType == START_TAG || eventType == END_TAG) {
                text = new String(buf, posStart, posEnd - posStart);
            } else {
                text = new String(pc, pcStart, pcEnd - pcStart);
            }
        }
        return text;
    }

    public String nextText() throws XmlPullParserException, IOException
    {
        //        String result = null;
        //        boolean onStartTag = false;
        //        if(eventType == START_TAG) {
        //            onStartTag = true;
        //            next();
        //        }
        //        if(eventType == TEXT) {
        //            result = getText();
        //            next();
        //        } else if(onStartTag && eventType == END_TAG) {
        //            result = "";
        //        } else {
        //            throw new XmlPullParserException(
        //                "parser must be on START_TAG or TEXT to read text", this, null);
        //        }
        //        if(eventType != END_TAG) {
        //            throw new XmlPullParserException(
        //                "event TEXT it must be immediately followed by END_TAG", this, null);
        //        }
        //        return result;
        if(getEventType() != START_TAG) {
            throw new XmlPullParserException(
                "parser must be on START_TAG to read next text", this, null);
        }
        int eventType = next();
        if(eventType == TEXT) {
            final String result = getText();
            eventType = next();
            if(eventType != END_TAG) {
                throw new XmlPullParserException(
                    "TEXT must be immediately followed by END_TAG and not "
                        +TYPES[ getEventType() ], this, null);
            }
            return result;
        } else if(eventType == END_TAG) {
            return "";
        } else {
            throw new XmlPullParserException(
                "parser must be on START_TAG or TEXT to read text", this, null);
        }
    }

    public int nextTag() throws XmlPullParserException, IOException
    {
        next();
        if(eventType == TEXT && isWhitespace()) {  // skip whitespace
            next();
        }
        if (eventType != START_TAG && eventType != END_TAG) {
            throw new XmlPullParserException("expected START_TAG or END_TAG not "
                                                 +TYPES[ getEventType() ], this, null);
        }
        return eventType;
    }

    public int next()
        throws XmlPullParserException, IOException
    {
        tokenize = false;
        return nextImpl();
    }

    public int nextToken()
        throws XmlPullParserException, IOException
    {
        tokenize = true;
        return nextImpl();
    }

    /* ************************************************************************* */

    protected int nextImpl()
        throws XmlPullParserException, IOException
    {
        text = null;
        pcEnd = pcStart = 0;
        usePC = false;
        bufStart = posEnd;
        if(pastEndTag) {
            pastEndTag = false;
            --depth;
        }
        if(emptyElementTag) {
            emptyElementTag = false;
            pastEndTag = true;
            return eventType = END_TAG;
        }

        // [1] document ::= prolog element Misc*
        if(depth > 0) {

            if(seenStartTag) {
                seenStartTag = false;
                return eventType = parseStartTag();
            }
            if(seenEndTag) {
                seenEndTag = false;
                return eventType = parseEndTag();
            }

            // ASSUMPTION: we are _on_ first character of content or markup!!!!
            // [43] content ::= CharData? ((element | Reference | CDSect | PI | Comment) CharData?)*
            char ch;
            if(seenMarkup) {  // we have read ahead ...
                seenMarkup = false;
                ch = '<';
            } else if(seenAmpersand) {
                seenAmpersand = false;
                ch = '&';
            } else {
                ch = more();
            }
            posStart = pos - 1; // VERY IMPORTANT: this is correct start of event!!!

            // when true there is some potential event TEXT to return - keep gathering
            boolean hadCharData = false;

            // when true TEXT data is not continuous (like <![CDATA[text]]>) and requires PC merging
            boolean needsMerging = false;

            MAIN_LOOP:
            while(true) {
                // work on MARKUP
                if(ch == '<') {
                    if(hadCharData) {
                        //posEnd = pos - 1;
                        if(tokenize) {
                            seenMarkup = true;
                            return eventType = TEXT;
                        }
                    }
                    ch = more();
                    if(ch == '/') {
                        if(!tokenize && hadCharData) {
                            seenEndTag = true;
                            //posEnd = pos - 2;
                            return eventType = TEXT;
                        }
                        return eventType = parseEndTag();
                    } else if(ch == '!') {
                        ch = more();
                        if(ch == '-') {
                            // note: if(tokenize == false) posStart/End is NOT changed!!!!
                            parseComment();
                            if(tokenize) return eventType = COMMENT;
                            if( !usePC && hadCharData ) {
                                needsMerging = true;
                            } else {
                                posStart = pos;  //completely ignore comment
                            }
                        } else if(ch == '[') {
                            //posEnd = pos - 3;
                            // must remember previous posStart/End as it merges with content of CDATA
                            //int oldStart = posStart + bufAbsoluteStart;
                            //int oldEnd = posEnd + bufAbsoluteStart;
                            parseCDSect(hadCharData);
                            if(tokenize) return eventType = CDSECT;
                            final int cdStart = posStart;
                            final int cdEnd = posEnd;
                            final int cdLen = cdEnd - cdStart;


                            if(cdLen > 0) { // was there anything inside CDATA section?
                                hadCharData = true;
                                if(!usePC) {
                                    needsMerging = true;
                                }
                            }

                            //                          posStart = oldStart;
                            //                          posEnd = oldEnd;
                            //                          if(cdLen > 0) { // was there anything inside CDATA section?
                            //                              if(hadCharData) {
                            //                                  // do merging if there was anything in CDSect!!!!
                            //                                  //                                    if(!usePC) {
                            //                                  //                                        // posEnd is correct already!!!
                            //                                  //                                        if(posEnd > posStart) {
                            //                                  //                                            joinPC();
                            //                                  //                                        } else {
                            //                                  //                                            usePC = true;
                            //                                  //                                            pcStart = pcEnd = 0;
                            //                                  //                                        }
                            //                                  //                                    }
                            //                                  //                                    if(pcEnd + cdLen >= pc.length) ensurePC(pcEnd + cdLen);
                            //                                  //                                    // copy [cdStart..cdEnd) into PC
                            //                                  //                                    System.arraycopy(buf, cdStart, pc, pcEnd, cdLen);
                            //                                  //                                    pcEnd += cdLen;
                            //                                  if(!usePC) {
                            //                                      needsMerging = true;
                            //                                      posStart = cdStart;
                            //                                      posEnd = cdEnd;
                            //                                  }
                            //                              } else {
                            //                                  if(!usePC) {
                            //                                      needsMerging = true;
                            //                                      posStart = cdStart;
                            //                                      posEnd = cdEnd;
                            //                                      hadCharData = true;
                            //                                  }
                            //                              }
                            //                              //hadCharData = true;
                            //                          } else {
                            //                              if( !usePC && hadCharData ) {
                            //                                  needsMerging = true;
                            //                              }
                            //                          }
                        } else {
                            throw new XmlPullParserException(
                                "unexpected character in markup "+printable(ch), this, null);
                        }
                    } else if(ch == '?') {
                        parsePI();
                        if(tokenize) return eventType = PROCESSING_INSTRUCTION;
                        if( !usePC && hadCharData ) {
                            needsMerging = true;
                        } else {
                            posStart = pos;  //completely ignore PI
                        }

                    } else if( isNameStartChar(ch) ) {
                        if(!tokenize && hadCharData) {
                            seenStartTag = true;
                            //posEnd = pos - 2;
                            return eventType = TEXT;
                        }
                        return eventType = parseStartTag();
                    } else {
                        throw new XmlPullParserException(
                            "unexpected character in markup "+printable(ch), this, null);
                    }
                    // do content comapctation if it makes sense!!!!

                } else if(ch == '&') {
                    // work on ENTITTY
                    //posEnd = pos - 1;
                    if(tokenize && hadCharData) {
                        seenAmpersand = true;
                        return eventType = TEXT;
                    }
                    final int oldStart = posStart + bufAbsoluteStart;
                    final int oldEnd = posEnd + bufAbsoluteStart;
                    final char[] resolvedEntity = parseEntityRef();
                    if(tokenize) return eventType = ENTITY_REF;
                    // check if replacement text can be resolved !!!
                    if(resolvedEntity == null) {
                        if(entityRefName == null) {
                            entityRefName = new String(buf, posStart, posEnd - posStart);
                        }
                        throw new XmlPullParserException(
                            "could not resolve entity named '"+printable(entityRefName)+"'",
                            this, null);
                    }
                    //int entStart = posStart;
                    //int entEnd = posEnd;
                    posStart = oldStart - bufAbsoluteStart;
                    posEnd = oldEnd - bufAbsoluteStart;
                    if(!usePC) {
                        if(hadCharData) {
                            joinPC(); // posEnd is already set correctly!!!
                            needsMerging = false;
                        } else {
                            usePC = true;
                            pcStart = pcEnd = 0;
                        }
                    }
                    //assert usePC == true;
                    // write into PC replacement text - do merge for replacement text!!!!
                    for (int i = 0; i < resolvedEntity.length; i++)
                    {
                        if(pcEnd >= pc.length) ensurePC(pcEnd);
                        pc[pcEnd++] = resolvedEntity[ i ];

                    }
                    hadCharData = true;
                    //assert needsMerging == false;
                } else {

                    if(needsMerging) {
                        //assert usePC == false;
                        joinPC();  // posEnd is already set correctly!!!
                        //posStart = pos  -  1;
                        needsMerging = false;
                    }


                    //no MARKUP not ENTITIES so work on character data ...



                    // [14] CharData ::=   [^<&]* - ([^<&]* ']]>' [^<&]*)


                    hadCharData = true;

                    boolean normalizedCR = false;
                    final boolean normalizeInput = tokenize == false || roundtripSupported == false;
                    // use loop locality here!!!!
                    boolean seenBracket = false;
                    boolean seenBracketBracket = false;
                    do {

                        // check that ]]> does not show in
                        if(ch == ']') {
                            if(seenBracket) {
                                seenBracketBracket = true;
                            } else {
                                seenBracket = true;
                            }
                        } else if(seenBracketBracket && ch == '>') {
                            throw new XmlPullParserException(
                                "characters ]]> are not allowed in content", this, null);
                        } else {
                            if(seenBracket) {
                                seenBracketBracket = seenBracket = false;
                            }
                            // assert seenTwoBrackets == seenBracket == false;
                        }
                        if(normalizeInput) {
                            // deal with normalization issues ...
                            if(ch == '\r') {
                                normalizedCR = true;
                                posEnd = pos -1;
                                // posEnd is already set
                                if(!usePC) {
                                    if(posEnd > posStart) {
                                        joinPC();
                                    } else {
                                        usePC = true;
                                        pcStart = pcEnd = 0;
                                    }
                                }
                                //assert usePC == true;
                                if(pcEnd >= pc.length) ensurePC(pcEnd);
                                pc[pcEnd++] = '\n';
                            } else if(ch == '\n') {
                                //   if(!usePC) {  joinPC(); } else { if(pcEnd >= pc.length) ensurePC(); }
                                if(!normalizedCR && usePC) {
                                    if(pcEnd >= pc.length) ensurePC(pcEnd);
                                    pc[pcEnd++] = '\n';
                                }
                                normalizedCR = false;
                            } else {
                                if(usePC) {
                                    if(pcEnd >= pc.length) ensurePC(pcEnd);
                                    pc[pcEnd++] = ch;
                                }
                                normalizedCR = false;
                            }
                        }

                        ch = more();
                    } while(ch != '<' && ch != '&');
                    posEnd = pos - 1;
                    continue MAIN_LOOP;  // skip ch = more() from below - we are already ahead ...
                }
                ch = more();
            } // endless while(true)
        } else {
            if(seenRoot) {
                return parseEpilog();
            } else {
                return parseProlog();
            }
        }
    }


    protected int parseProlog()
        throws XmlPullParserException, IOException
    {
        // [2] prolog: ::= XMLDecl? Misc* (doctypedecl Misc*)? and look for [39] element

        char ch;
        if(seenMarkup) {
            ch = buf[ pos - 1 ];
        } else {
            ch = more();
        }

        if(eventType == START_DOCUMENT) {
            // bootstrap parsing with getting first character input!
            // deal with BOM
            // detect BOM and crop it (Unicode int Order Mark)
            if(ch == '\uFFFE') {
                throw new XmlPullParserException(
                    "first character in input was UNICODE noncharacter (0xFFFE)"+
                        "- input requires int swapping", this, null);
            }
            if(ch == '\uFEFF') {
                // skipping UNICODE int Order Mark (so called BOM)
                ch = more();
            }
        }
        seenMarkup = false;
        boolean gotS = false;
        posStart = pos - 1;
        final boolean normalizeIgnorableWS = tokenize == true && roundtripSupported == false;
        boolean normalizedCR = false;
        while(true) {
            // deal with Misc
            // [27] Misc ::= Comment | PI | S
            // deal with docdecl --> mark it!
            // else parseStartTag seen <[^/]
            if(ch == '<') {
                if(gotS && tokenize) {
                    posEnd = pos - 1;
                    seenMarkup = true;
                    return eventType = IGNORABLE_WHITESPACE;
                }
                ch = more();
                if(ch == '?') {
                    // check if it is 'xml'
                    // deal with XMLDecl
                    boolean isXMLDecl = parsePI();
                    if(tokenize) {
                        if (isXMLDecl) {
                            return eventType = START_DOCUMENT;
                        }
                        return eventType = PROCESSING_INSTRUCTION;
                    }
                } else if(ch == '!') {
                    ch = more();
                    if(ch == 'D') {
                        if(seenDocdecl) {
                            throw new XmlPullParserException(
                                "only one docdecl allowed in XML document", this, null);
                        }
                        seenDocdecl = true;
                        parseDocdecl();
                        if(tokenize) return eventType = DOCDECL;
                    } else if(ch == '-') {
                        parseComment();
                        if(tokenize) return eventType = COMMENT;
                    } else {
                        throw new XmlPullParserException(
                            "unexpected markup <!"+printable(ch), this, null);
                    }
                } else if(ch == '/') {
                    throw new XmlPullParserException(
                        "expected start tag name and not "+printable(ch), this, null);
                } else if(isNameStartChar(ch)) {
                    seenRoot = true;
                    return parseStartTag();
                } else {
                    throw new XmlPullParserException(
                        "expected start tag name and not "+printable(ch), this, null);
                }
            } else if(isS(ch)) {
                gotS = true;
                if(normalizeIgnorableWS) {
                    if(ch == '\r') {
                        normalizedCR = true;
                        //posEnd = pos -1;
                        //joinPC();
                        // posEnd is already set
                        if(!usePC) {
                            posEnd = pos -1;
                            if(posEnd > posStart) {
                                joinPC();
                            } else {
                                usePC = true;
                                pcStart = pcEnd = 0;
                            }
                        }
                        //assert usePC == true;
                        if(pcEnd >= pc.length) ensurePC(pcEnd);
                        pc[pcEnd++] = '\n';
                    } else if(ch == '\n') {
                        if(!normalizedCR && usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = '\n';
                        }
                        normalizedCR = false;
                    } else {
                        if(usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = ch;
                        }
                        normalizedCR = false;
                    }
                }
            } else {
                throw new XmlPullParserException(
                    "only whitespace content allowed before start tag and not "+printable(ch),
                    this, null);
            }
            ch = more();
        }
    }

    protected int parseEpilog()
        throws XmlPullParserException, IOException
    {
        if(eventType == END_DOCUMENT) {
            throw new XmlPullParserException("already reached end of XML input", this, null);
        }
        if(reachedEnd) {
            return eventType = END_DOCUMENT;
        }
        boolean gotS = false;
        final boolean normalizeIgnorableWS = tokenize == true && roundtripSupported == false;
        boolean normalizedCR = false;
        try {
            // epilog: Misc*
            char ch;
            if(seenMarkup) {
                ch = buf[ pos - 1 ];
            } else {
                ch = more();
            }
            seenMarkup = false;
            posStart = pos - 1;
            if(!reachedEnd) {
                while(true) {
                    // deal with Misc
                    // [27] Misc ::= Comment | PI | S
                    if(ch == '<') {
                        if(gotS && tokenize) {
                            posEnd = pos - 1;
                            seenMarkup = true;
                            return eventType = IGNORABLE_WHITESPACE;
                        }
                        ch = more();
                        if(reachedEnd) {
                            break;
                        }
                        if(ch == '?') {
                            // check if it is 'xml'
                            // deal with XMLDecl
                            parsePI();
                            if(tokenize) return eventType = PROCESSING_INSTRUCTION;

                        } else if(ch == '!') {
                            ch = more();
                            if(reachedEnd) {
                                break;
                            }
                            if(ch == 'D') {
                                parseDocdecl(); //FIXME
                                if(tokenize) return eventType = DOCDECL;
                            } else if(ch == '-') {
                                parseComment();
                                if(tokenize) return eventType = COMMENT;
                            } else {
                                throw new XmlPullParserException(
                                    "unexpected markup <!"+printable(ch), this, null);
                            }
                        } else if(ch == '/') {
                            throw new XmlPullParserException(
                                "end tag not allowed in epilog but got "+printable(ch), this, null);
                        } else if(isNameStartChar(ch)) {
                            throw new XmlPullParserException(
                                "start tag not allowed in epilog but got "+printable(ch), this, null);
                        } else {
                            throw new XmlPullParserException(
                                "in epilog expected ignorable content and not "+printable(ch),
                                this, null);
                        }
                    } else if(isS(ch)) {
                        gotS = true;
                        if(normalizeIgnorableWS) {
                            if(ch == '\r') {
                                normalizedCR = true;
                                //posEnd = pos -1;
                                //joinPC();
                                // posEnd is already set
                                if(!usePC) {
                                    posEnd = pos -1;
                                    if(posEnd > posStart) {
                                        joinPC();
                                    } else {
                                        usePC = true;
                                        pcStart = pcEnd = 0;
                                    }
                                }
                                //assert usePC == true;
                                if(pcEnd >= pc.length) ensurePC(pcEnd);
                                pc[pcEnd++] = '\n';
                            } else if(ch == '\n') {
                                if(!normalizedCR && usePC) {
                                    if(pcEnd >= pc.length) ensurePC(pcEnd);
                                    pc[pcEnd++] = '\n';
                                }
                                normalizedCR = false;
                            } else {
                                if(usePC) {
                                    if(pcEnd >= pc.length) ensurePC(pcEnd);
                                    pc[pcEnd++] = ch;
                                }
                                normalizedCR = false;
                            }
                        }
                    } else {
                        throw new XmlPullParserException(
                            "in epilog non whitespace content is not allowed but got "+printable(ch),
                            this, null);
                    }
                    ch = more();
                    if(reachedEnd) {
                        break;
                    }

                }
            }

            // throw Exception("unexpected content in epilog
            // catch EOFException return END_DOCUMENT
            //try {
        } catch(EOFException ex) {
            reachedEnd = true;
        }
        if(reachedEnd) {
            if(tokenize && gotS) {
                posEnd = pos; // well - this is LAST available character pos
                return eventType = IGNORABLE_WHITESPACE;
            }
            return eventType = END_DOCUMENT;
        } else {
            throw new XmlPullParserException("internal error in parseEpilog");
        }
    }


    private int parseEndTag() throws XmlPullParserException, IOException {
        //ASSUMPTION ch is past "</"
        // [42] ETag ::=  '</' Name S? '>'
        char ch = more();
        if(!isNameStartChar(ch)) {
            throw new XmlPullParserException(
                "expected name start and not "+printable(ch), this, null);
        }
        posStart = pos - 3;
        final int nameStart = pos - 1 + bufAbsoluteStart;
        do {
            ch = more();
        } while(isNameChar(ch));

        // now we go one level down -- do checks
        //--depth;  //FIXME

        // check that end tag name is the same as start tag
        //String name = new String(buf, nameStart - bufAbsoluteStart,
        //                           (pos - 1) - (nameStart - bufAbsoluteStart));
        //int last = pos - 1;
        int off = nameStart - bufAbsoluteStart;
        //final int len = last - off;
        final int len = (pos - 1) - off;
        final char[] cbuf = elRawName[depth];
        if(elRawNameEnd[depth] != len) {
            // construct strings for exception
            final String startname = new String(cbuf, 0, elRawNameEnd[depth]);
            final String endname = new String(buf, off, len);
            throw new XmlPullParserException(
                "end tag name </"+endname+"> must match start tag name <"+startname+">"
                    +" from line "+elRawNameLine[depth], this, null);
        }
        for (int i = 0; i < len; i++)
        {
            if(buf[off++] != cbuf[i]) {
                // construct strings for exception
                final String startname = new String(cbuf, 0, len);
                final String endname = new String(buf, off - i - 1, len);
                throw new XmlPullParserException(
                    "end tag name </"+endname+"> must be the same as start tag <"+startname+">"
                        +" from line "+elRawNameLine[depth], this, null);
            }
        }

        while(isS(ch)) { ch = more(); } // skip additional white spaces
        if(ch != '>') {
            throw new XmlPullParserException(
                "expected > to finsh end tag not "+printable(ch)
                    +" from line "+elRawNameLine[depth], this, null);
        }
        posEnd = pos;
        pastEndTag = true;
        return eventType = END_TAG;
    }

    private int parseStartTag() throws XmlPullParserException, IOException {
        //ASSUMPTION ch is past <T
        ++depth; //FIXME

        posStart = pos - 2;

        emptyElementTag = false;
        // retrieve name
        final int nameStart = pos - 1 + bufAbsoluteStart;
        int colonPos = -1;
        char ch = buf[ pos - 1];
        while(true) {
            ch = more();
            if(!isNameChar(ch)) break;
        }

        // retrieve name
        ensureElementsCapacity();


        //TODO check for efficient interning and then use elRawNameInterned!!!!

        int elLen = (pos - 1) - (nameStart - bufAbsoluteStart);
        if(elRawName[ depth ] == null || elRawName[ depth ].length < elLen) {
            elRawName[ depth ] = new char[ 2 * elLen ];
        }
        System.arraycopy(buf, nameStart - bufAbsoluteStart, elRawName[ depth ], 0, elLen);
        elRawNameEnd[ depth ] = elLen;
        elRawNameLine[ depth ] = lineNumber;

        String name = null;
        name = elName[ depth ] = new String(buf, nameStart - bufAbsoluteStart, elLen);
        while(true) {

            while(isS(ch)) { ch = more(); } // skip additional white spaces

            if(ch == '>') {
                break;
            } else if(ch == '/') {
                if(emptyElementTag) throw new XmlPullParserException(
                        "repeated / in tag declaration", this, null);
                emptyElementTag = true;
                ch = more();
                if(ch != '>') throw new XmlPullParserException(
                        "expected > to end empty tag not "+printable(ch), this, null);
                break;
            } else if(isNameStartChar(ch)) {
//                ch = parseAttribute();
                ch = more();
                continue;
            } else {
                throw new XmlPullParserException(
                    "start tag unexpected character "+printable(ch), this, null);
            }
            //ch = more(); // skip space
        }

        posEnd = pos;
        return eventType = START_TAG;
    }

    protected char[] charRefOneCharBuf = new char[1];

    protected char[] parseEntityRef()
        throws XmlPullParserException, IOException
    {
        // entity reference http://www.w3.org/TR/2000/REC-xml-20001006#NT-Reference
        // [67] Reference          ::=          EntityRef | CharRef

        // ASSUMPTION just after &
        entityRefName = null;
        posStart = pos;
        char ch = more();
        StringBuffer sb = new StringBuffer();
        if(ch == '#') {
            // parse character reference
            char charRef = 0;
            ch = more();
            if(ch == 'x') {
                //encoded in hex
                while(true) {
                    ch = more();
                    if(ch >= '0' && ch <= '9') {
                        charRef = (char)(charRef * 16 + (ch - '0'));
                        sb.append( ch );
                    } else if(ch >= 'a' && ch <= 'f') {
                        charRef = (char)(charRef * 16 + (ch - ('a' - 10)));
                        sb.append( ch );
                    } else if(ch >= 'A' && ch <= 'F') {
                        charRef = (char)(charRef * 16 + (ch - ('A' - 10)));
                        sb.append( ch );
                    } else if(ch == ';') {
                        break;
                    } else {
                        throw new XmlPullParserException(
                            "character reference (with hex value) may not contain "
                                +printable(ch), this, null);
                    }
                }
            } else {
                // encoded in decimal
                while(true) {
                    if(ch >= '0' && ch <= '9') {
                        charRef = (char)(charRef * 10 + (ch - '0'));
                    } else if(ch == ';') {
                        break;
                    } else {
                        throw new XmlPullParserException(
                            "character reference (with decimal value) may not contain "
                                +printable(ch), this, null);
                    }
                    ch = more();
                }
            }
            posEnd = pos - 1;
            if ( sb.length() > 0 )
            {
                char[] tmp = toChars( Integer.parseInt( sb.toString(), 16 ) );
                charRefOneCharBuf = tmp;
                if ( tokenize )
                {
                    text = new String( charRefOneCharBuf, 0, charRefOneCharBuf.length );
                }
                return charRefOneCharBuf;
            }
            charRefOneCharBuf[0] = charRef;
            if(tokenize) {
                text = new String(charRefOneCharBuf, 0, 1);
            }
            return charRefOneCharBuf;
        } else {
            // [68]     EntityRef          ::=          '&' Name ';'
            // scan anem until ;
            if(!isNameStartChar(ch)) {
                throw new XmlPullParserException(
                    "entity reference names can not start with character '"
                        +printable(ch)+"'", this, null);
            }
            while(true) {
                ch = more();
                if(ch == ';') {
                    break;
                }
                if(!isNameChar(ch)) {
                    throw new XmlPullParserException(
                        "entity reference name can not contain character "
                            +printable(ch)+"'", this, null);
                }
            }
            posEnd = pos - 1;
            // determine what name maps to
            final int len = posEnd - posStart;
            if(len == 2 && buf[posStart] == 'l' && buf[posStart+1] == 't') {
                if(tokenize) {
                    text = "<";
                }
                charRefOneCharBuf[0] = '<';
                return charRefOneCharBuf;
                //if(paramPC || isParserTokenizing) {
                //    if(pcEnd >= pc.length) ensurePC();
                //   pc[pcEnd++] = '<';
                //}
            } else if(len == 3 && buf[posStart] == 'a'
                          && buf[posStart+1] == 'm' && buf[posStart+2] == 'p') {
                if(tokenize) {
                    text = "&";
                }
                charRefOneCharBuf[0] = '&';
                return charRefOneCharBuf;
            } else if(len == 2 && buf[posStart] == 'g' && buf[posStart+1] == 't') {
                if(tokenize) {
                    text = ">";
                }
                charRefOneCharBuf[0] = '>';
                return charRefOneCharBuf;
            } else if(len == 4 && buf[posStart] == 'a' && buf[posStart+1] == 'p'
                          && buf[posStart+2] == 'o' && buf[posStart+3] == 's')
            {
                if(tokenize) {
                    text = "'";
                }
                charRefOneCharBuf[0] = '\'';
                return charRefOneCharBuf;
            } else if(len == 4 && buf[posStart] == 'q' && buf[posStart+1] == 'u'
                          && buf[posStart+2] == 'o' && buf[posStart+3] == 't')
            {
                if(tokenize) {
                    text = "\"";
                }
                charRefOneCharBuf[0] = '"';
                return charRefOneCharBuf;
            } else {
                final char[] result = lookuEntityReplacement(len);
                if(result != null) {
                    return result;
                }
            }
            if(tokenize) text = null;
            return null;
        }
    }

    protected char[] lookuEntityReplacement(int entitNameLen)
        throws XmlPullParserException, IOException

    {
        entityRefName = new String(buf, posStart, posEnd - posStart);
        for (int i = entityEnd - 1; i >= 0; --i) {
            // take advantage that interning for newStirng is enforced
            if(entityRefName == entityName[ i ]) {
                if(tokenize) text = entityReplacement[ i ];
                return entityReplacementBuf[ i ];
            }
        }
        return null;
    }

    protected void parseComment()
        throws XmlPullParserException, IOException
    {
        // implements XML 1.0 Section 2.5 Comments

        //ASSUMPTION: seen <!-
        char ch = more();
        if(ch != '-') throw new XmlPullParserException(
                "expected <!-- for comment start", this, null);
        if(tokenize) posStart = pos;

        final int curLine = lineNumber;
        final int curColumn = columnNumber;
        try {
            final boolean normalizeIgnorableWS = tokenize == true && roundtripSupported == false;
            boolean normalizedCR = false;

            boolean seenDash = false;
            boolean seenDashDash = false;
            while(true) {
                // scan until it hits -->
                ch = more();
                if(seenDashDash && ch != '>') {
                    throw new XmlPullParserException(
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
                if(normalizeIgnorableWS) {
                    if(ch == '\r') {
                        normalizedCR = true;
                        //posEnd = pos -1;
                        //joinPC();
                        // posEnd is alreadys set
                        if(!usePC) {
                            posEnd = pos -1;
                            if(posEnd > posStart) {
                                joinPC();
                            } else {
                                usePC = true;
                                pcStart = pcEnd = 0;
                            }
                        }
                        //assert usePC == true;
                        if(pcEnd >= pc.length) ensurePC(pcEnd);
                        pc[pcEnd++] = '\n';
                    } else if(ch == '\n') {
                        if(!normalizedCR && usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = '\n';
                        }
                        normalizedCR = false;
                    } else {
                        if(usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = ch;
                        }
                        normalizedCR = false;
                    }
                }
            }

        } catch(EOFException ex) {
            // detect EOF and create meaningful error ...
            throw new XmlPullParserException(
                "comment started on line "+curLine+" and column "+curColumn+" was not closed",
                this, ex);
        }
        if(tokenize) {
            posEnd = pos - 3;
            if(usePC) {
                pcEnd -= 2;
            }
        }
    }

    protected boolean parsePI()
        throws XmlPullParserException, IOException
    {
        // implements XML 1.0 Section 2.6 Processing Instructions

        // [16] PI ::= '<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
        // [17] PITarget         ::=    Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
        //ASSUMPTION: seen <?
        if(tokenize) posStart = pos;
        final int curLine = lineNumber;
        final int curColumn = columnNumber;
        int piTargetStart = pos + bufAbsoluteStart;
        int piTargetEnd = -1;
        final boolean normalizeIgnorableWS = tokenize == true && roundtripSupported == false;
        boolean normalizedCR = false;

        try {
            boolean seenQ = false;
            char ch = more();
            if(isS(ch)) {
                throw new XmlPullParserException(
                    "processing instruction PITarget must be exactly after <? and not white space character",
                    this, null);
            }
            while(true) {
                // scan until it hits ?>
                //ch = more();

                if(ch == '?') {
                    seenQ = true;
                } else if(ch == '>') {
                    if(seenQ) {
                        break;  // found end sequence!!!!
                    }
                    seenQ = false;
                } else {
                    if(piTargetEnd == -1 && isS(ch)) {
                        piTargetEnd = pos - 1 + bufAbsoluteStart;

                        // [17] PITarget ::= Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
                        if((piTargetEnd - piTargetStart) == 3) {
                            if((buf[piTargetStart] == 'x' || buf[piTargetStart] == 'X')
                                   && (buf[piTargetStart+1] == 'm' || buf[piTargetStart+1] == 'M')
                                   && (buf[piTargetStart+2] == 'l' || buf[piTargetStart+2] == 'L')
                              )
                            {
                                if(piTargetStart > 3) {  //<?xml is allowed as first characters in input ...
                                    throw new XmlPullParserException(
                                        "processing instruction can not have PITarget with reserveld xml name",
                                        this, null);
                                } else {
                                    if(buf[piTargetStart] != 'x'
                                           && buf[piTargetStart+1] != 'm'
                                           && buf[piTargetStart+2] != 'l')
                                    {
                                        throw new XmlPullParserException(
                                            "XMLDecl must have xml name in lowercase",
                                            this, null);
                                    }
                                }
                                parseXmlDecl(ch);
                                if(tokenize) posEnd = pos - 2;
                                final int off = piTargetStart - bufAbsoluteStart + 3;
                                final int len = pos - 2 - off;
                                xmlDeclContent = new String(buf, off, len);
                                return false;
                            }
                        }
                    }
                    seenQ = false;
                }
                if(normalizeIgnorableWS) {
                    if(ch == '\r') {
                        normalizedCR = true;
                        //posEnd = pos -1;
                        //joinPC();
                        // posEnd is alreadys set
                        if(!usePC) {
                            posEnd = pos -1;
                            if(posEnd > posStart) {
                                joinPC();
                            } else {
                                usePC = true;
                                pcStart = pcEnd = 0;
                            }
                        }
                        //assert usePC == true;
                        if(pcEnd >= pc.length) ensurePC(pcEnd);
                        pc[pcEnd++] = '\n';
                    } else if(ch == '\n') {
                        if(!normalizedCR && usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = '\n';
                        }
                        normalizedCR = false;
                    } else {
                        if(usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = ch;
                        }
                        normalizedCR = false;
                    }
                }
                ch = more();
            }
        } catch(EOFException ex) {
            // detect EOF and create meaningful error ...
            throw new XmlPullParserException(
                "processing instruction started on line "+curLine+" and column "+curColumn
                    +" was not closed",
                this, ex);
        }
        if(piTargetEnd == -1) {
            piTargetEnd = pos - 2 + bufAbsoluteStart;
            //throw new XmlPullParserException(
            //    "processing instruction must have PITarget name", this, null);
        }
        piTargetStart -= bufAbsoluteStart;
        piTargetEnd -= bufAbsoluteStart;
        if(tokenize) {
            posEnd = pos - 2;
            if(normalizeIgnorableWS) {
                --pcEnd;
            }
        }
        return true;
    }

    //    protected final static char[] VERSION = {'v','e','r','s','i','o','n'};
    //    protected final static char[] NCODING = {'n','c','o','d','i','n','g'};
    //    protected final static char[] TANDALONE = {'t','a','n','d','a','l','o','n','e'};
    //    protected final static char[] YES = {'y','e','s'};
    //    protected final static char[] NO = {'n','o'};

    protected final static char[] VERSION = "version".toCharArray();
    protected final static char[] NCODING = "ncoding".toCharArray();
    protected final static char[] TANDALONE = "tandalone".toCharArray();
    protected final static char[] YES = "yes".toCharArray();
    protected final static char[] NO = "no".toCharArray();



    protected void parseXmlDecl(char ch)
        throws XmlPullParserException, IOException
    {
        // [23] XMLDecl ::= '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'

        // first make sure that relative positions will stay OK
        preventBufferCompaction = true;
        bufStart = 0; // necessary to keep pos unchanged during expansion!

        // --- parse VersionInfo

        // [24] VersionInfo ::= S 'version' Eq ("'" VersionNum "'" | '"' VersionNum '"')
        // parse is positioned just on first S past <?xml
        ch = skipS(ch);
        ch = requireInput(ch, VERSION);
        // [25] Eq ::= S? '=' S?
        ch = skipS(ch);
        if(ch != '=') {
            throw new XmlPullParserException(
                "expected equals sign (=) after version and not "+printable(ch), this, null);
        }
        ch = more();
        ch = skipS(ch);
        if(ch != '\'' && ch != '"') {
            throw new XmlPullParserException(
                "expected apostrophe (') or quotation mark (\") after version and not "
                    +printable(ch), this, null);
        }
        final char quotChar = ch;
        //int versionStart = pos + bufAbsoluteStart;  // required if preventBufferCompaction==false
        final int versionStart = pos;
        ch = more();
        // [26] VersionNum ::= ([a-zA-Z0-9_.:] | '-')+
        while(ch != quotChar) {
            if((ch  < 'a' || ch > 'z') && (ch  < 'A' || ch > 'Z') && (ch  < '0' || ch > '9')
                   && ch != '_' && ch != '.' && ch != ':' && ch != '-')
            {
                throw new XmlPullParserException(
                    "<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-')"
                        +" not "+printable(ch), this, null);
            }
            ch = more();
        }
        final int versionEnd = pos - 1;
        parseXmlDeclWithVersion(versionStart, versionEnd);
        preventBufferCompaction = false; // allow again buffer commpaction - pos MAY chnage
    }

    protected void parseXmlDeclWithVersion(int versionStart, int versionEnd)
        throws XmlPullParserException, IOException
    {
        // check version is "1.0"
        if((versionEnd - versionStart != 3)
               || buf[versionStart] != '1'
               || buf[versionStart+1] != '.'
               || buf[versionStart+2] != '0')
        {
            throw new XmlPullParserException(
                "only 1.0 is supported as <?xml version not '"
                    +printable(new String(buf, versionStart, versionEnd - versionStart))+"'", this, null);
        }
        xmlDeclVersion = new String(buf, versionStart, versionEnd - versionStart);

        // [80] EncodingDecl ::= S 'encoding' Eq ('"' EncName '"' | "'" EncName "'" )
        char ch = more();
        ch = skipS(ch);
        if(ch == 'e') {
            ch = more();
            ch = requireInput(ch, NCODING);
            ch = skipS(ch);
            if(ch != '=') {
                throw new XmlPullParserException(
                    "expected equals sign (=) after encoding and not "+printable(ch), this, null);
            }
            ch = more();
            ch = skipS(ch);
            if(ch != '\'' && ch != '"') {
                throw new XmlPullParserException(
                    "expected apostrophe (') or quotation mark (\") after encoding and not "
                        +printable(ch), this, null);
            }
            final char quotChar = ch;
            final int encodingStart = pos;
            ch = more();
            // [81] EncName ::= [A-Za-z] ([A-Za-z0-9._] | '-')*
            if((ch  < 'a' || ch > 'z') && (ch  < 'A' || ch > 'Z'))
            {
                throw new XmlPullParserException(
                    "<?xml encoding name expected to start with [A-Za-z]"
                        +" not "+printable(ch), this, null);
            }
            ch = more();
            while(ch != quotChar) {
                if((ch  < 'a' || ch > 'z') && (ch  < 'A' || ch > 'Z') && (ch  < '0' || ch > '9')
                       && ch != '.' && ch != '_' && ch != '-')
                {
                    throw new XmlPullParserException(
                        "<?xml encoding value expected to be in ([A-Za-z0-9._] | '-')"
                            +" not "+printable(ch), this, null);
                }
                ch = more();
            }
            final int encodingEnd = pos - 1;
            ch = more();
        }

        ch = skipS(ch);
        // [32] SDDecl ::= S 'standalone' Eq (("'" ('yes' | 'no') "'") | ('"' ('yes' | 'no') '"'))
        if(ch == 's') {
            ch = more();
            ch = requireInput(ch, TANDALONE);
            ch = skipS(ch);
            if(ch != '=') {
                throw new XmlPullParserException(
                    "expected equals sign (=) after standalone and not "+printable(ch),
                    this, null);
            }
            ch = more();
            ch = skipS(ch);
            if(ch != '\'' && ch != '"') {
                throw new XmlPullParserException(
                    "expected apostrophe (') or quotation mark (\") after encoding and not "
                        +printable(ch), this, null);
            }
            char quotChar = ch;
            int standaloneStart = pos;
            ch = more();
            if(ch == 'y') {
                ch = requireInput(ch, YES);
                //Boolean standalone = new Boolean(true);
                xmlDeclStandalone = new Boolean(true);
            } else if(ch == 'n') {
                ch = requireInput(ch, NO);
                //Boolean standalone = new Boolean(false);
                xmlDeclStandalone = new Boolean(false);
            } else {
                throw new XmlPullParserException(
                    "expected 'yes' or 'no' after standalone and not "
                        +printable(ch), this, null);
            }
            if(ch != quotChar) {
                throw new XmlPullParserException(
                    "expected "+quotChar+" after standalone value not "
                        +printable(ch), this, null);
            }
            ch = more();
        }


        ch = skipS(ch);
        if(ch != '?') {
            throw new XmlPullParserException(
                "expected ?> as last part of <?xml not "
                    +printable(ch), this, null);
        }
        ch = more();
        if(ch != '>') {
            throw new XmlPullParserException(
                "expected ?> as last part of <?xml not "
                    +printable(ch), this, null);
        }

    }
    protected void parseDocdecl()
        throws XmlPullParserException, IOException
    {
        //ASSUMPTION: seen <!D
        char ch = more();
        if(ch != 'O') throw new XmlPullParserException(
                "expected <!DOCTYPE", this, null);
        ch = more();
        if(ch != 'C') throw new XmlPullParserException(
                "expected <!DOCTYPE", this, null);
        ch = more();
        if(ch != 'T') throw new XmlPullParserException(
                "expected <!DOCTYPE", this, null);
        ch = more();
        if(ch != 'Y') throw new XmlPullParserException(
                "expected <!DOCTYPE", this, null);
        ch = more();
        if(ch != 'P') throw new XmlPullParserException(
                "expected <!DOCTYPE", this, null);
        ch = more();
        if(ch != 'E') throw new XmlPullParserException(
                "expected <!DOCTYPE", this, null);
        posStart = pos;
        // do simple and crude scanning for end of doctype

        // [28]  doctypedecl ::= '<!DOCTYPE' S Name (S ExternalID)? S? ('['
        //                      (markupdecl | DeclSep)* ']' S?)? '>'
        int bracketLevel = 0;
        final boolean normalizeIgnorableWS = tokenize == true && roundtripSupported == false;
        boolean normalizedCR = false;
        while(true) {
            ch = more();
            if(ch == '[') ++bracketLevel;
            if(ch == ']') --bracketLevel;
            if(ch == '>' && bracketLevel == 0) break;
            if(normalizeIgnorableWS) {
                if(ch == '\r') {
                    normalizedCR = true;
                    //posEnd = pos -1;
                    //joinPC();
                    // posEnd is alreadys set
                    if(!usePC) {
                        posEnd = pos -1;
                        if(posEnd > posStart) {
                            joinPC();
                        } else {
                            usePC = true;
                            pcStart = pcEnd = 0;
                        }
                    }
                    //assert usePC == true;
                    if(pcEnd >= pc.length) ensurePC(pcEnd);
                    pc[pcEnd++] = '\n';
                } else if(ch == '\n') {
                    if(!normalizedCR && usePC) {
                        if(pcEnd >= pc.length) ensurePC(pcEnd);
                        pc[pcEnd++] = '\n';
                    }
                    normalizedCR = false;
                } else {
                    if(usePC) {
                        if(pcEnd >= pc.length) ensurePC(pcEnd);
                        pc[pcEnd++] = ch;
                    }
                    normalizedCR = false;
                }
            }

        }
        posEnd = pos - 1;
    }

    protected void parseCDSect(boolean hadCharData)
        throws XmlPullParserException, IOException
    {
        // implements XML 1.0 Section 2.7 CDATA Sections

        // [18] CDSect ::= CDStart CData CDEnd
        // [19] CDStart ::=  '<![CDATA['
        // [20] CData ::= (Char* - (Char* ']]>' Char*))
        // [21] CDEnd ::= ']]>'

        //ASSUMPTION: seen <![
        char ch = more();
        if(ch != 'C') throw new XmlPullParserException(
                "expected <[CDATA[ for comment start", this, null);
        ch = more();
        if(ch != 'D') throw new XmlPullParserException(
                "expected <[CDATA[ for comment start", this, null);
        ch = more();
        if(ch != 'A') throw new XmlPullParserException(
                "expected <[CDATA[ for comment start", this, null);
        ch = more();
        if(ch != 'T') throw new XmlPullParserException(
                "expected <[CDATA[ for comment start", this, null);
        ch = more();
        if(ch != 'A') throw new XmlPullParserException(
                "expected <[CDATA[ for comment start", this, null);
        ch = more();
        if(ch != '[') throw new XmlPullParserException(
                "expected <![CDATA[ for comment start", this, null);

        //if(tokenize) {
        final int cdStart = pos + bufAbsoluteStart;
        final int curLine = lineNumber;
        final int curColumn = columnNumber;
        final boolean normalizeInput = tokenize == false || roundtripSupported == false;
        try {
            if(normalizeInput) {
                if(hadCharData) {
                    if(!usePC) {
                        // posEnd is correct already!!!
                        if(posEnd > posStart) {
                            joinPC();
                        } else {
                            usePC = true;
                            pcStart = pcEnd = 0;
                        }
                    }
                }
            }
            boolean seenBracket = false;
            boolean seenBracketBracket = false;
            boolean normalizedCR = false;
            while(true) {
                // scan until it hits "]]>"
                ch = more();
                if(ch == ']') {
                    if(!seenBracket) {
                        seenBracket = true;
                    } else {
                        seenBracketBracket = true;
                        //seenBracket = false;
                    }
                } else if(ch == '>') {
                    if(seenBracket && seenBracketBracket) {
                        break;  // found end sequence!!!!
                    } else {
                        seenBracketBracket = false;
                    }
                    seenBracket = false;
                } else {
                    if(seenBracket) {
                        seenBracket = false;
                    }
                }
                if(normalizeInput) {
                    // deal with normalization issues ...
                    if(ch == '\r') {
                        normalizedCR = true;
                        posStart = cdStart - bufAbsoluteStart;
                        posEnd = pos - 1; // posEnd is alreadys set
                        if(!usePC) {
                            if(posEnd > posStart) {
                                joinPC();
                            } else {
                                usePC = true;
                                pcStart = pcEnd = 0;
                            }
                        }
                        //assert usePC == true;
                        if(pcEnd >= pc.length) ensurePC(pcEnd);
                        pc[pcEnd++] = '\n';
                    } else if(ch == '\n') {
                        if(!normalizedCR && usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = '\n';
                        }
                        normalizedCR = false;
                    } else {
                        if(usePC) {
                            if(pcEnd >= pc.length) ensurePC(pcEnd);
                            pc[pcEnd++] = ch;
                        }
                        normalizedCR = false;
                    }
                }
            }
        } catch(EOFException ex) {
            // detect EOF and create meaningful error ...
            throw new XmlPullParserException(
                "CDATA section started on line "+curLine+" and column "+curColumn+" was not closed",
                this, ex);
        }
        if(normalizeInput) {
            if(usePC) {
                pcEnd = pcEnd - 2;
            }
        }
        posStart = cdStart - bufAbsoluteStart;
        posEnd = pos - 3;
    }

    protected void fillBuf() throws IOException, XmlPullParserException {
        if(reader == null) throw new XmlPullParserException(
                "reader must be set before parsing is started");

        // see if we are in compaction area
        if(bufEnd > bufSoftLimit) {

            // expand buffer it makes sense!!!!
            boolean compact = bufStart > bufSoftLimit;
            boolean expand = false;
            if(preventBufferCompaction) {
                compact = false;
                expand = true;
            } else if(!compact) {
                //freeSpace
                if(bufStart < buf.length / 2) {
                    // less then half buffer available forcompactin --> expand instead!!!
                    expand = true;
                } else {
                    // at least half of buffer can be reclaimed --> worthwhile effort!!!
                    compact = true;
                }
            }

            // if buffer almost full then compact it
            if(compact) {
                //TODO: look on trashing
                // //assert bufStart > 0
                System.arraycopy(buf, bufStart, buf, 0, bufEnd - bufStart);
                if(TRACE_SIZING) System.out.println(
                        "TRACE_SIZING fillBuf() compacting "+bufStart
                            +" bufEnd="+bufEnd
                            +" pos="+pos+" posStart="+posStart+" posEnd="+posEnd
                            +" buf first 100 chars:"+new String(buf, bufStart,
                                                                bufEnd - bufStart < 100 ? bufEnd - bufStart : 100 ));

            } else if(expand) {
                final int newSize = 2 * buf.length;
                final char newBuf[] = new char[ newSize ];
                if(TRACE_SIZING) System.out.println("TRACE_SIZING fillBuf() "+buf.length+" => "+newSize);
                System.arraycopy(buf, bufStart, newBuf, 0, bufEnd - bufStart);
                buf = newBuf;
                if(bufLoadFactor > 0) {
                    bufSoftLimit = ( bufLoadFactor * buf.length ) /100;
                }

            } else {
                throw new XmlPullParserException("internal error in fillBuffer()");
            }
            bufEnd -= bufStart;
            pos -= bufStart;
            posStart -= bufStart;
            posEnd -= bufStart;
            bufAbsoluteStart += bufStart;
            bufStart = 0;
            if(TRACE_SIZING) System.out.println(
                    "TRACE_SIZING fillBuf() after bufEnd="+bufEnd
                        +" pos="+pos+" posStart="+posStart+" posEnd="+posEnd
                        +" buf first 100 chars:"+new String(buf, 0, bufEnd < 100 ? bufEnd : 100));
        }
        // at least one charcter must be read or error
        final int len = buf.length - bufEnd > READ_CHUNK_SIZE ? READ_CHUNK_SIZE : buf.length - bufEnd;
        final int ret = reader.read(buf, bufEnd, len);
        if(ret > 0) {
            bufEnd += ret;
            if(TRACE_SIZING) System.out.println(
                    "TRACE_SIZING fillBuf() after filling in buffer"
                        +" buf first 100 chars:"+new String(buf, 0, bufEnd < 100 ? bufEnd : 100));

            return;
        }
        if(ret == -1) {
            if(bufAbsoluteStart == 0 && pos == 0) {
                throw new EOFException("input contained no data");
            } else {
                if(seenRoot && depth == 0) { // inside parsing epilog!!!
                    reachedEnd = true;
                    return;
                } else {
                    StringBuffer expectedTagStack = new StringBuffer();
                    if(depth > 0) {
                        //final char[] cbuf = elRawName[depth];
                        //final String startname = new String(cbuf, 0, elRawNameEnd[depth]);
                        expectedTagStack.append(" - expected end tag");
                        if(depth > 1) {
                            expectedTagStack.append("s"); //more than one end tag
                        }
                        expectedTagStack.append(" ");
                        for (int i = depth; i > 0; i--)
                        {
                            String tagName = new String(elRawName[i], 0, elRawNameEnd[i]);
                            expectedTagStack.append("</").append(tagName).append('>');
                        }
                        expectedTagStack.append(" to close");
                        for (int i = depth; i > 0; i--)
                        {
                            if(i != depth) {
                                expectedTagStack.append(" and"); //more than one end tag
                            }
                            String tagName = new String(elRawName[i], 0, elRawNameEnd[i]);
                            expectedTagStack.append(" start tag <"+tagName+">");
                            expectedTagStack.append(" from line "+elRawNameLine[i]);
                        }
                        expectedTagStack.append(", parser stopped on");
                    }
                    throw new EOFException("no more data available"
                                               +expectedTagStack.toString()+getPositionDescription());
                }
            }
        } else {
            throw new IOException("error reading input, returned "+ret);
        }
    }

    protected char more() throws IOException, XmlPullParserException {
        if(pos >= bufEnd) {
            fillBuf();
            // this return value should be ignonored as it is used in epilog parsing ...
            if(reachedEnd) return (char)-1;
        }
        final char ch = buf[pos++];
        //line/columnNumber
        if(ch == '\n') { ++lineNumber; columnNumber = 1; }
        else { ++columnNumber; }
        //System.out.print(ch);
        return ch;
    }

    protected void ensurePC(int end) {
        //assert end >= pc.length;
        final int newSize = end > READ_CHUNK_SIZE ? 2 * end : 2 * READ_CHUNK_SIZE;
        final char[] newPC = new char[ newSize ];
        if(TRACE_SIZING) System.out.println("TRACE_SIZING ensurePC() "+pc.length+" ==> "+newSize+" end="+end);
        System.arraycopy(pc, 0, newPC, 0, pcEnd);
        pc = newPC;
        //assert end < pc.length;
    }

    protected void joinPC() {
        //assert usePC == false;
        //assert posEnd > posStart;
        final int len = posEnd - posStart;
        final int newEnd = pcEnd + len + 1;
        if(newEnd >= pc.length) ensurePC(newEnd); // add 1 for extra space for one char
        //assert newEnd < pc.length;
        System.arraycopy(buf, posStart, pc, pcEnd, len);
        pcEnd += len;
        usePC = true;

    }

    protected char requireInput(char ch, char[] input)
        throws XmlPullParserException, IOException
    {
        for (int i = 0; i < input.length; i++)
        {
            if(ch != input[i]) {
                throw new XmlPullParserException(
                    "expected "+printable(input[i])+" in "+new String(input)
                        +" and not "+printable(ch), this, null);
            }
            ch = more();
        }
        return ch;
    }

    protected char requireNextS()
        throws XmlPullParserException, IOException
    {
        final char ch = more();
        if(!isS(ch)) {
            throw new XmlPullParserException(
                "white space is required and not "+printable(ch), this, null);
        }
        return skipS(ch);
    }

    protected char skipS(char ch)
        throws XmlPullParserException, IOException
    {
        while(isS(ch)) { ch = more(); } // skip additional spaces
        return ch;
    }

    // nameStart / name lookup tables based on XML 1.1 http://www.w3.org/TR/2001/WD-xml11-20011213/
    protected static final int LOOKUP_MAX = 0x400;
    protected static final char LOOKUP_MAX_CHAR = (char)LOOKUP_MAX;
    //    protected static int lookupNameStartChar[] = new int[ LOOKUP_MAX_CHAR / 32 ];
    //    protected static int lookupNameChar[] = new int[ LOOKUP_MAX_CHAR / 32 ];
    protected static boolean lookupNameStartChar[] = new boolean[ LOOKUP_MAX ];
    protected static boolean lookupNameChar[] = new boolean[ LOOKUP_MAX ];

    private static final void setName(char ch)
        //{ lookupNameChar[ (int)ch / 32 ] |= (1 << (ch % 32)); }
    { lookupNameChar[ ch ] = true; }
    private static final void setNameStart(char ch)
        //{ lookupNameStartChar[ (int)ch / 32 ] |= (1 << (ch % 32)); setName(ch); }
    { lookupNameStartChar[ ch ] = true; setName(ch); }

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

        //      if(ch < LOOKUP_MAX_CHAR) return lookupNameStartChar[ ch ];
        //      else return ch <= '\u2027'
        //              || (ch >= '\u202A' &&  ch <= '\u218F')
        //              || (ch >= '\u2800' &&  ch <= '\uFFEF')
        //              ;
        //return false;
        //        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == ':'
        //          || (ch >= '0' && ch <= '9');
        //        if(ch < LOOKUP_MAX_CHAR) return (lookupNameStartChar[ (int)ch / 32 ] & (1 << (ch % 32))) != 0;
        //        if(ch <= '\u2027') return true;
        //        //[#x202A-#x218F]
        //        if(ch < '\u202A') return false;
        //        if(ch <= '\u218F') return true;
        //        // added pairts [#x2800-#xD7FF] | [#xE000-#xFDCF] | [#xFDE0-#xFFEF] | [#x10000-#x10FFFF]
        //        if(ch < '\u2800') return false;
        //        if(ch <= '\uFFEF') return true;
        //        return false;


        // else return (supportXml11 && ( (ch < '\u2027') || (ch > '\u2029' && ch < '\u2200') ...
    }

    //private final static boolean isNameChar(char ch) {
    protected boolean isNameChar(char ch) {
        //return isNameStartChar(ch);

        //        if(ch < LOOKUP_MAX_CHAR) return (lookupNameChar[ (int)ch / 32 ] & (1 << (ch % 32))) != 0;

        return (ch < LOOKUP_MAX_CHAR && lookupNameChar[ ch ])
            || (ch >= LOOKUP_MAX_CHAR && ch <= '\u2027')
            || (ch >= '\u202A' &&  ch <= '\u218F')
            || (ch >= '\u2800' &&  ch <= '\uFFEF')
            ;
        //return false;
        //        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == ':'
        //          || (ch >= '0' && ch <= '9');
        //        if(ch < LOOKUP_MAX_CHAR) return (lookupNameStartChar[ (int)ch / 32 ] & (1 << (ch % 32))) != 0;

        //else return
        //  else if(ch <= '\u2027') return true;
        //        //[#x202A-#x218F]
        //        else if(ch < '\u202A') return false;
        //        else if(ch <= '\u218F') return true;
        //        // added pairts [#x2800-#xD7FF] | [#xE000-#xFDCF] | [#xFDE0-#xFFEF] | [#x10000-#x10FFFF]
        //        else if(ch < '\u2800') return false;
        //        else if(ch <= '\uFFEF') return true;
        //else return false;
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

    //
    // Imported code from ASF Harmony project rev 770909
    // http://svn.apache.org/repos/asf/harmony/enhanced/classlib/trunk/modules/luni/src/main/java/java/lang/Character.java
    //

        private static int toCodePoint( char high, char low )
        {
            // See RFC 2781, Section 2.2
            // http://www.faqs.org/rfcs/rfc2781.html
            int h = ( high & 0x3FF ) << 10;
            int l = low & 0x3FF;
            return ( h | l ) + 0x10000;
        }

        private static final char MIN_HIGH_SURROGATE = '\uD800';
        private static final char MAX_HIGH_SURROGATE = '\uDBFF';

        private static boolean isHighSurrogate( char ch )
        {
            return ( MIN_HIGH_SURROGATE <= ch && MAX_HIGH_SURROGATE >= ch );
        }

        private static final int MIN_CODE_POINT = 0x000000;
        private static final int MAX_CODE_POINT = 0x10FFFF;
        private static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x10000;

        private static boolean isValidCodePoint( int codePoint )
        {
            return ( MIN_CODE_POINT <= codePoint && MAX_CODE_POINT >= codePoint );
        }

        private static boolean isSupplementaryCodePoint( int codePoint )
        {
            return ( MIN_SUPPLEMENTARY_CODE_POINT <= codePoint && MAX_CODE_POINT >= codePoint );
        }

        /**
         * TODO add javadoc
         *
         * @param codePoint
         * @return
         */
        public static char[] toChars( int codePoint )
        {
            if ( !isValidCodePoint( codePoint ) )
            {
                throw new IllegalArgumentException();
            }

            if ( isSupplementaryCodePoint( codePoint ) )
            {
                int cpPrime = codePoint - 0x10000;
                int high = 0xD800 | ( ( cpPrime >> 10 ) & 0x3FF );
                int low = 0xDC00 | ( cpPrime & 0x3FF );
                return new char[] { (char) high, (char) low };
            }

            return new char[] { (char) codePoint };
        }

}
