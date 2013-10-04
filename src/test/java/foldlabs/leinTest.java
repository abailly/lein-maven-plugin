package foldlabs;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static foldlabs.LeinMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class leinTest {

    public static final String LEIN_UBER_JAR_WITH_DEFAULT_VERSION_IN_SELF_INSTALLS = ".*self-installs.*" + lein.DEFAULT_VERSION + ".*jar";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Sys sys;
    private lein l;
    private Path buildDirectory;

    @Before
    public void setup() throws IOException, URISyntaxException {
        sys = MockSys.defaultSys();
        buildDirectory = Paths.get(getClass().getResource("/lein").toURI()).getParent();
        
        l = new lein(sys);
    }

    @Test
    public void downloadsUberjarForDefaultVersion() throws Exception {
        l.build();

        verify(sys).download(argThat(aPathMatching(LEIN_UBER_JAR_WITH_DEFAULT_VERSION_IN_SELF_INSTALLS)), anyString());
    }

    @Test
    public void createInstallDirectoryWhenDownloadingUberJar() throws Exception {
        l.build();

        verify(sys).makeDir(Paths.get(".", "self-installs"));
    }

    @Test
    public void downloadsUberjarForGivenVersion() throws Exception {
        l = new lein(sys, "1.0");
        l.build();

        verify(sys).download(argThat(aPathMatching(".*1.0.*jar")), anyString());
    }

    @Test
    public void downloadsLeinBatOnWindows() throws Exception {
        when(sys.isWindows()).thenReturn(true);

        l.build();

        verify(sys).download(argThat(aPathMatching(".*lein.bat")), anyString());
    }

    @Test
    public void downloadsPlainLeinScriptOnUnix() throws Exception {
        when(sys.isWindows()).thenReturn(false);

        l.build();

        verify(sys).download(argThat(aPathMatching(".*lein")), anyString());
    }

    @Test
    public void runScriptWithDepsTargetAndEnvironmentWithLEIN_JARSet() throws Exception {
        l.build();

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"),
                argThat(aMapWith("LEIN_JAR", aStringMatching(LEIN_UBER_JAR_WITH_DEFAULT_VERSION_IN_SELF_INSTALLS))));
    }

    @Test
    public void initPutsLEIN_JARInEnvironment() throws Exception {
        l.init();
        l.run("deps");

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"),
                argThat(aMapWith("LEIN_JAR", aStringMatching(LEIN_UBER_JAR_WITH_DEFAULT_VERSION_IN_SELF_INSTALLS))));
    }

    @Test
    public void initPutsCurrentDirectoryAsLEIN_HOMEInEnvironmentGivenBuildDirectoryIsNotSpecified() throws Exception {
        when(sys.currentDirectory()).thenReturn(Paths.get("foo"));
        l = new lein(sys);
        
        l.init();
        l.run("deps");

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"),
                argThat(aMapWith("LEIN_HOME", CoreMatchers.is("foo"))));
    }

    @Test
    public void useEnvironmentExtendsEnvironment() throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("PORT", "3000");
        
        l.init();
        l.useEnvironment(env);
        l.run("run");
        
        verify(sys).run(argThat(aPathMatching(".*lein")), eq("run"), argThat(aMapWith("PORT", is("3000"))));
    }

    @Test
    public void downloadsScriptAndUberJarToConfigureDirectory() throws Exception {
        l = new lein(buildDirectory,sys,"2.0");

        l.build();

        verify(sys,times(2)).download(argThat(aPathContaining(buildDirectory.toString())), anyString());
    }

    @Test
    public void setsWorkDirectoryInSysWhenSpecified() throws Exception {
        l = new lein(buildDirectory,sys,"2.0");

        verify(sys).cd(buildDirectory);
    }

    @Test
    public void initPutsConfiguredDirectoryAsLEIN_HOMEInEnvironmentGivenItIsDefined() throws Exception {
        l = new lein(buildDirectory,sys,"2.0");

        l.init();
        l.run("deps");

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"),
                argThat(aMapWith("LEIN_HOME", CoreMatchers.is(buildDirectory.toString()))));
    }

    @Test
    public void rethrowsIOExceptionWrappedInLeinException() throws Exception {
        expectedException.expect(lein.leinException.class);
        when(sys.download(any(Path.class), anyString())).thenThrow(new IOException("error"));

        l.build();
    }
}
