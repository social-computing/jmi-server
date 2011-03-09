package com.socialcomputing.config;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.MessageBodyWriter;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.socialcomputing.wps.server.webservices.maker.PlanService;
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
                bind(PlanService.class);
                bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
                Map<String, String> params = new HashMap<String, String>();
                params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
                serve("/services/*").with(GuiceContainer.class, params);
            }
        });
    }
}
