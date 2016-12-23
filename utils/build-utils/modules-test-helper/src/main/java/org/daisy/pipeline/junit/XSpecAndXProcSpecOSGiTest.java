package org.daisy.pipeline.junit;

import java.io.File;

import javax.inject.Inject;

import org.daisy.maven.xproc.xprocspec.XProcSpecRunner;
import org.daisy.maven.xspec.XSpecRunner;

import org.daisy.pipeline.pax.exam.Options.MavenBundle;

import static org.daisy.pipeline.pax.exam.Options.calabashConfigFile;
import static org.daisy.pipeline.pax.exam.Options.domTraversalPackage;
import static org.daisy.pipeline.pax.exam.Options.felixDeclarativeServices;
import static org.daisy.pipeline.pax.exam.Options.logbackClassic;
import static org.daisy.pipeline.pax.exam.Options.logbackConfigFile;
import static org.daisy.pipeline.pax.exam.Options.mavenBundle;
import static org.daisy.pipeline.pax.exam.Options.mavenBundles;
import static org.daisy.pipeline.pax.exam.Options.mavenBundlesWithDependencies;
import static org.daisy.pipeline.pax.exam.Options.thisBundle;
import static org.daisy.pipeline.pax.exam.Options.xprocspec;
import static org.daisy.pipeline.pax.exam.Options.xspec;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.util.PathUtils;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public abstract class XSpecAndXProcSpecOSGiTest extends XSpecAndXProcSpecTest {
	
	protected abstract String[] testDependencies();
	
	@Override
	protected File baseDir() {
		return new File(PathUtils.getBaseDir());
	}
	
	@Inject
	private XProcSpecRunner xprocspecRunner;
	
	@Override
	protected XProcSpecRunner xprocspecRunner() {
		return xprocspecRunner;
	}
	
	@Inject
	private XSpecRunner xspecRunner;
	
	@Override
	protected XSpecRunner xspecRunner() {
		return xspecRunner;
	}
	
	@Configuration
	public Option[] config() {
		String[] depStrings = testDependencies();
		MavenBundle[] deps = new MavenBundle[depStrings.length];
		for (int i = 0; i < depStrings.length; i++)
			deps[i] = mavenBundle(depStrings[i]);
		return util.withTestDependencies(deps);
	}
	
	protected String pipelineModule(String module) {
		return "org.daisy.pipeline.modules:" + module + ":?";
	}
	
	public static abstract class util {
		
		// wrapped in class to avoid ClassNotFoundException
		public static Option[] withTestDependencies(MavenBundle... deps) {
			File calabashConfigFile = new File(PathUtils.getBaseDir() + "/src/test/resources/config-calabash.xml");
			File logbackConfigFile = new File(PathUtils.getBaseDir() + "/src/test/resources/logback.xml");
			return options(
				calabashConfigFile.exists() ? calabashConfigFile() : null,
				logbackConfigFile.exists() ? logbackConfigFile() : null,
				domTraversalPackage(),
				felixDeclarativeServices(),
				thisBundle(),
				junitBundles(),
				mavenBundle("org.daisy.pipeline.build:modules-test-helper:?"),
				mavenBundlesWithDependencies(
					mavenBundles(deps),
					// logging
					logbackClassic(),
					// xprocspec
					xprocspec(),
					mavenBundle("org.daisy.maven:xproc-engine-daisy-pipeline:?"),
					// xspec
					xspec(),
					mavenBundle("org.daisy.pipeline:saxon-adapter:?"))
			);
		}
	}
}
