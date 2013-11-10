package foldlabs;

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps clojure's leiningen build tool in maven.
 * <p>
 * This simple mojo wraps leiningen execution with maven. It is extremely dumb, simple and dirty:
 * </p>
 * <ul>
 * <li>download leiningen uber-jar,</li>
 * <li>download platform-dependent script,</li>
 * <li>run <tt>lein &lt;args&gt;</tt> repeatedly for each given <tt>do</tt> element.</li>
 * </ul>
 */
@Mojo(name = "lein", defaultPhase = LifecyclePhase.COMPILE, threadSafe = false)
public class LeinMojo extends AbstractMojo {

    /**
     * Sets leiningen's tasks to execute. 
     * <p>
     *     Each task will be executed in sequence. Default task is {@code compile}. For example:
     * </p>
     * <pre>
     *    &lt;targets&gt;
            &lt;target&gt;deps&lt;/target&gt;
            &lt;target&gt;run&lt;/target&gt;
          &lt;/targets&gt;
     * </pre>
     * <p>
     *     This instructs plugin to run <tt>deps</tt> task followed by <tt>run</tt> task. 
     * </p>
     * <p>
     *     <b>CAVEAT:</b> Note each task is run in a sub-process of the current maven process. If the current process is
     *     killed while sub-processes are still alive, this will create orphan processes that must be killed manually.
     * </p>
     */
    @Parameter(property = "lein.targets")
    private String[] targets = new String[]{"compile"};

    /**
     * Defines which leiningen's version to use.
     */
    @Parameter(property = "lein.version", defaultValue = "2.3.3", required = true)
    private String leinVersion = "2.3.3";

    /**
     * Sets environment variables to pass to underlying processes.
     * <p>
     *     Each leiningen execution will be run in an environment with all this configured variables set. Of course 
     *     the variables content may use maven properties.
     * </p>
     */
    @Parameter
    private Map<String,String> environment = new HashMap<>();
    
    private Sys sys;

    /**
     * Makes maven happy.
     */
    public LeinMojo(){}
    
    @VisibleForTesting
    LeinMojo(Sys sys) {
        this.sys = sys;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (sys == null) {
            sys = new DefaultSys(new Log() {
                @Override
                public void log(String message) {
                    getLog().info(message);
                }
            });
            getLog().info("using default system utilities provider");
        }
        
        lein lein = new lein(sys, leinVersion);

        lein.init();
        lein.useEnvironment(environment);
        
        for (String target : targets) {
            lein.run(target);
        }

    }

    public void setLeinVersion(String leinVersion) {
        this.leinVersion = leinVersion;
    }

    public void setTargets(String[] targets) {
        this.targets = targets;
    }

    public void setEnvironment(Map<String,String> environment) {
        this.environment = environment;
    }
    
    public void setSys(Sys sys){
        this.sys = sys;
    }
}
