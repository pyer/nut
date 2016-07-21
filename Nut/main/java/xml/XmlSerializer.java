package nut.xml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class XmlSerializer {

    // properties/features
    protected String indentationString = "  ";
    protected String lineSeparator = System.getProperty("line.separator");

    protected Writer out;
    protected int depth;
    protected boolean startTagIncomplete;
    protected boolean seenTag;

    protected void writeIndent() throws IOException {
        out.write(lineSeparator);
        for (int i = 0; i < depth; i++) {
          out.write(indentationString);
        }
    }

    // this is special method that can be accessed directly to retrieve Writer serializer is using
    public Writer getWriter()
    {
        return out;
    }

    public void setOutput(Writer writer)
    {
        out = writer;
    }

    public void startDocument() throws IOException
    {
        if(out == null) {
            throw new IllegalStateException("setOutput() must called set before serialization can start");
        }
        startTagIncomplete = false;
        seenTag = false;
        depth = 0;
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write(lineSeparator);
    }

    public void endDocument() throws IOException
    {
        // check unclosed tag
        if(depth > 0) {
            throw new IllegalArgumentException("at least one tag is not close");
        }
        out.flush();
    }

    public XmlSerializer startTag (String name) throws IOException
    {
        if(name == null) {
            throw new IllegalArgumentException("end tag name cannot be null");
        }

        if(startTagIncomplete) {
            closeStartTag();
        }
        if(depth > 0 && seenTag) {
            writeIndent();
        }
        seenTag = true;
        depth++;
        out.write('<');
        out.write(name);
        startTagIncomplete = true;
        return this;
    }

    protected void closeStartTag() throws IOException {
        out.write('>');
        startTagIncomplete = false;
    }

    public XmlSerializer endTag(String name) throws IOException
    {
        if(name == null) {
            throw new IllegalArgumentException("end tag name cannot be null");
        }

        if(startTagIncomplete) {
            out.write(" />"); //space is added to make it easier to work in XHTML!!!
            depth--;
        } else {
            depth--;
            if(seenTag)
                writeIndent();
            out.write("</");
            out.write(name);
            out.write('>');
        }
        startTagIncomplete = false;
        seenTag = true;
        return this;
    }

    public XmlSerializer text (String text) throws IOException
    {
        if(startTagIncomplete) closeStartTag();
        seenTag = false;
        // escape '<', '&', '>' and <32 if necessary
        //TODO: escape ]] ?
        int pos = 0;
        for (int i = 0; i < text.length(); i++)
        {
            //TODO: check if doing char[] text.getChars() would be faster than getCharAt(i) ...
            char ch = text.charAt(i);
            if(ch == '&') {
                if(i > pos) out.write(text.substring(pos, i));
                out.write("&amp;");
                pos = i + 1;
            } else if(ch == '<') {
                if(i > pos) out.write(text.substring(pos, i));
                out.write("&lt;");
                pos = i + 1;
            } else if(ch == '>') {
                if(i > pos) out.write(text.substring(pos, i));
                out.write("&gt;");
                pos = i + 1;
            /* other characters are ignored
            } else if(ch < 32) {
                    //in XML 1.0 only legal character are #x9 | #xA | #xD
                    if( ch != 9 && ch != 10 && ch != 13) {
                        throw new IllegalStateException(
                            "character "+Integer.toString(ch)+" is not allowed in XML 1.0");
                    }
            */
            }
        }
        if(pos > 0) {
            out.write(text.substring(pos));
        } else {
            out.write(text);  // this is shortcut to the most common case
        }
        return this;
    }

    public void entityRef (String text)  throws IOException
    {
        if(startTagIncomplete) closeStartTag();
        seenTag = false;
        out.write('&');
        out.write(text); //escape?
        out.write(';');
    }

    public void processingInstruction (String text)  throws IOException
    {
        if(startTagIncomplete) closeStartTag();
        seenTag = false;
        out.write("<?");
        out.write(text); //escape?
        out.write("?>");
    }

    public void comment (String text)  throws IOException
    {
        if(startTagIncomplete) closeStartTag();
        seenTag = false;
        out.write("<!--");
        out.write(text); //escape?
        out.write("-->");
    }

}
