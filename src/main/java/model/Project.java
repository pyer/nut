package nut.model;

import nut.logging.Log;
import nut.model.Dependency;
import nut.model.ParserException;
import nut.model.ValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Project implements java.io.Serializable
{
    private Log log;

    /**
     * Declares a parent file which contains common values, for example version or group.
     */
    private String parent;

    /**
     * A universally unique identifier for a project.
     * It is normal to use a fully-qualified name to
     * distinguish it from other projects with a similar name.
     */
    private String group;

    /**
     * The identifier for this artifact that is unique within the
     * group given by the group ID.
     * An artifact is something that is either produced or used by a project.
     */
    private String name;

    /**
     * The current version of the artifact produced by this project.
     */
    private String version;

    /**
     * The suffix of the built artifact, for example: jar, zip
     * It is usually defined in the pattern file.
     */
    private String packaging = "jar";

    /**
     * The pattern used to build this this project produces, for example: jar, zip
     * modules is a special pattern type.
     */
    private String pattern = "modules";

    /**
     * A detailed description of the project,
     * whenever it needs to describe the project, such as on the web site.
     */
    private String description;

    /**
     * The local repository where are all the artifact binaries
     */
    private String repository;

    // Other variables
    private List<String> modules;
    private List<Dependency> dependencies;
    private List<Dependency> testDependencies;
    private List<String> repositories;
    private Properties properties;

    /**
     * The directory where is nut.yml
     */
    private String baseDirectory = ".";

    /**
     * This element specifies a directory containing the source of the project.
     * The generated layout system will compile the source
     * in this directory when the project is built.
     * The path given is relative to the project descriptor.
     */
    private String sourceDirectory = "src/main/java";

    /**
     * This element specifies a directory containing the resources of the project.
     * The path given is relative to the project descriptor.
     */
    private String resourceDirectory = "src/main/resources";

    /**
     * This element specifies a directory containing the web application sources.
     * The path given is relative to the project descriptor.
     */
    private String webappDirectory = "src/main/webapp";

    /**
     * This element specifies a directory containing the unit test
     * source of the project.
     * The generated layout system will compile this directory
     * when the project is being tested.
     * The path given is relative to the project descriptor.
     */
    private String testSourceDirectory = "src/test/java";

    /**
     * This element specifies a directory containing the unit test
     * resources of the project.
     * The path given is relative to the project descriptor.
     */
    private String testResourceDirectory = "src/test/resources";

    /**
     * The directory where all files generated by the layout are
     * placed.
     */
    private String targetDirectory = "target";

    /**
     * The directory where compiled application classes are placed.
     */
    private String outputDirectory = "target/classes";

    /**
     * The directory where compiled test classes are placed.
     */
    private String testOutputDirectory = "target/test-classes";

    /**
     * The directory where test reports are placed.
     */
    private String testReportDirectory = "target/test-reports";

    /**
     * The main class for run
     */
    private String mainClass;

    // Building time
    private long time;
    // Status
    private boolean buildDone;
    private boolean buildSuccess;

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    public Project()
    {
        this.log = new Log();
        this.time = System.currentTimeMillis();
        this.buildDone = false;
        this.buildSuccess = false;
        this.repository = System.getProperty( "nut.home" );
    }

    // ----------------------------------------------------------------------
    public boolean isBuilt()
    {
        return this.buildDone;
    }

    public void start()
    {
        this.time = System.currentTimeMillis();
        this.buildDone = true;
    }

    public boolean isSuccessful()
    {
        return buildSuccess;
    }

    public void success()
    {
        this.buildSuccess = true;
        this.time = System.currentTimeMillis() - this.time;
    }

    public void failure()
    {
        this.buildSuccess = false;
        this.time = System.currentTimeMillis() - this.time;
    }

    public long getTime()
    {
        return time;
    }

    // ----------------------------------------------------------------------

    /**
     * Public methods
     */

    public String getParent()
    {
        return this.parent;
    }

    public String getGroup()
    {
        return this.group;
    }

    public void setGroup(String s)
    {
        this.group = s;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String s)
    {
        this.name = s;
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setVersion(String s)
    {
        this.version = s;
    }

    public String getPackaging()
    {
        return this.packaging;
    }

    public void setPackaging(String s)
    {
        this.packaging = s;
    }

    public String getPattern()
    {
        return this.pattern;
    }

    public String getDescription()
    {
        return this.description;
    }

    /**
     * Layout public methods
     */
    public String getRepository()
    {
        return this.repository;
    }

    public void setRepository(String s)
    {
        this.repository = s;
    }

    public String getBaseDirectory()
    {
        return this.baseDirectory;
    }

    public void setBaseDirectory(String s)
    {
        this.baseDirectory = s;
    }

    public String getSourceDirectory()
    {
        return this.sourceDirectory;
    }

    public String getResourceDirectory()
    {
        return this.resourceDirectory;
    }

    public String getWebappDirectory()
    {
        return this.webappDirectory;
    }

    public String getTestSourceDirectory()
    {
        return this.testSourceDirectory;
    }

    public String getTestResourceDirectory()
    {
        return this.testResourceDirectory;
    }

    public String getTargetDirectory()
    {
        return this.targetDirectory;
    }

    public String getOutputDirectory()
    {
        return this.outputDirectory;
    }

    public String getTestOutputDirectory()
    {
        return this.testOutputDirectory;
    }

    public String getTestReportDirectory()
    {
        return this.testReportDirectory;
    }

    public String getMainClass()
    {
        return this.mainClass;
    }

    /**
     * Other public methods
     */
    public String getId()
    {
        return getGroup() + ":" + getName() + ":" + getVersion() + ":" + getPackaging();
    }

    public String getPathName()
    {
        String path = "/" + getGroup().replace('.', '/');
        return path + File.separator + getName() + "-" + getVersion() + "." + getPackaging();
    }

    public List<String> getModules()
    {
        if ( this.modules == null ) {
            this.modules = new ArrayList<String>();
        }
        return this.modules;
    }

    private void addModule( String value )
    {
        getModules().add( value );
    }

    public List<Dependency> getDependencies()
    {
        if ( this.dependencies == null ) {
            this.dependencies = new ArrayList<Dependency>();
        }
        return this.dependencies;
    }

    private void addDependency( String path )
    {
        log.debug("addDependency("+path+")");
        getDependencies().add(new Dependency(path));
    }

    public List<Dependency> getTestDependencies()
    {
        if ( this.testDependencies == null ) {
            this.testDependencies = new ArrayList<Dependency>();
        }
        return this.testDependencies;
    }

    private void addTestDependency( String path )
    {
        log.debug("addTestDependency("+path+")");
        getTestDependencies().add(new Dependency(path));
    }

    public Properties getProperties()
    {
        if ( this.properties == null ) {
            this.properties = new Properties();
        }
        return this.properties;
    }

    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    private void addProperty( String prop )
    {
        String[] parts = prop.split("=",2);
        String key = parts[0].trim();
        String value = parts[1].trim();
        getProperties().put( key, value );
    }

    /**
     * Add all system properties "nut.*" to model
     */
    public void addProperties()
    {
      for ( Enumeration en = System.getProperties().propertyNames(); en.hasMoreElements(); ) {
        String key = (String) en.nextElement();
        if( key.startsWith( "nut." ) ) {
            getProperties().put( key, System.getProperty(key) );
        }
      }
    }

    // ----------------------------------------------------------------------
    // returns classpath for child process
    public String getDependenciesClassPath()
    {
      String classpath = this.baseDirectory + File.separator + this.outputDirectory;
      for ( Iterator it = getDependencies().iterator(); it.hasNext(); ) {
          Dependency dep = (Dependency) it.next();
          classpath = classpath + ":" + this.repository + dep.getPath();
      }
      return classpath;
    }

    // returns classpath for TestNG child process (all dependencies)
    public String getTestDependenciesClassPath()
    {
      String classpath = getDependenciesClassPath();
      classpath = classpath + ":" + this.baseDirectory + File.separator + this.testOutputDirectory;
      for ( Iterator it = getTestDependencies().iterator(); it.hasNext(); ) {
          Dependency dep = (Dependency) it.next();
          classpath = classpath + ":" + this.repository + dep.getPath();
      }
      return classpath;
    }

    // ----------------------------------------------------------------------

    /**
     * Whole project validation
     */
    public void validate() throws ValidationException
    {
        String ID_REGEX = "[A-Za-z0-9_\\-.]+";
        validateStringNotEmpty( "group", group );
        if ( !group.matches( ID_REGEX ) )
            throw new ValidationException( "group '" + group + "' does not match a valid pattern." );
        validateStringNotEmpty( "name", name );
        if ( !name.matches( ID_REGEX ) )
            throw new ValidationException( "name '" + name + "' does not match a valid pattern." );

        validateStringNotEmpty( "version", version );
        validateStringNotEmpty( "pattern", pattern );

        if ( "modules".equals( pattern ) ) {
            if ( getModules().isEmpty() ) {
                throw new ValidationException( "List of modules is empty." );
            }
        } else {
            validateStringNotEmpty( "packaging", packaging );
            if ( !getModules().isEmpty() ) {
                throw new ValidationException( "Pattern '" + pattern + "' is invalid. Aggregator projects require 'modules' as pattern." );
            }
        }
    }

    /**
     * Field validation
     */
    private void validateStringNotEmpty( String fieldName, String string )
        throws ValidationException
    {
        if ( string == null )
            throw new ValidationException( "'" + fieldName + "' is null." );
        if ( string.length() <1 )
            throw new ValidationException( "'" + fieldName + "' is empty." );
    }

    // ----------------------------------------------------------------------
    public boolean equals( Object other )
    {
        if ( other == this ) {
            return true;
        } else if ( !( other instanceof Project ) ) {
            return false;
        } else {
            Project otherProject = (Project) other;
            return getId().equals( otherProject.getId() );
        }
    }

    // ----------------------------------------------------------------------
    public String effectiveNut()
    {
      StringBuffer buf = new StringBuffer();
      if (parent != null)
        buf.append("parent:    " + parent + "\n");
      buf.append("group:     " + group + "\n");
      buf.append("name:      " + name + "\n");
      buf.append("version:   " + version + "\n");
      buf.append("packaging: " + packaging + "\n");
      if (pattern.equals("modules")) {
        buf.append("modules:\n");
        for ( Iterator i = modules.iterator(); i.hasNext(); ) {
          buf.append("  - " + (String) i.next() + "\n");
        }
      } else {
        buf.append("pattern :  " + pattern + "\n");
        buf.append("sourceDirectory:       " + sourceDirectory + "\n");
        buf.append("resourceDirectory:     " + resourceDirectory + "\n");
        buf.append("webappDirectory:       " + webappDirectory + "\n");
        buf.append("testSourceDirectory:   " + testSourceDirectory + "\n");
        buf.append("testResourceDirectory: " + testResourceDirectory + "\n");
        buf.append("targetDirectory:       " + targetDirectory + "\n");
        buf.append("outputDirectory:       " + outputDirectory + "\n");
        buf.append("testOutputDirectory:   " + testOutputDirectory + "\n");
        buf.append("testReportDirectory:   " + testReportDirectory + "\n");
        buf.append("mainClass: " + mainClass + "\n");
        buf.append("properties:\n");
        buf.append("dependencies:\n");
        buf.append("testDependencies:\n");
      }
      return buf.toString();
    }


    // ----------------------------------------------------------------------
    public void parseFile( File file ) throws ParserException
    {
        try {
          String line;
          String tag = null;
          log.debug("Parsing " + file.getPath());
          BufferedReader reader = new BufferedReader(new FileReader(file));
          while ((line = reader.readLine()) != null) {
            String trimed = line.trim();
            log.debug(trimed);
            if ( !(trimed.startsWith("#") || trimed.isEmpty()) ) {
              if ( trimed.startsWith("- ") ) {
                parseList(tag, trimed);
              } else {
                tag = parseLine(trimed);
              }
            }
          }
          reader.close();
        } catch ( FileNotFoundException e ) {
          throw new ParserException( "Could not find the file '" + file.getPath() + "'.", e );
        } catch ( IOException e ) {
          throw new ParserException( "Could not read the file '" + file.getPath() + "'.", e );
        }
    }

    private String parseLine(String line) throws ParserException
    {
      String[] parts = line.split(":",2);
      String key = parts[0].trim();
      String value = parts[1].trim();
      if ( "name".equals(key) ) {
        name = value;
      } else if ( "group".equals(key) ) {
        group = value;
      } else if ( "version".equals(key) ) {
        version = value;
      } else if ( "packaging".equals(key) ) {
        packaging = value;
        if ( "modules".equals(pattern) ) {
          pattern = value;
        }
      } else if ( "pattern".equals(key) ) {
        pattern = repository + File.separator + value + ".yml";
        parseFile( new File(pattern));
//      } else if ( "parent".equals(key) ) {
//        parent = baseDirectory + File.separator + value;
//        parseFile( new File(parent));
      } else if ( "sourceDirectory".equals(key) ) {
        sourceDirectory = value;
      } else if ( "resourceDirectory".equals(key) ) {
        resourceDirectory = value;
      } else if ( "webappDirectory".equals(key) ) {
        webappDirectory = value;
      } else if ( "testSourceDirectory".equals(key) ) {
        testSourceDirectory = value;
      } else if ( "testResourceDirectory".equals(key) ) {
        testResourceDirectory = value;
      } else if ( "targetDirectory".equals(key) ) {
        targetDirectory = value;
      } else if ( "outputDirectory".equals(key) ) {
        outputDirectory = value;
      } else if ( "testOutputDirectory".equals(key) ) {
        testOutputDirectory = value;
      } else if ( "testReportDirectory".equals(key) ) {
        testReportDirectory = value;
      } else if ( "mainClass".equals(key) ) {
        mainClass = value;
      } else if ( "properties".equals(key) ) {
        checkList(key, value);
      } else if ( "dependencies".equals(key) ) {
        checkList(key, value);
      } else if ( "testDependencies".equals(key) ) {
        checkList(key, value);
      } else if ( "modules".equals(key) ) {
        checkList(key, value);
      } else {
        throw new ParserException( "Unrecognized tag: '" + key + "'", null );
      }
      return key;
    }

    private void parseList(String tag, String line) throws ParserException
    {
      String value = line.substring(2);
      if ( value.isEmpty() ) {
        throw new ParserException("Empty value in list '" + tag + "'.", null);
      }
      if ( "properties".equals(tag) ) {
        addProperty(value);
      } else if ( "dependencies".equals(tag) ) {
        addDependency(value);
      } else if ( "testDependencies".equals(tag) ) {
        addTestDependency(value);
      } else if ( "modules".equals(tag) ) {
        addModule(value);
      }
    }

    // The list name must not have a value
    private void checkList(String key, String value) throws ParserException
    {
      if ( !value.isEmpty() ) {
        throw new ParserException("Invalid '" + key + "' tag: it is a list. ", null);
      }
    }

}
