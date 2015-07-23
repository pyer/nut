package nut.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

public class JsonSerializer {

    // properties/features
    protected String indentationString = "  ";
    protected String lineSeparator = System.getProperty("line.separator");

    protected Writer out;
    protected int depth;
    protected List<Boolean> comma = new ArrayList<Boolean>();

    // protected method
    protected void writeIndent() throws IOException {
        out.write(lineSeparator);
        for (int i = 0; i < depth+1; i++) {
          out.write(indentationString);
        }
    }

    protected void comma() throws IOException {
        if( comma.get(depth) ) {
          out.write(", ");
        } else {
          comma.set(depth,true);
        }
//        System.out.println( out.toString() );
//        System.out.println( "comma: depth=" + String.valueOf(depth) );
    }

    protected void comma( boolean status) {
        if( depth<comma.size() ) {
          comma.set(depth,status);
        } else {
          comma.add(status);
        }
//        System.out.println( out.toString() );
//        System.out.println( "comma: " + Boolean.valueOf(status) + " depth=" + String.valueOf(depth) );
    }

    // constructor
    public JsonSerializer(Writer writer)
        throws IOException
    {
        if(writer == null) {
            throw new IllegalStateException("Undefined Writer");
        }
        out = writer;
    }

    // public methods
    public void startDocument () throws IOException
    {
        out.write("{ ");
        depth = 0;
        comma(false);
    }

    public void endDocument() throws IOException
    {
        depth--;
        writeIndent();
        out.write("}");
        out.flush();
    }

    public void startObject() throws IOException
    {
        comma();
        writeIndent();
        out.write("{ ");
        depth++;
        comma(false);
    }

    public void startObject( String name) throws IOException
    {
        comma();
        writeIndent();
        if ( name == null ) {
          out.write("{ ");
        } else {
          out.write('"');
          out.write(name);
          out.write("\": { ");
        }
        depth++;
        comma(false);
    }

    public void endObject() throws IOException
    {
        comma(false);
        depth--;
        writeIndent();
        out.write("}");
    }

    public void startList(String name) throws IOException
    {
        comma();
        writeIndent();
        out.write('"');
        out.write(name);
        out.write("\": [ ");
        depth++;
        comma(false);
    }

    public void endList() throws IOException
    {
        comma(false);
        depth--;
        writeIndent();
        out.write("]");
    }

    public void element(String value) throws IOException
    {
      if( value == null )
        return;

      comma();
      writeIndent();
      out.write('"');
      out.write(value.replace("\n"," "));
      out.write('"');
    }

    public void element(String key, String value) throws IOException
    {
      if( value == null )
        return;

      comma();
      writeIndent();
      out.write('"');
      out.write(key);
      out.write("\": \"");
      out.write(value.replace("\n"," "));
      out.write('"');
    }

}
