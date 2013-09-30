package foldlabs;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Wraps clojure's leiningen build tool in maven. 
 * <p>
 *     This simple mojo wraps leiningen execution with maven. It is extremely dumb, simple and dirty:
 * </p>
 * <ul>
 *     <li>download leiningen uber-jar,</li>
 *     <li>download platform-dependent script,</li>
 *     <li>run <tt>lein &lt;args&gt;</tt> repeatedly for each given <tt>do</tt> element.</li>
 * </ul>
 * 
 */
@Mojo(name = "lein", defaultPhase = LifecyclePhase.COMPILE, threadSafe = false)
public class LeinMojo extends AbstractMojo {

    @Parameter(property = "lein.dos")
    private String[] dos;

    @Parameter(property = "lein.version", defaultValue = "2.3.2", required = true)
    private String leinVersion;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        lein lein = new lein(new Sys(new Log() {
            @Override
            public void log(String message) {
                getLog().info(message);
            }
        }), leinVersion);

        lein.build();
    }
}
