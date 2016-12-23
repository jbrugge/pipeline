package org.daisy.pipeline.junit;

import java.io.File;

import org.daisy.maven.xproc.xprocspec.XProcSpecRunner;
import org.daisy.maven.xspec.TestResults;
import org.daisy.maven.xspec.XSpecRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public abstract class XSpecAndXProcSpecTest {
	
	protected abstract File baseDir();
	
	protected XSpecRunner xspecRunner() {
		throw new UnsupportedOperationException();
	}
	
	protected XProcSpecRunner xprocspecRunner() {
		throw new UnsupportedOperationException();
	}
	
	@Test
	public void runXSpec() throws Exception {
		File testsDir = new File(baseDir(), "src/test/xspec");
		if (testsDir.exists()) {
			File reportsDir = new File(baseDir(), "target/surefire-reports");
			reportsDir.mkdirs();
			TestResults result = xspecRunner().run(testsDir, reportsDir);
			assertEquals("Number of failures and errors should be zero", 0L, result.getFailures() + result.getErrors());
		}
	}
	
	@Test
	public void runXProcSpec() throws Exception {
		File testsDir = new File(baseDir(), "src/test/xprocspec");
		if (testsDir.exists()) {
			boolean success = xprocspecRunner().run(testsDir,
			                                        new File(baseDir(), "target/xprocspec-reports"),
			                                        new File(baseDir(), "target/surefire-reports"),
			                                        new File(baseDir(), "target/xprocspec"),
			                                        new XProcSpecRunner.Reporter.DefaultReporter());
			assertTrue("XProcSpec tests should run with success", success);
		}
	}
}
