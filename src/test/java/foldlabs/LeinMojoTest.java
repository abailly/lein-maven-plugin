package foldlabs;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;

import static foldlabs.LeinMatchers.aPathMatching;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LeinMojoTest {

    private Sys sys;

    @Before
    public void setup() throws IOException {
        sys = mock(Sys.class);
        when(sys.download(any(Path.class), any(String.class))).thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Path) invocationOnMock.getArguments()[0];
            }
        });
    }

    @Test
    public void passVersionParameterToLeinForDownloadingJar() throws Exception {
        LeinMojo mojo = new LeinMojo(sys);
        
        mojo.setLeinVersion("1.2");
        mojo.execute();
        
        verify(sys).download(argThat(aPathMatching(".*1.2.*")),anyString());
    }
}
