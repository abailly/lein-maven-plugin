package foldlabs;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static foldlabs.LeinMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class leinTest {

    public static final String LEIN_UBER_JAR_WITH_DEFAULT_VERSION_IN_SELF_INSTALLS = ".*self-installs.*" + lein.DEFAULT_VERSION + ".*jar";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Sys sys;
    private lein l;

    @Before
    public void setup() throws IOException {
        sys = mock(Sys.class);
        when(sys.download(any(Path.class), any(String.class))).thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Path) invocationOnMock.getArguments()[0];
            }
        });
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

        verify(sys).makeDir(Paths.get(".","self-installs"));
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

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"), anyString(),
                argThat(aMapWith("LEIN_JAR", aStringMatching(LEIN_UBER_JAR_WITH_DEFAULT_VERSION_IN_SELF_INSTALLS))));
    }

    @Test
    public void initPutsLEIN_JARInEnvironment() throws Exception {
        l.init();
        l.run("deps");

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"), anyString(),
                argThat(aMapWith("LEIN_JAR", aStringMatching(LEIN_UBER_JAR_WITH_DEFAULT_VERSION_IN_SELF_INSTALLS))));
    }

    @Test
    public void initPutsLEIN_HOMEInEnvironment() throws Exception {
        when(sys.currentDirectory()).thenReturn("foo");
        
        l.init();
        l.run("deps");

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"), anyString(),
                argThat(aMapWith("LEIN_HOME", CoreMatchers.is("foo"))));
    }

    @Test
    public void useEnvironmentExtendsEnvironment() throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("PORT", "3000");
        
        l.init();
        l.useEnvironment(env);
        l.run("run");
        
        verify(sys).run(argThat(aPathMatching(".*lein")), eq("run"), anyString(), argThat(aMapWith("PORT", is("3000"))));
    }

    @Test
    public void rethrowsIOExceptionWrappedInLeinException() throws Exception {
        expectedException.expect(lein.leinException.class);
        when(sys.download(any(Path.class), anyString())).thenThrow(new IOException("error"));

        l.build();
    }
}
