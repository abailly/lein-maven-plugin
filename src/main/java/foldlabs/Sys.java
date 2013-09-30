package foldlabs;

import com.google.common.collect.ImmutableSet;

import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

public class Sys {
    private final Log log;

    public Sys(Log log) {
        this.log = log;
    }

    static Thread pump(final InputStream in) {
        return new Thread() {
            @Override
            public void run() {
                try {
                    byte[] buffer = new byte[1024];
                    int ln;
                    while ((ln = in.read(buffer)) > -1) {
                        System.out.write(buffer, 0, ln);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    Path download(Path path, String uri) throws IOException {
       log.log("downloading " + uri + " to " + path);
        if (!path.toFile().exists()) {
            InputStream lein = ClientBuilder.newClient().target(uri).request().get(InputStream.class);
            Files.copy(lein, path, StandardCopyOption.REPLACE_EXISTING);
            log.log("done");
        } else {
            log.log(path + " exists, skipping download");
        }
        return path;

    }

    void run(Path lein1, String target, String message, Map<String, String> env) throws IOException, InterruptedException {
        log.log(message);
        ProcessBuilder processBuilder = new ProcessBuilder(lein1.toAbsolutePath().toString(), target).redirectErrorStream(true);
        Map<String, String> environment = processBuilder.environment();
        environment.putAll(env);
        Process install = processBuilder.start();

        Thread pump = pump(install.getInputStream());
        pump.start();
        install.waitFor();
        pump.join();
        log.log("done");
    }

    boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    void makeExecutable(Path scriptPath) throws IOException {
        if (!isWindows())
            Files.setPosixFilePermissions(scriptPath, ImmutableSet.of(OWNER_EXECUTE, OWNER_READ, OWNER_WRITE));
    }
}
