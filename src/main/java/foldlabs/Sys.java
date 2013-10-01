package foldlabs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface Sys {
    Path download(Path path, String uri) throws IOException;

    void run(Path lein1, String target, String message, Map<String, String> env) throws IOException, InterruptedException;

    boolean isWindows();

    void makeExecutable(Path scriptPath) throws IOException;

    String currentDirectory();

    boolean makeDir(Path installDir);
}
