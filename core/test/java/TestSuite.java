import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   nut.artifact.ArtifactTest.class,
   nut.model.BuildTest.class,
   nut.model.DependencyTest.class,
   nut.model.EffectiveModelTest.class,
   nut.model.ModelTest.class,
   nut.model.PluginContainerTest.class,
   nut.model.PluginTest.class,
   nut.project.ProjectBuilderTest.class })

public class TestSuite {
}
