package nut.model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Locale;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;
import nut.model.Repository;

import nut.xml.pull.XmlPullParser;
import nut.xml.pull.XmlPullParserException;

public class xmlReader {

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
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( parser.getName().equals( "name" )  ) {
                if ( parsed.contains( "name" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                goal.setName( parser.nextText() );
            } else if ( parser.getName().equals( "type" )  ) {
                if ( parsed.contains( "type" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                goal.setType( parser.nextText() );
            } else if ( parser.getName().equals( "configuration" )  ) {
                if ( parsed.contains( "configuration" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
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
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( parser.getName().equals( "sourceDirectory" ) ) {
                if ( parsed.contains( "sourceDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setSourceDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "resourceDirectory" ) ) {
                if ( parsed.contains( "resourceDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setResourceDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "testSourceDirectory" ) ) {
                if ( parsed.contains( "testSourceDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setTestSourceDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "testResourceDirectory" ) ) {
                if ( parsed.contains( "testResourceDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setTestResourceDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "targetDirectory" )  ) {
                if ( parsed.contains( "targetDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setTargetDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "outputDirectory" )  ) {
                if ( parsed.contains( "outputDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setOutputDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "testOutputDirectory" )  ) {
                if ( parsed.contains( "testOutputDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setTestOutputDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "testReportDirectory" )  ) {
                if ( parsed.contains( "testReportDirectory" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                build.setTestReportDirectory( parser.nextText() );
            } else if ( parser.getName().equals( "goals" )  ) {
                if ( parsed.contains( "goals" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                java.util.List<Goal> goals = new java.util.ArrayList<Goal>();
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
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( parser.getName().equals( "groupId" )  ) {
                if ( parsed.contains( "groupId" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                dependency.setGroupId( parser.nextText() );
            } else if ( parser.getName().equals( "artifactId" )  ) {
                if ( parsed.contains( "artifactId" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                dependency.setArtifactId( parser.nextText() );
            } else if ( parser.getName().equals( "version" )  ) {
                if ( parsed.contains( "version" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                dependency.setVersion( parser.nextText() );
            } else if ( parser.getName().equals( "type" )  ) {
                if ( parsed.contains( "type" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                dependency.setType( parser.nextText() );
            } else if ( parser.getName().equals( "scope" )  ) {
                if ( parsed.contains( "scope" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
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
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG ) {
            if ( parser.getName().equals( "name" )  ) {
                if ( parsed.contains( "name" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                repository.setName( parser.nextText() );
            } else if ( parser.getName().equals( "layout" )  ) {
                if ( parsed.contains( "layout" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
                repository.setLayout( parser.nextText() );
            } else if ( parser.getName().equals( "url" )  ) {
                if ( parsed.contains( "url" ) )
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                parsed.add( parser.getName() );
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
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        int eventType = parser.getEventType();
        boolean foundRoot = false;
        while ( eventType != XmlPullParser.END_DOCUMENT ) {
            if ( eventType == XmlPullParser.START_TAG ) {
                if ( parser.getName().equals( "project" ) ) {
                    foundRoot = true;
                } else if ( parser.getName().equals( "modelVersion" )  ) {
                    if ( parsed.contains( "modelVersion" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setModelVersion( parser.nextText() );
                } else if ( parser.getName().equals( "groupId" )  ) {
                    if ( parsed.contains( "groupId" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setGroupId( parser.nextText() );
                } else if ( parser.getName().equals( "artifactId" )  ) {
                    if ( parsed.contains( "artifactId" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setArtifactId( parser.nextText() );
                } else if ( parser.getName().equals( "version" )  ) {
                    if ( parsed.contains( "version" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setVersion( parser.nextText() );
                } else if ( parser.getName().equals( "packaging" )  ) {
                    if ( parsed.contains( "packaging" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setPackaging( parser.nextText() );
                } else if ( parser.getName().equals( "name" )  ) {
                    if ( parsed.contains( "name" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setName( parser.nextText() );
                } else if ( parser.getName().equals( "description" )  ) {
                    if ( parsed.contains( "description" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setDescription( parser.nextText() );
                } else if ( parser.getName().equals( "build" )  ) {
                    if ( parsed.contains( "build" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    model.setBuild( parseBuild( parser ) );
                } else if ( parser.getName().equals( "modules" )  ) {
                    if ( parsed.contains( "modules" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    java.util.List<String> modules = new java.util.ArrayList<String>();
                    model.setModules( modules );
                    while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                        if ( parser.getName().equals( "module" ) ) {
                            modules.add( parser.nextText() );
                        } else {
                            throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( parser.getName().equals( "dependencies" )  ) {
                    if ( parsed.contains( "dependencies" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    java.util.List<Dependency> dependencies = new java.util.ArrayList<Dependency>();
                    model.setDependencies( dependencies );
                    while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                        if ( parser.getName().equals( "dependency" ) ) {
                            dependencies.add( parseDependency( parser ) );
                        } else {
                            throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( parser.getName().equals( "repositories" )  ) {
                    if ( parsed.contains( "repositories" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
                    java.util.List<Repository> repositories = new java.util.ArrayList<Repository>();
                    model.setRepositories( repositories );
                    while ( parser.nextTag() == XmlPullParser.START_TAG ) {
                        if ( parser.getName().equals( "repository" ) ) {
                            repositories.add( parseRepository( parser ) );
                        } else {
                            throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( parser.getName().equals( "properties" )  ) {
                    if ( parsed.contains( "properties" ) )
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    parsed.add( parser.getName() );
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
        return model;
    }

}
