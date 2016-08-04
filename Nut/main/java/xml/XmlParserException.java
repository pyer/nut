package nut.xml;

/**
 * This exception is thrown to signal XML Pull Parser related faults.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class XmlParserException extends Exception {
    /*    public XmlParserException() {
          }*/

    public XmlParserException(String s) {
        super(s);
    }

    public XmlParserException(String msg, XmlParser parser, Throwable chain) {
        super ((msg == null ? "" : msg+" ")
               + (parser == null ? "" : " (line "+parser.getLineNumber()+", column "+parser.getColumnNumber()+") ")
               + (chain == null ? "" : "caused by: "+chain));
    }
}
