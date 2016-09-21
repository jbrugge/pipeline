package org.daisy.common.stax.woodstox.osgi.impl;

import java.util.Properties;

import javax.xml.stream.XMLInputFactory;

import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.stax.WstxInputFactory;

import org.osgi.service.component.annotations.Component;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating StaxInputFactoryService objects.
 */
@Component(
    name = "stax-input-factory",
    service = { XMLInputFactory.class }
    
    // try if it works without
    // (also, if enabled, test if it really works)
    
    // servicefactory = true
)
public class StaxInputFactoryService extends WstxInputFactory {
	
	// try without these properties
	// how to set them without OSGi?
	
	// Properties props = new Properties();
	// props.setProperty(Stax2InputFactoryProvider.OSGI_SVC_PROP_IMPL_NAME, ReaderConfig.getImplName());
	// props.setProperty(Stax2InputFactoryProvider.OSGI_SVC_PROP_IMPL_VERSION, ReaderConfig.getImplVersion());
}
