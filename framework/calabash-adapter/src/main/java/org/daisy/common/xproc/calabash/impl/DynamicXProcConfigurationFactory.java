package org.daisy.common.xproc.calabash.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.xproc.calabash.XProcConfigurationFactory;
import org.daisy.common.xproc.calabash.XProcStepProvider;
import org.daisy.common.xproc.calabash.XProcStepRegistry;
import org.daisy.pipeline.xpath.XPathFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.runtime.XAtomicStep;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Mainly thought to be used throught OSGI this class creates configuration
 * objects used with the calabash engine wrapper.
 */
@Component(
	name = "calabash-config-factory",
	service = {
		XProcConfigurationFactory.class,
		XProcStepRegistry.class
	}
)
public class DynamicXProcConfigurationFactory implements
		XProcConfigurationFactory, XProcStepRegistry {

	/** The Constant CONFIG_PATH. */
	public static final String CONFIG_PATH = "org.daisy.pipeline.xproc.configuration";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(DynamicXProcConfigurationFactory.class);

	/** The step providers. */
	private final Map<QName, XProcStepProvider> stepProviders = new HashMap<QName, XProcStepProvider>();

	// private FunctionLibraryList mFunctionLibrary=new FunctionLibraryList();
	private XPathFunctionRegistry mXPathRegistry = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.daisy.common.xproc.calabash.XProcConfigurationFactory#newConfiguration
	 * ()
	 */
	@Override
	public XProcConfiguration newConfiguration() {
		XProcConfiguration config = new DynamicXProcConfiguration(this);
		loadConfigurationFile(config);
		registerExtensionFunctions(config);
		// config.getProcessor().getUnderlyingConfiguration().addExtensionBinders(mFunctionLibrary);

		return config;
	}

	/**
	 * Activate (OSGI)
	 */
	@Activate
	public void activate() {
		logger.trace("Activating XProc Configuration Factory");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.daisy.common.xproc.calabash.XProcConfigurationFactory#newConfiguration
	 * (boolean)
	 */
	@Override
	public XProcConfiguration newConfiguration(boolean schemaAware) {
		XProcConfiguration config = new DynamicXProcConfiguration(schemaAware,
				this);
		loadConfigurationFile(config);
		registerExtensionFunctions(config);
		// config.getProcessor().getUnderlyingConfiguration().addExtensionBinders(mFunctionLibrary);
		return config;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.daisy.common.xproc.calabash.XProcConfigurationFactory#newConfiguration
	 * (net.sf.saxon.s9api.Processor)
	 */
	@Override
	public XProcConfiguration newConfiguration(Processor processor) {
		XProcConfiguration config = new DynamicXProcConfiguration(processor,
				this);
		loadConfigurationFile(config);
		registerExtensionFunctions(config);
		// config.getProcessor().getUnderlyingConfiguration().addExtensionBinders(mFunctionLibrary);
		return config;
	}

	/**
	 * Adds the step.
	 *
	 * @param stepProvider
	 *            the step provider
	 * @param properties
	 *            the properties
	 */
	@Reference(
		name = "XProcStepProvider",
		unbind = "removeStep",
		service = XProcStepProvider.class,
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC
	)
	public void addStep(XProcStepProvider stepProvider, Map<?, ?> properties) {
		QName type = QName.fromClarkName((String) properties.get("type"));
		logger.debug("Adding step to registry: {}", type.toString());
		stepProviders.put(type, stepProvider);
	}

	/**
	 * Removes the step from the registry
	 *
	 * @param stepProvider
	 *            the step provider
	 * @param properties
	 *            the properties
	 */
	public void removeStep(XProcStepProvider stepProvider, Map<?, ?> properties) {
		QName type = QName.fromClarkName((String) properties.get("type"));
		logger.debug("Removing step from registry: {}", type.toString());
		stepProviders.remove(type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.daisy.common.xproc.calabash.XProcStepRegistry#hasStep(net.sf.saxon
	 * .s9api.QName)
	 */
	@Override
	public boolean hasStep(QName type) {
		return stepProviders.containsKey(type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.daisy.common.xproc.calabash.XProcStepRegistry#newStep(net.sf.saxon
	 * .s9api.QName, com.xmlcalabash.core.XProcRuntime,
	 * com.xmlcalabash.runtime.XAtomicStep)
	 */
	@Override
	public XProcStep newStep(QName type, XProcRuntime runtime, XAtomicStep step) {
		XProcStepProvider stepProvider = stepProviders.get(type);
		return (stepProvider != null) ? stepProvider.newStep(runtime, step)
				: null;
	}

	/**
	 * Loads the custom configuration file located in CONFIG_PATH
	 *
	 * @param conf
	 *            the conf
	 */
	private void loadConfigurationFile(XProcConfiguration conf) {
		// TODO cleanup and cache
		String configPath = System.getProperty(CONFIG_PATH);
		if (configPath != null) {
			if (!configPath.startsWith("file:")) {
				configPath = new File(configPath).toURI().toString();
			}
			logger.debug("Reading Calabash configuration from {}", configPath);
			// Make this absolute because sometimes it fails from the command
			// line otherwise. WTF?

			SAXSource source = new SAXSource(new InputSource(configPath));
			DocumentBuilder builder = conf.getProcessor().newDocumentBuilder();
			XdmNode doc;
			try {
				doc = builder.build(source);
			} catch (SaxonApiException e) {
				logger.error("Error loading configuration file", e);
				throw new RuntimeException("error loading configuration file",
						e);
			}
			conf.parse(doc);
		}
	}

	@Reference(
		name = "FunctionLibary",
		unbind = "unsetXPathFunctionRegistry",
		service = XPathFunctionRegistry.class,
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC
	)
	public void setXPathFunctionRegistry(XPathFunctionRegistry xpathFunctions) {
		logger.debug("Setting function registry");
		// mFunctionLibrary.addFunctionLibrary(xpathFunctions);
		mXPathRegistry = xpathFunctions;
	}

	public void unsetXPathFunctionRegistry(XPathFunctionRegistry xpathFunctions) {
		logger.debug("Unsetting function registry");
		if (mXPathRegistry == xpathFunctions)
			mXPathRegistry = null;
	}

	private void registerExtensionFunctions(XProcConfiguration config) {
		if (mXPathRegistry != null) {
			for (ExtensionFunctionDefinition func : mXPathRegistry
					.getFunctions()) {
				config.getProcessor().registerExtensionFunction(func);
			}
		}
	}
}
