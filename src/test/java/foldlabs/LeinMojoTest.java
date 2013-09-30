package foldlabs;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static foldlabs.LeinMatchers.aMapWith;
import static foldlabs.LeinMatchers.aPathMatching;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.*;

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

        verify(sys).download(argThat(aPathMatching(".*1.2.*")), anyString());
    }

    @Test
    public void requestExecutionOfLeinWithGivenTargets() throws Exception {
        LeinMojo mojo = new LeinMojo(sys);

        mojo.setTargets(new String[]{"foo", "bar"});
        mojo.execute();

        verify(sys).run(any(Path.class), eq("foo"),anyString(),anyMapOf(String.class,String.class));
        verify(sys).run(any(Path.class), eq("bar"), anyString(), anyMapOf(String.class,String.class));
    }

    @Test
    public void defaultsToCompileTargetWhenNotSetExplicitly() throws Exception {
        LeinMojo mojo = new LeinMojo(sys);

        mojo.execute();

        verify(sys).run(any(Path.class), eq("compile"),anyString(),anyMapOf(String.class,String.class));
    }


    @Test
    public void canSetEnvironmentVariablesFromConfiguredProperties() throws Exception {
        LeinMojo mojo = new LeinMojo(sys);

        Map<String,String> properties = new HashMap<String, String>();
        properties.put("foo", "bar");
        properties.put("baz", "qix");
        
        mojo.setEnvironment(properties);
        mojo.execute();

        verify(sys).run(any(Path.class), eq("compile"),anyString(),argThat(aMapWith("foo",is("bar"))));
        verify(sys).run(any(Path.class), eq("compile"),anyString(),argThat(aMapWith("baz", is("qix"))));
    }

}
