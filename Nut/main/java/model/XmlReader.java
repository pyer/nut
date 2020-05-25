package nut.model;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import nut.model.Dependency;
import nut.model.Layout;
import nut.model.Project;
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
     * Method parseLayout
     *
     * @param parser
     * @throws IOException
     * @throws XmlParserException
     * @return Layout
     */
    private Layout parseLayout( XmlParser parser )
        throws IOException, XmlParserException
    {
        Layout layout = new Layout();
        Set<String> parsed = new HashSet<String>();
        while ( parser.nextTag() ) {
            if ( tagEquals( parser.getName(), "sourceDirectory", parsed ) ) {
                layout.setSourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "resourceDirectory", parsed ) ) {
                layout.setResourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "webappDirectory", parsed ) ) {
                layout.setWebappDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testSourceDirectory", parsed ) ) {
                layout.setTestSourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testResourceDirectory", parsed ) ) {
                layout.setTestResourceDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "targetDirectory", parsed ) ) {
                layout.setTargetDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "outputDirectory", parsed ) ) {
                layout.setOutputDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testOutputDirectory", parsed ) ) {
                layout.setTestOutputDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testReportDirectory", parsed ) ) {
                layout.setTestReportDirectory( parser.getText() );
            } else if ( tagEquals( parser.getName(), "testSuite", parsed ) ) {
                layout.setTestSuite( parser.getText() );
            } else {
                throw new XmlParserException( "Unrecognized layout tag: '" + parser.getName() + "'", parser, null );
            }
            parser.nextTag(); // end of tag
        }
        return layout;
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
     * Method parseProject.
     *
     * @param sReader
     * @throws IOException
     * @throws XmlParserException
     * @return Project
     */
    public Project parseProject( StringReader sReader )
        throws IOException, XmlParserException
    {
        String tag;
        Project project = new Project();
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
                    project.setArtifactId( parser.getText() );
                } else if ( tagEquals( tag, "groupId", parsed ) ) {
                    project.setGroupId( parser.getText() );
                } else if ( tagEquals( tag, "version", parsed ) ) {
                    project.setVersion( parser.getText() );
                } else if ( tagEquals( tag, "parent", parsed ) ) {
                    project.setParent( parser.getText() );
                } else if ( tagEquals( tag, "packaging", parsed ) ) {
                    project.setPackaging( parser.getText() );
                } else if ( tagEquals( tag, "description", parsed ) ) {
                    project.setDescription( parser.getText() );
                } else if ( tagEquals( tag, "build", parsed ) ) {
                    project.setBuild( parser.getText() );
                } else if ( tagEquals( tag, "layout", parsed ) ) {
                    project.setLayout( parseLayout( parser ) );
                } else if ( tagEquals( tag, "modules", parsed ) ) {
                    List<String> modules = new ArrayList<String>();
                    project.setModules( modules );
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
                    project.setDependencies( dependencies );
                    while ( parser.nextTag() ) {
                        if ( parser.getName().equals( "dependency" ) ) {
                            dependencies.add( parseDependency( parser ) );
                        } else {
                            throw new XmlParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                        }
                    }
                } else if ( tagEquals( tag, "repositories", parsed ) ) {
                    List<Repository> repositories = new ArrayList<Repository>();
                    project.setRepositories( repositories );
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
                       project.addProperty( key, value );
                       parser.nextTag(); // end of tag
                   }
               } else {
                   throw new XmlParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
               }
            }
        }
        return project;
    }
}
