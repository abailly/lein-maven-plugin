package foldlabs;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class leinTest {

    @Test
    public void downloadsUberjarForDefaultVersion() throws Exception {
        Sys sys = mock(Sys.class);
        when(sys.download(any(Path.class),any(String.class))).thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Path) invocationOnMock.getArguments()[0];
            }
        });
        lein l = new lein(new Log.SystemLog(), sys);

        l.build();
        
        verify(sys).download(argThat(aPathMatching(".*"+lein.DEFAULT_VERSION+".*jar")),anyString());
    }

    private Matcher<Path> aPathMatching(final String regex) {
        return new TypeSafeMatcher<Path>() {
            @Override
            protected boolean matchesSafely(Path path) {
                return path.toString().matches(regex);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a path matching "+regex);
            }
        };
    }
}
