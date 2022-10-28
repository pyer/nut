package nut.model;

import nut.logging.Log;
import nut.model.Dependency;
import nut.model.ParserException;
import nut.model.ValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class Project implements java.io.Serializable
{
    private final static int NOT_VISITED = 0;
    private final static int VISITING = 1;
    private final static int VISITED = 2;

    private Log log;

    /**
     * A universally unique identifier for a project.
     * It is normal to use a fully-qualified name to
     * distinguish it from other projects with a similar name.
     */
    private String group = "";

    /**
     * The identifier for this artifact that is unique within the
     * group given by the group ID.
     * An artifact is something that is either produced or used by a project.
     */
    private String name = "";

    /**
     * The current version of the artifact produced by this project.
     */
    private String version = "";

    /**
     * The suffix of the built artifact, for example: jar, zip
     * It is usually defined in the pattern file.
     */
    private String packaging = "jar";

    /**
     * The pattern used to build this project produces, for example: jar, zip
     * modules is a special pattern type.
     */
    private String pattern = "modules";

    /**
     * A detailed description of the project,
     * whenever it needs to describe the project, such as on the web site.
     */
    private String description;

    /**
     * The state of the project managed by Sorter
     */
    private int state = NOT_VISITED;

    /**
     * The local repository where are all the artifact binaries.
     */
    private String localRepository;

    /**
     * The remote repository where the dependencies are downloaded from.
     */
    private String remoteRepository;

    // Other variables
    private List<String> modules = new ArrayList<String>();
    private List<Dependency> dependencies = new ArrayList<Dependency>();
    private List<Dependency> testDependencies = new ArrayList<Dependency>();
    private List<String> repositories = new ArrayList<String>();
    private Properties properties = new Properties();

    /**
     * The directory where is nut.yaml
     */
    private String baseDirectory = ".";

    /**
     * The main class for run
     */
    private String mainClass;
    private List<String> arguments = new ArrayList<String>();

    // Building time
    private long time;
    // Status
    private boolean buildDone    = false;
    private boolean buildSuccess = false;

    private boolean noopMode = false;

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    public Project()
    {
        this(false);
    }

    public Project(boolean noopMode)
    {
        this.noopMode = noopMode;
        this.log = new Log();
        this.time = System.currentTimeMillis();
        this.localRepository = System.getProperty( "nut.local" );
        this.remoteRepository = System.getProperty( "nut.remote" );
    }

    // For tests
    public Project(String name)
    {
        this(false);
        setName(name);
    }

    // ----------------------------------------------------------------------
    public boolean noop()
    {
        return this.noopMode;
    }

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

    public void setVersionMode(String mode)
    {
        this.version = this.version + mode;
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

    public void visiting()
    {
        state = VISITING;
    }

    public void visited()
    {
        state = VISITED;
    }

    public boolean isNotVisited()
    {
        return ( state == NOT_VISITED );
    }

    public boolean isVisiting()
    {
        return ( state == VISITING );
    }

    public boolean isVisited()
    {
        return ( state == VISITED );
    }

    /**
     * Layout public methods
     */
    public String getRepository()
    {
        return this.localRepository;
    }

    public void setRepository( String s )
    {
        this.localRepository = s;
    }

    public String getRemoteRepository()
    {
        return this.remoteRepository;
    }

    public String getBaseDirectory()
    {
        return this.baseDirectory;
    }

    public void setBaseDirectory(String s)
    {
        this.baseDirectory = s;
    }

    /**
     * This element specifies a directory containing the source of the project.
     * The generated layout system will compile the source
     * in this directory when the project is built.
     * The path given is relative to the project descriptor.
     */
    public String getSourceDirectory()
    {
        return properties.getProperty("sourceDirectory", "src/main/java");
    }

    /**
     * This element specifies a directory containing the resources of the project.
     * The path given is relative to the project descriptor.
     */
    public String getResourceDirectory()
    {
        return properties.getProperty("resourceDirectory", "src/main/resources");
    }

    /**
     * This element specifies a directory containing the web application sources.
     * The path given is relative to the project descriptor.
     */
    public String getWebappDirectory()
    {
        return properties.getProperty("webappDirectory", "src/main/webapp");
    }

    /**
     * This element specifies a directory containing the unit test
     * source of the project.
     * The generated layout system will compile this directory
     * when the project is being tested.
     * The path given is relative to the project descriptor.
     */
    public String getTestSourceDirectory()
    {
        return properties.getProperty("testSourceDirectory", "src/test/java");
    }

    /**
     * This element specifies a directory containing the unit test
     * resources of the project.
     * The path given is relative to the project descriptor.
     */
    public String getTestResourceDirectory()
    {
        return properties.getProperty("testResourceDirectory", "src/test/resources");
    }

    /**
     * The directory where all files generated by the layout are placed.
     */
    public String getTargetDirectory()
    {
        return properties.getProperty("targetDirectory", "target");
    }

    /**
     * The directory where compiled application classes are placed.
     */
    public String getOutputDirectory()
    {
        return properties.getProperty("outputDirectory","target/classes");
    }

    /**
     * The directory where compiled test classes are placed.
     */
    public String getTestOutputDirectory()
    {
        return properties.getProperty("testOutputDirectory","target/test-classes");
    }

    /**
     * The directory where test reports are placed.
     */
    public String getTestReportDirectory()
    {
        return properties.getProperty("testReportDirectory","target/test-reports");
    }


    public String getMainClass()
    {
        return this.mainClass;
    }

    public List<String> getArguments()
    {
        return this.arguments;
    }

    public void setArguments(List<String> arguments)
    {
        this.arguments = arguments;
    }

    /**
     * Other public methods
     */
    public String getId()
    {
        return getGroup() + ":" + getName() + ":" + getVersion() + ":" + getPackaging();
    }

    public String getPath()
    {
        String path = "/" + getGroup().replace('.', '/');
        return path + File.separator + getName() + "-" + getVersion() + "." + getPackaging();
    }

    public List<String> getModules()
    {
        return this.modules;
    }

    private void addModule( String value )
    {
        getModules().add( value );
    }

    public List<Dependency> getDependencies()
    {
        return this.dependencies;
    }

    private void addDependency( String path )
    {
        log.debug("addDependency("+path+")");
        getDependencies().add(new Dependency(path));
    }

    public List<Dependency> getTestDependencies()
    {
        return this.testDependencies;
    }

    private void addTestDependency( String path )
    {
        log.debug("addTestDependency("+path+")");
        getTestDependencies().add(new Dependency(path));
    }

    private void addProperty( String prop )
    {
        String[] parts = prop.split(": ",2);
        String key = parts[0].trim();
        String value = parts[1].trim();
        properties.put( key, value );
    }

    /**
     * Initialize properties from the pattern file
     * pattern must be declared before other properties in nut.yaml
     * 
     */
    private void readProperties( String pattern ) throws ParserException
    {
        String fileName = this.localRepository + File.separator + pattern + ".properties";
        try {
          FileInputStream in = new FileInputStream(fileName);
          properties.load(in);
          in.close();
        } catch ( Exception e ) {
          throw new ParserException( "Could not read the pattern '" + fileName + "'. Reason: " + e.getMessage(), e );
        }
    }

    // ----------------------------------------------------------------------
    // returns classpath for child process
    public String getDependenciesClassPath()
    {
      String classpath = getBaseDirectory() + File.separator + getOutputDirectory();
      for ( Dependency dependency : getDependencies() ) {
          classpath = classpath + ":" + this.localRepository + dependency.getPath();
      }
      return classpath;
    }

    // returns classpath for TestNG child process (all dependencies)
    public String getTestDependenciesClassPath()
    {
      String classpath = getDependenciesClassPath();
      classpath = classpath + ":" + getBaseDirectory() + File.separator + getTestOutputDirectory();
      for ( Dependency dependency : getTestDependencies() ) {
          classpath = classpath + ":" + this.localRepository + dependency.getPath();
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
    public String model()
    {
      StringBuffer buf = new StringBuffer();
      buf.append("group:     " + group + "\n");
      buf.append("name:      " + name + "\n");
      buf.append("version:   " + version + "\n");
      if (pattern.equals("modules")) {
        buf.append("modules:\n");
        modules.forEach (module -> buf.append("  - " + module + "\n"));
      } else {
        buf.append("packaging: " + packaging + "\n");
        //buf.append("pattern:   " + pattern + "\n");
        if ( mainClass != null ) {
          buf.append("mainClass: " + mainClass + "\n");
        }
        buf.append("dependencies:\n");
        for ( Dependency dep: dependencies ) {
            buf.append("  - " + dep.getPath() + "\n");
        }
        buf.append("testDependencies:\n");
        for ( Dependency dep: testDependencies ) {
            buf.append("  - " + dep.getPath() + "\n");
        }
        buf.append("properties:\n");
        properties.forEach((k, v) -> buf.append("  " + k + ": " + v + "\n"));
      }
      buf.append("\n");
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
                if ( "properties".equals(tag) ) {
                  addProperty(trimed);
                } else {
                  tag = parseLine(trimed);
                }
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
        pattern = value;
        readProperties(pattern);
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
      if ( "dependencies".equals(tag) ) {
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
