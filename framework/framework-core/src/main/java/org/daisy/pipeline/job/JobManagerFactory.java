package org.daisy.pipeline.job;

import org.daisy.common.properties.PropertyPublisher;
import org.daisy.common.properties.PropertyPublisherFactory;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.impl.DefaultJobManager;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    name = "job-manager-factory",
    service = { JobManagerFactory.class }
)
public class JobManagerFactory {
        private JobStorage storage;
        private JobExecutionService executionService;
        private RuntimeConfigurator runtimeConfigurator;
        
        public JobManager createFor(Client client){
                return new DefaultJobManager(this.storage.filterBy(client),
                                this.executionService.filterBy(client),
                                new JobContextFactory(this.runtimeConfigurator,client));
        }
        public JobManager createFor(Client client,JobBatchId batchId){
                return new DefaultJobManager(this.storage.filterBy(client).filterBy(batchId),
                                this.executionService.filterBy(client),
                                new JobContextFactory(this.runtimeConfigurator,client));
        }

        /**
         * @param storage the storage to set
         */
        @Reference(
            name = "job-storage",
            unbind = "-",
            service = JobStorage.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setJobStorage(JobStorage storage) {
                //TODO: check null
                this.storage = storage;
        }

        /**
         * @param executionService the executionService to set
         */
        @Reference(
            name = "execution-service",
            unbind = "-",
            service = JobExecutionService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setExecutionService(JobExecutionService executionService) {
                //TODO:check null
                this.executionService = executionService;
        }

        @Reference(
            name = "runtime-configurator",
            unbind = "-",
            service = RuntimeConfigurator.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setRuntimeConfigurator(RuntimeConfigurator configurator){
                //TODO: check null
                //              logger.debug("setting monitor factory");
                this.runtimeConfigurator=configurator;
        }

        //FIXME: probably move these two methods somewhere else, maybe a dummy class for the framework just tu publish this.
        public void setPropertyPublisherFactory(PropertyPublisherFactory propertyPublisherFactory){
                PropertyPublisher propertyPublisher=propertyPublisherFactory.newPropertyPublisher();    
                //the property publishing step goes here
                propertyPublisher.publish("org.daisy.pipeline.iobase" ,System.getProperty("org.daisy.pipeline.iobase","" ),this.getClass());
                propertyPublisher.publish("org.daisy.pipeline.home" ,System.getProperty("org.daisy.pipeline.home","" ),this.getClass());
                propertyPublisher.publish("org.daisy.pipeline.logdir",System.getProperty("org.daisy.pipeline.logdir","" ),this.getClass());
                propertyPublisher.publish("org.daisy.pipeline.procs",System.getProperty("org.daisy.pipeline.procs","" ),this.getClass());
        }

        public void unsetPropertyPublisherFactory(PropertyPublisherFactory propertyPublisherFactory){
                PropertyPublisher propertyPublisher=propertyPublisherFactory.newPropertyPublisher();    
                //the property unpublishing step goes here
                propertyPublisher.unpublish("org.daisy.pipeline.iobase" ,  this.getClass());
                propertyPublisher.unpublish("org.daisy.pipeline.home"   ,  this.getClass());
                propertyPublisher.unpublish("org.daisy.pipeline.logdir" ,  this.getClass());

        }
}
