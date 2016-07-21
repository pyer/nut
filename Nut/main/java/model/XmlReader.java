package nut.model;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;
import nut.model.Repository;

import nut.xml.pull.XmlPullParser;
import nut.xml.pull.XmlPullParserException;

public class XmlReader {

    /**
     * Method parseGoal.
     *
     * @param tag   which is read from xml file
     * @param name  of the tag
     * @param set   HashSet of valid values
     */
    private boolean tagEquals( String value, String name, Set<String> set )
        throws XmlPullParserException
    {
      boolean ret = value.equals( name );
      if ( ret ) {
         if ( set.contains( name ) )
            throw new XmlPullParserException( "Duplicated tag: '" + name + "'" );
         set.add( name );
      }
      return ret;
    }

    /**
     * Method parseGoal.
     *
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Goal
     */
    private Goal parseGoal( XmlPullParser parser )
        throws IOException, XmlPullParserException
    {
        Goal goal = new Goal();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( tagEquals( parser.getName(), "name", parsed ) ) {
                goal.setName( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "class", parsed ) ) {
                goal.setClassName( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "configuration", parsed ) ) {
                while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                    String key = parser.getName();
                    String value = parser.nextText().trim();
                    goal.setConfigurationValue( key, value );
                }
            } else {
                throw new XmlPullParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
            }
        }
        return goal;
    }

    /**
     * Method parseBuild.
     *
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Build
     */
    private Build parseBuild( XmlPullParser parser )
        throws IOException, XmlPullParserException
    {
        Build build = new Build();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( tagEquals( parser.getName(), "sourceDirectory", parsed ) ) {
                build.setSourceDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "resourceDirectory", parsed ) ) {
                build.setResourceDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "testSourceDirectory", parsed ) ) {
                build.setTestSourceDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "testResourceDirectory", parsed ) ) {
                build.setTestResourceDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "targetDirectory", parsed ) ) {
                build.setTargetDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "outputDirectory", parsed ) ) {
                build.setOutputDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "testOutputDirectory", parsed ) ) {
                build.setTestOutputDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "testReportDirectory", parsed ) ) {
                build.setTestReportDirectory( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "goals", parsed ) ) {
                List<Goal> goals = new ArrayList<Goal>();
                build.setGoals( goals );
                while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                    if ( parser.getName().equals( "goal" ) ) {
                        goals.add( parseGoal( parser ) );
                    } else {
                        throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                    }
                }
            } else {
                throw new XmlPullParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
            }
        }
        return build;
    }

    /**
     * Method parseDependency.
     *
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Dependency
     */
    private Dependency parseDependency( XmlPullParser parser )
        throws IOException, XmlPullParserException
    {
        Dependency dependency = new Dependency();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( tagEquals( parser.getName(), "groupId", parsed ) ) {
                dependency.setGroupId( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "artifactId", parsed ) ) {
                dependency.setArtifactId( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "version", parsed ) ) {
                dependency.setVersion( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "type", parsed ) ) {
                dependency.setType( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "scope", parsed ) ) {
                dependency.setScope( parser.nextText() );
            } else {
                throw new XmlPullParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
            }
        }
        return dependency;
    }

    /**
     * Method parseRepository.
     *
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Repository
     */
    private Repository parseRepository( XmlPullParser parser )
        throws IOException, XmlPullParserException
    {
        Repository repository = new Repository();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( tagEquals( parser.getName(), "name", parsed ) ) {
                repository.setName( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "layout", parsed ) ) {
                repository.setLayout( parser.nextText() );
            } else if ( tagEquals( parser.getName(), "url", parsed ) ) {
                repository.setURL( parser.nextText() );
            } else {
                throw new XmlPullParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
            }
        }
        return repository;
    }

    /**
     * Method parseModel.
     *
     * @param sReader
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    public Model parseModel( StringReader sReader )
        throws IOException, XmlPullParserException
    {
        XmlPullParser parser = new XmlPullParser();
        parser.setInput( sReader );
        parser.next();

        Model model = new Model();
        Set<String> parsed = new HashSet<String>();
        int eventType = parser.getEventType();
        boolean foundRoot = false;
        while ( eventType != XmlPullParser.END_DOCUMENT ) {
            if ( eventType == XmlPullParser.START_TAG ) {
                if ( parser.getName().equals( "project" ) ) {
                    foundRoot = true;
                } else if ( tagEquals( parser.getName(), "modelVersion", parsed ) ) {
                    model.setModelVersion( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "modelEncoding", parsed ) ) {
                    model.setModelEncoding( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "parent", parsed ) ) {
                    model.setParent( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "groupId", parsed ) ) {
                    model.setGroupId( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "artifactId", parsed ) ) {
                    model.setArtifactId( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "version", parsed ) ) {
                    model.setVersion( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "packaging", parsed ) ) {
                    model.setPackaging( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "description", parsed ) ) {
                    model.setDescription( parser.nextText() );
                } else if ( tagEquals( parser.getName(), "build", parsed ) ) {
                    model.setBuild( parseBuild( parser ) );
                } else if ( tagEquals( parser.getName(), "modules", parsed ) ) {
                    List<String> modules = new ArrayList<String>();
                    model.setModules( modules );
                    while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                        if ( parser.getName().equals( "module" ) ) {
                            modules.add( parser.nextText() );
                        } else {
                            throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( tagEquals( parser.getName(), "dependencies", parsed ) ) {
                    List<Dependency> dependencies = new ArrayList<Dependency>();
                    model.setDependencies( dependencies );
                    while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                        if ( parser.getName().equals( "dependency" ) ) {
                            dependencies.add( parseDependency( parser ) );
                        } else {
                            throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( tagEquals( parser.getName(), "repositories", parsed ) ) {
                    List<Repository> repositories = new ArrayList<Repository>();
                    model.setRepositories( repositories );
                    while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                        if ( parser.getName().equals( "repository" ) ) {
                            repositories.add( parseRepository( parser ) );
                        } else {
                            throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( tagEquals( parser.getName(), "properties", parsed ) ) {
                    while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                        String key = parser.getName();
                        String value = parser.nextText().trim();
                        model.addProperty( key, value );
                    }
                } else {
                    throw new XmlPullParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
                }
            }
            eventType = parser.next();
        }
        if (!foundRoot) {
            throw new XmlPullParserException( "'project' tag not found", parser, null );
        }
        return model;
    }
}
