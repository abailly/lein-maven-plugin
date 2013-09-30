package foldlabs;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class leinTest {

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

        verify(sys).download(argThat(aPathMatching(".*" + lein.DEFAULT_VERSION + ".*jar")), anyString());
    }

    @Test
    public void downloadsLeinBatOnWindows() throws Exception {
        when(sys.isWindows()).thenReturn(true);

        l.build();

        verify(sys).download(argThat(aPathMatching(".*lein.bat")), anyString());
    }

    @Test
    public void downloadsPlainLeinScriptOnUnices() throws Exception {
        when(sys.isWindows()).thenReturn(false);

        l.build();

        verify(sys).download(argThat(aPathMatching(".*lein")), anyString());
    }

    @Test
    public void runScriptWithDepsTargetAndEnvironmentWithLEIN_JARSet() throws Exception {
        l.build();

        verify(sys).run(argThat(aPathMatching(".*lein")), eq("deps"), anyString(), argThat(aMapWith("LEIN_JAR", aStringMatching(".*" + lein.DEFAULT_VERSION + ".*jar"))));
    }

    private Matcher<String> aStringMatching(final String regex) {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String s) {
                return s.matches(regex);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a string matching " + regex);
            }
        };
    }

    private Matcher<Map<String, String>> aMapWith(final String key, final Matcher<String> valueMatcher) {
        return new TypeSafeMatcher<Map<String, String>>() {
            @Override
            protected boolean matchesSafely(Map<String, String> stringStringMap) {
                for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                    if(entry.getKey().equals(key) && valueMatcher.matches(entry.getValue()))
                        return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a map containing pair (" + key + ", " + valueMatcher);
            }
        };
    }

    private Matcher<Path> aPathMatching(final String regex) {
        return new TypeSafeMatcher<Path>() {
            @Override
            protected boolean matchesSafely(Path path) {
                return path.toString().matches(regex);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a path matching " + regex);
            }
        };
    }
}
