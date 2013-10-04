package foldlabs;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MockSys {

    private MockSys() {
    }

    public static Sys defaultSys() throws IOException {
        Sys sys = mock(Sys.class);
        when(sys.currentDirectory()).thenReturn(Paths.get("."));

        when(sys.download(any(Path.class), any(String.class))).thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Path) invocationOnMock.getArguments()[0];
            }
        });
        return sys;
    }
}
