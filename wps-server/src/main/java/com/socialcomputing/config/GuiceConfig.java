package com.socialcomputing.config;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.socialcomputing.wps.server.webservices.EngineRESTService;
import com.socialcomputing.wps.server.webservices.PlanRESTService;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;


/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 * 
 *         WPS Guice configuration
 * 
 */
public class GuiceConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {

            @Override
            protected void configureServlets() {
                bind(PlanRESTService.class);
                bind(EngineRESTService.class);
                // bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
                Map<String, String> params = new HashMap<String, String>();
                params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
                serve("/services/*").with(GuiceContainer.class, params);
            }
        });
    }
}
