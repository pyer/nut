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

import nut.xml.XmlParser;
import nut.xml.XmlParserException;

public class XmlReader {

    private boolean tagEquals( String value, String name, Set<String> set )
        throws XmlParserException
    {
      boolean ret = value.equals( name );
      if ( ret ) {
         if ( set.contains( name ) )
            throw new XmlParserException( "Duplicated tag: '" + name + "'" );
         set.add( name );
      }
      return ret;
    }

    /**
     * Method parseGoal.
     *
     * @param parser
     * @throws IOException
     * @throws XmlParserException
     * @return Goal
     */
    private Goal parseGoal( XmlParser parser )
        throws IOException, XmlParserException
    {
        Goal goal = new Goal();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() ) {
            if ( tagEquals( parser.getName(), "name", parsed ) ) {
                goal.setName( parser.getText() );
            } else if ( tagEquals( parser.getName(), "class", parsed ) ) {
                goal.setClassName( parser.getText() );
            } else if ( tagEquals( parser.getName(), "configuration", parsed ) ) {
                while ( parser.nextTag() ) {
                    String key = parser.getName();
                    String value = parser.getText().trim();
                    goal.setConfigurationValue( key, value );
                }
//                parser.nextTag(); // end of tag
            }
            parser.nextTag(); // end of tag
        }
        return goal;
    }

    /**
     * Method parseBuild.
     *
     * @param parser
     * @throws IOException
     * @throws XmlParserException
     * @return Build
     */
    private Build parseBuild( XmlParser parser )
        throws IOException, XmlParserException
    {
        Build build = new Build();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() ) {
            if ( tagEquals( parser.getName(), "sourceDirectory", parsed ) ) {
                build.setSourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "resourceDirectory", parsed ) ) {
                build.setResourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testSourceDirectory", parsed ) ) {
                build.setTestSourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testResourceDirectory", parsed ) ) {
                build.setTestResourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "targetDirectory", parsed ) ) {
                build.setTargetDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "outputDirectory", parsed ) ) {
                build.setOutputDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testOutputDirectory", parsed ) ) {
                build.setTestOutputDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testReportDirectory", parsed ) ) {
                build.setTestReportDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "goals", parsed ) ) {
                List<Goal> goals = new ArrayList<Goal>();
                build.setGoals( goals );
                while ( parser.nextTag() ) {
                    if ( parser.getName().equals( "goal" ) ) {
                        goals.add( parseGoal( parser ) );
                    } else {
                        throw new XmlParserException( "Unrecognized build association: '" + parser.getName() + "'", parser, null );
                    }
                }
            } else {
                throw new XmlParserException( "Unrecognized build tag: '" + parser.getName() + "'", parser, null );
            }
            parser.nextTag(); // end of tag
        }
        return build;
    }

    /**
     * Method parseDependency.
     *
     * @param parser
     * @throws IOException
     * @throws XmlParserException
     * @return Dependency
     */
    private Dependency parseDependency( XmlParser parser )
        throws IOException, XmlParserException
    {
        Dependency dependency = new Dependency();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() ) {
            if ( tagEquals( parser.getName(), "groupId", parsed ) ) {
                dependency.setGroupId( parser.getText() );
            } else if ( tagEquals( parser.getName(), "artifactId", parsed ) ) {
                dependency.setArtifactId( parser.getText() );
            } else if ( tagEquals( parser.getName(), "version", parsed ) ) {
                dependency.setVersion( parser.getText() );
            } else if ( tagEquals( parser.getName(), "type", parsed ) ) {
                dependency.setType( parser.getText() );
            } else if ( tagEquals( parser.getName(), "scope", parsed ) ) {
                dependency.setScope( parser.getText() );
            } else {
                throw new XmlParserException( "Unrecognized dependency tag: '" + parser.getName() + "'", parser, null );
            }
            parser.nextTag(); // end of tag
        }
        return dependency;
    }

    /**
     * Method parseRepository.
     *
     * @param parser
     * @throws IOException
     * @throws XmlParserException
     * @return Repository
     */
    private Repository parseRepository( XmlParser parser )
        throws IOException, XmlParserException
    {
        Repository repository = new Repository();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() ) {
            if ( tagEquals( parser.getName(), "name", parsed ) ) {
                repository.setName( parser.getText() );
            } else if ( tagEquals( parser.getName(), "layout", parsed ) ) {
                repository.setLayout( parser.getText() );
            } else if ( tagEquals( parser.getName(), "url", parsed ) ) {
                repository.setURL( parser.getText() );
            } else {
                throw new XmlParserException( "Unrecognized repository tag: '" + parser.getName() + "'", parser, null );
            }
            parser.nextTag(); // end of tag
        }
        return repository;
    }

    /**
     * Method parseModel.
     *
     * @param sReader
     * @throws IOException
     * @throws XmlParserException
     * @return Model
     */
    public Model parseModel( StringReader sReader )
        throws IOException, XmlParserException
    {
        String tag;
        Model model = new Model();
        Set<String> parsed = new HashSet<String>();
        XmlParser parser = new XmlParser( sReader );
        parser.skipBOM();
        parser.nextTag();
        if (!parser.getName().equals( "project" )) {
            throw new XmlParserException( "'project' tag not found", parser, null );
        }
        while ( !parser.endOfDocument() ) {
            parser.nextTag();
            tag = parser.getName();
            if(parser.endOfTag()) {
                continue;
            } else {
                if ( tagEquals( tag, "artifactId", parsed ) ) {
                    model.setArtifactId( parser.getText() );
                } else if ( tagEquals( tag, "groupId", parsed ) ) {
                    model.setGroupId( parser.getText() );
                } else if ( tagEquals( tag, "version", parsed ) ) {
                    model.setVersion( parser.getText() );
                } else if ( tagEquals( tag, "parent", parsed ) ) {
                    model.setParent( parser.getText() );
                } else if ( tagEquals( tag, "packaging", parsed ) ) {
                    model.setPackaging( parser.getText() );
                } else if ( tagEquals( tag, "description", parsed ) ) {
                    model.setDescription( parser.getText() );
                } else if ( tagEquals( tag, "build", parsed ) ) {
                    model.setBuild( parseBuild( parser ) );
                } else if ( tagEquals( tag, "modules", parsed ) ) {
                    List<String> modules = new ArrayList<String>();
                    model.setModules( modules );
                    while ( parser.nextTag() ) {
                        if ( parser.getName().equals( "module" ) ) {
                            modules.add( parser.getText() );
                            parser.nextTag(); // end of tag
                        } else {
                            throw new XmlParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( tagEquals( tag, "dependencies", parsed ) ) {
                    List<Dependency> dependencies = new ArrayList<Dependency>();
                    model.setDependencies( dependencies );
                    while ( parser.nextTag() ) {
                        if ( parser.getName().equals( "dependency" ) ) {
                            dependencies.add( parseDependency( parser ) );
                        } else {
                            throw new XmlParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( tagEquals( tag, "repositories", parsed ) ) {
                    List<Repository> repositories = new ArrayList<Repository>();
                    model.setRepositories( repositories );
                    while ( parser.nextTag() ) {
                         if ( parser.getName().equals( "repository" ) ) {
                             repositories.add( parseRepository( parser ) );
                         } else {
                             throw new XmlParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                         }
                    }
                } else if ( tagEquals( tag, "properties", parsed ) ) {
                  while ( parser.nextTag() ) {
                       String key = parser.getName();
                       String value = parser.getText().trim();
                       model.addProperty( key, value );
                       parser.nextTag(); // end of tag
                   }
               } else {
                   throw new XmlParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
               }
            }
        }
        return model;
    }
}
