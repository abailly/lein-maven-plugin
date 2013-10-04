package foldlabs;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.nio.file.Path;

import static foldlabs.LeinMatchers.aMapWith;
import static foldlabs.LeinMatchers.aStringMatching;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

public class LeinMojoPluginTest extends AbstractMojoTestCase {

    private Sys sys;

    public void setUp() throws Exception {
        super.setUp();
        sys = MockSys.defaultSys();
    }

    public void testConfigureEnvironmentFromPom() throws Exception {
        File pom = pathOf("poms/pom-with-environment.xml");

        LeinMojo mojo = (LeinMojo) lookupMojo("lein", pom);
        assertNotNull(mojo);

        mojo.setSys(sys);
        mojo.execute();

        verify(sys).download(any(Path.class),argThat(aStringMatching(".*1.2.3.*")));
        verify(sys).run(any(Path.class), eq("go"), argThat(aMapWith("foo",is("bar"))));
        verify(sys).run(any(Path.class), eq("farther"), argThat(aMapWith("foo",is("bar"))));
    }

    private File pathOf(String relativePathToFile) {
        return new File(getClass().getResource("/" + relativePathToFile).getFile());
    }

}
