package nut.xml.pull;

/**
 * This exception is thrown to signal XML Pull Parser related faults.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class XmlPullParserException extends Exception {
    /*    public XmlPullParserException() {
          }*/

    public XmlPullParserException(String s) {
        super(s);
    }

    public XmlPullParserException(String msg, XmlPullParser parser, Throwable chain) {
        super ((msg == null ? "" : msg+" ")
               + (parser == null ? "" : " (line "+parser.getLineNumber()+", column "+parser.getColumnNumber()+") ")
               + (chain == null ? "" : "caused by: "+chain));
    }
}
