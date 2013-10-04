package foldlabs;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static foldlabs.LeinMatchers.aMapWith;
import static foldlabs.LeinMatchers.aPathMatching;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.verify;

public class LeinMojoTest {

    private Sys sys;

    @Before
    public void setup() throws IOException {
        sys = MockSys.defaultSys();
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

        verify(sys).run(any(Path.class), eq("foo"), anyMapOf(String.class,String.class));
        verify(sys).run(any(Path.class), eq("bar"), anyMapOf(String.class,String.class));
    }

    @Test
    public void defaultsToCompileTargetWhenNotSetExplicitly() throws Exception {
        LeinMojo mojo = new LeinMojo(sys);

        mojo.execute();

        verify(sys).run(any(Path.class), eq("compile"), anyMapOf(String.class,String.class));
    }


    @Test
    public void canSetEnvironmentVariablesFromConfiguredProperties() throws Exception {
        LeinMojo mojo = new LeinMojo(sys);

        Map<String,String> properties = new HashMap<>();
        properties.put("foo", "bar");
        properties.put("baz", "qix");
        
        mojo.setEnvironment(properties);
        mojo.execute();

        verify(sys).run(any(Path.class), eq("compile"), argThat(aMapWith("foo",is("bar"))));
        verify(sys).run(any(Path.class), eq("compile"), argThat(aMapWith("baz", is("qix"))));
    }

}
