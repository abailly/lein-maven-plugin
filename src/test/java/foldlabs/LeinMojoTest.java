package foldlabs;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;

import static foldlabs.LeinMatchers.aPathMatching;
import static org.mockito.Matchers.*;
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

    @Test
    public void requestExecutionOfLeinWithGivenTargets() throws Exception {
        LeinMojo mojo = new LeinMojo(sys);

        mojo.setTargets(new String[]{"foo","bar"});
        mojo.execute();

        verify(sys).run(any(Path.class), eq("foo"),anyString(),anyMap());
        verify(sys).run(any(Path.class), eq("bar"),anyString(),anyMap());
    }


}
