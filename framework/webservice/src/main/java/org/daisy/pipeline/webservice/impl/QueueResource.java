
package org.daisy.pipeline.webservice.impl;



import java.util.Collection;

import org.daisy.common.priority.Prioritizable;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.webserviceutils.xml.QueueXmlWriter;
import org.daisy.pipeline.webserviceutils.xml.XmlWriterFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;


public class QueueResource extends AuthenticatedResource {

	@Override
	public void doInit() {
		super.doInit();
		if (!isAuthenticated()) {
			return;
		}
	}

	/**
	 * List the jobs, their final priorities and their times
	 *
	 * @return the resource
	 */
	@Get("xml")
	public Representation getResource() {
		if (!isAuthenticated()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}

		setStatus(Status.SUCCESS_OK);
                Collection<? extends Prioritizable<Job>> jobs=webservice().getJobManager(this.getClient()).getExecutionQueue().asCollection();
		QueueXmlWriter writer = XmlWriterFactory.createXmlWriterForQueue(jobs);
                DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML,
                                writer.getXmlDocument());
		return dom;
	}

}
