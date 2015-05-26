package nut.model;

import nut.logging.Log;
import nut.model.Model;
import nut.model.xmlWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EffectiveModel
{
    private Model model;

    public EffectiveModel( Model model )
    {
        this.model = model;
    }

    public String getEffectiveModel()
             throws IOException
    {
        xmlWriter modelWriter = new xmlWriter();
        StringWriter sWriter  = new StringWriter();
        modelWriter.writeModel( sWriter, model );
        return sWriter.toString();
    }

}
