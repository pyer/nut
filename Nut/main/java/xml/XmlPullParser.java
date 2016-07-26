package nut.xml.pull;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;

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
    private static int START_DOCUMENT = 0;

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
    private static int END_DOCUMENT = 1;

    /**
     * Returned from getEventType(),
     * <a href="#next()">next()</a>, <a href="#nextToken()">nextToken()</a> when
     * a start tag was read.
     * The name of start tag is available from getName()
     * @see #next
     * @see #nextToken
     * @see #getName
     */
    private static int START_TAG = 2;

    /**
     * Returned from getEventType(), <a href="#next()">next()</a>, or
     * <a href="#nextToken()">nextToken()</a> when an end tag was read.
     * The name of start tag is available from getName()
     * @see #next
     * @see #nextToken
     * @see #getName
     */
    private static int END_TAG = 3;


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
     * return unnormalized content in the case of nextToken().
     *
     * @see #next
     * @see #nextToken
     * @see #getText
     */
    private static int TEXT = 4;

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
    private static int COMMENT = 5;

    /**
     * An XML processing instruction declaration was just read. This
     * event type is available only via <a href="#nextToken()">nextToken()</a>.
     * getText() will return text that is inside the processing instruction.
     * Calls to next() will skip processing instructions automatically.
     * @see #nextToken
     * @see #getText
     */
    private static int PROCESSING_INSTRUCTION = 6;

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
    private static int IGNORABLE_WHITESPACE = 7;

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
            "COMMENT",
            "PROCESSING_INSTRUCTION",
            "IGNORABLE_WHITESPACE"
    };


    // ----------------------------------------------------------------------------
    private static final boolean TRACE_SIZING = false;


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


    // input buffer management
    protected static final int READ_CHUNK_SIZE = 8*1024; //max data chars in one read() call
    protected StringReader reader;

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
    protected boolean usePC;


    protected boolean seenStartTag;
    protected boolean seenEndTag;
    protected boolean pastEndTag;

    protected String text;

    protected String xmlDeclVersion;
    protected String xmlDeclContent;

    /**
     * Class constructor
     */
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

        preventBufferCompaction = false;
        bufAbsoluteStart = 0;
        bufEnd = bufStart = 0;
        pos = posStart = posEnd = 0;

        pcEnd = pcStart = 0;

        usePC = false;

        seenStartTag = false;
        seenEndTag = false;
        pastEndTag = false;

        xmlDeclVersion = null;
        xmlDeclContent = null;
    }

    /**
     * return true while the parser has not reach the end of document
     */
    public boolean isNotEndOfDocument() {
      return eventType != END_DOCUMENT;
    }

    /**
     * return true if the parser is at the start of a tag
     */
    public boolean isStartOfTag() {
      return eventType == START_TAG;
    }

    /**
    * return true if the parser is at the start of a tag
     */
    public boolean nextTag() throws XmlPullParserException, IOException
    {
        next();
        if(eventType == TEXT && isWhitespace()) {  // skip whitespace
            next();
        }
        if (eventType != START_TAG && eventType != END_TAG) {
            throw new XmlPullParserException("expected START_TAG or END_TAG not "
                                                 +TYPES[ eventType ], this, null);
        }
        return eventType == START_TAG;
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
        if(eventType == TEXT) {
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
        } else {
            return null;
        }
    }

    public String getText()
    {
        if(eventType == START_DOCUMENT || eventType == END_DOCUMENT) {
            return null;
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
        if(eventType != START_TAG) {
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
                        +TYPES[ eventType ], this, null);
            }
            return result;
        } else if(eventType == END_TAG) {
            return "";
        } else {
            throw new XmlPullParserException(
                "parser must be on START_TAG or TEXT to read text", this, null);
        }
    }

    public int next()
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
            char ch = more();

            posStart = pos - 1; // VERY IMPORTANT: this is correct start of event!!!

            // when true there is some potential event TEXT to return - keep gathering
            boolean hadCharData = false;

            // when true TEXT data is not continuous (like <![CDATA[text]]>) and requires PC merging
            boolean needsMerging = false;

            MAIN_LOOP:
            while(true) {
                // work on MARKUP
                if(ch == '<') {
                    ch = more();
                    if(ch == '/') {
                        if(hadCharData) {
                            seenEndTag = true;
                            //posEnd = pos - 2;
                            return eventType = TEXT;
                        }
                        return eventType = parseEndTag();
                    } else if(ch == '!') {
                        ch = more();
                        if(ch == '-') {
                            parseComment();
                            if( !usePC && hadCharData ) {
                                needsMerging = true;
                            } else {
                                posStart = pos;  //completely ignore comment
                            }
                        } else {
                            throw new XmlPullParserException(
                                "unexpected character in markup "+printable(ch), this, null);
                        }
                    } else if(ch == '?') {
                        parsePI();
                        if( !usePC && hadCharData ) {
                            needsMerging = true;
                        } else {
                            posStart = pos;  //completely ignore PI
                        }

                    } else if( isNameStartChar(ch) ) {
                        if(hadCharData) {
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

                } else {
                    if(needsMerging) {
                        //assert usePC == false;
                        joinPC();  // posEnd is already set correctly!!!
                        //posStart = pos  -  1;
                        needsMerging = false;
                    }
                    hadCharData = true;

                    boolean normalizedCR = false;
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

        char ch = more();
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
        boolean gotS = false;
        posStart = pos - 1;
        boolean normalizedCR = false;
        while(true) {
            // deal with Misc
            // [27] Misc ::= Comment | PI | S
            // deal with docdecl --> mark it!
            // else parseStartTag seen <[^/]
            if(ch == '<') {
                ch = more();
                if(ch == '?') {
                    // check if it is 'xml'
                    // deal with XMLDecl
                    boolean isXMLDecl = parsePI();
                } else if(ch == '!') {
                    ch = more();
                    if(ch == '-') {
                        parseComment();
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
        boolean normalizedCR = false;
        try {
            // epilog: Misc*
            char ch = more();
            posStart = pos - 1;
            if(!reachedEnd) {
                while(true) {
                    // deal with Misc
                    // [27] Misc ::= Comment | PI | S
                    if(ch == '<') {
                        ch = more();
                        if(reachedEnd) {
                            break;
                        }
                        if(ch == '?') {
                            // check if it is 'xml'
                            // deal with XMLDecl
                            parsePI();

                        } else if(ch == '!') {
                            ch = more();
                            if(reachedEnd) {
                                break;
                            }
                            if(ch == '-') {
                                parseComment();
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

    protected void parseComment()
        throws XmlPullParserException, IOException
    {
        // implements XML 1.0 Section 2.5 Comments

        //ASSUMPTION: seen <!-
        char ch = more();
        if(ch != '-') throw new XmlPullParserException(
                "expected <!-- for comment start", this, null);

        final int curLine = lineNumber;
        final int curColumn = columnNumber;
        try {
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

            }

        } catch(EOFException ex) {
            // detect EOF and create meaningful error ...
            throw new XmlPullParserException(
                "comment started on line "+curLine+" and column "+curColumn+" was not closed",
                this, ex);
        }
    }

    protected boolean parsePI()
        throws XmlPullParserException, IOException
    {
        // implements XML 1.0 Section 2.6 Processing Instructions

        // [16] PI ::= '<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
        // [17] PITarget         ::=    Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
        //ASSUMPTION: seen <?
        final int curLine = lineNumber;
        final int curColumn = columnNumber;
        int piTargetStart = pos + bufAbsoluteStart;
        int piTargetEnd = -1;
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
                                final int off = piTargetStart - bufAbsoluteStart + 3;
                                final int len = pos - 2 - off;
                                xmlDeclContent = new String(buf, off, len);
                                return false;
                            }
                        }
                    }
                    seenQ = false;
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
        return true;
    }

    /**
     * Make sure that we have enough space to keep element stack if passed size.
     * It will always create one additional slot then current depth
     */
    protected void ensureElementsCapacity() {
        final int elStackSize = elName != null ? elName.length : 0;
        if( (depth + 1) >= elStackSize) {
            // we add at least one extra slot ...
            final int newSize = (depth >= 7 ? 2 * depth : 8) + 2; // = lucky 7 + 1 //25
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


    //    protected final static char[] VERSION = {'v','e','r','s','i','o','n'};
    //    protected final static char[] NCODING = {'n','c','o','d','i','n','g'};

    protected final static char[] VERSION = "version".toCharArray();
    protected final static char[] NCODING = "ncoding".toCharArray();

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


    protected void fillBuf() throws IOException, XmlPullParserException {
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
        // at least one character must be read or error
        final int len = buf.length - bufEnd > READ_CHUNK_SIZE ? READ_CHUNK_SIZE : buf.length - bufEnd;
        //final int len=1;
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

}
