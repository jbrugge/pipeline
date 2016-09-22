package org.daisy.pipeline.dtbooktoepub3;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.daisy.pipeline.script.XProcScriptService;
import org.osgi.service.component.annotations.Activate;

import com.google.common.collect.Maps;

@org.osgi.service.component.annotations.Component(
        name = "dtbook-to-epub3-script",
        immediate = true,
        service = { XProcScriptService.class }
)
public class DtbookToEpub3Script extends XProcScriptService{

        @Activate
        public void activate(){
                URI uri;
                try {
                        uri = this.getClass().getClassLoader().getResource("xml/dtbook-to-epub3.xpl").toURI();
                } catch (URISyntaxException e) {
                        throw new RuntimeException(e);

                }
                System.out.println("URI" + uri.toString());
                Map<String,String> props = Maps.newHashMap();
                //TODO: we should be able to generate this somehow
                // and have a better activation strategy than having a hashmap
                // my legacy... technical debts :D
                props.put(SCRIPT_ID,"dtbook-to-epub3");
                props.put(SCRIPT_DESCRIPTION,"Converts a dtbook into an epub3");
                props.put(SCRIPT_VERSION,"0.0.0");
                props.put(SCRIPT_URL,uri.toString());
                super.activate(props);

        }

}
