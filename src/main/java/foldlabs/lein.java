package foldlabs;


import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Paths.get;

/**
 * Simple wrapper over leiningen script files.
 */
public class lein {

    public static final String DEFAULT_VERSION = "2.3.3";

    private final Path buildDirectory;
    private final String version;
    private final Sys sys;
    
    private Path leinScript;
    private Path leinJar;
    private Map<String, String> environment = new HashMap<>();

    public lein() {
        this(new DefaultSys(new Log.SystemLog()));
    }

    public lein(Sys sys) {
        this(sys, DEFAULT_VERSION);
    }

    public lein(Sys sys, String leinVersion) {
        this(sys.currentDirectory(),sys,leinVersion);
    }

    public lein(Path buildDirectory, Sys sys, String leinVersion) {
        this.buildDirectory = buildDirectory;
        this.version = leinVersion;
        this.sys = sys;
        
        sys.cd(buildDirectory);
    }

    public static void main(String[] args) throws IOException {
        new lein().build();
    }

    public void build() {
        try {
            init();

            run("deps");
            run("run");
        } catch (Exception e) {
            throw new leinException(e);
        }
    }

    public void useEnvironment(Map<String, String> environment) {
        this.environment.putAll(environment);
    }

    Path getUberJar() throws IOException {
        Path installDir = buildDirectory.resolve(get("self-installs"));
        sys.makeDir(installDir);
        return sys.download(installDir.resolve(get("leiningen-" + version + "-standalone.jar")), "https://leiningen.s3.amazonaws.com/downloads/leiningen-" + version + "-standalone.jar");
    }

    Path getScript() throws IOException {
        Path scriptPath = buildDirectory.resolve(get("lein" + (sys.isWindows() ? ".bat" : "")));

        scriptPath = sys.download(scriptPath, "https://raw.github.com/technomancy/leiningen/stable/bin/lein" + (sys.isWindows() ? ".bat" : ""));

        sys.makeExecutable(scriptPath);

        return scriptPath;
    }

    public void init() {
        try {
            leinScript = getScript();
            leinJar = getUberJar();

            Map<String, String> environment = new HashMap<>();
            environment.put("LEIN_JAR", leinJar.toAbsolutePath().toString());
            environment.put("LEIN_HOME", buildDirectory.toString());
            useEnvironment(environment);
            
        } catch (IOException e) {
            throw new leinException(e);
        }
    }

    public void run(String target) {
        try {
            sys.run(leinScript, target, environment);
        } catch (Exception e) {
            throw new leinException(e);
        }
    }

    class leinException extends RuntimeException {
        public leinException(Throwable throwable) {
            super(throwable);
        }
    }
}
