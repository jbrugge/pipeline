package org.daisy.pipeline.dtbooktoepub3;

import java.net.URI;
import java.util.LinkedList;

import org.daisy.pipeline.modules.Component;
import org.daisy.pipeline.modules.Entity;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@org.osgi.service.component.annotations.Component(
        name = "dtbook-to-epub3",
        immediate = true,
        service = { Module.class }
)
public class DtbookToEpub3Module extends Module{


        static{
                Component module = new Component(URI.create(""), "path",null);
        }

        public DtbookToEpub3Module(){
                super("dtbook-to-epub3","1.0.0", "Dtbook to epub3", new LinkedList<Component>(), new LinkedList<Entity>());
                System.out.println("Initialising dtbook to epub3");

        }
        ModuleRegistry registry; 
        @Reference(
                name = "ModuleRegistry",
                unbind = "-",
                service = ModuleRegistry.class,
                cardinality = ReferenceCardinality.MANDATORY,
                policy = ReferencePolicy.STATIC
        )
        public void setRegistry(ModuleRegistry registry) {
                System.out.println("Setting registry ++++++++++++!!!");
                this.registry = registry;
        }

        @Activate
        public void init() {
                System.out.println("Registering module ++++++++++++!!!");
                registry.addModule(this);
        }
}
