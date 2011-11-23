package com.socialcomputing.wps.server.web;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.MDC;

/**
 * 
 * <p>
 * An implementation of {@link Filter Servlet Filter} to reset the
 * content of slf4j MDC before processing incoming requests.
 * </p>
 * <p>
 * This class shall be deployed, when using a servlet container
 * compatible with version 2.3+ of the Servlet specification,
 * through the web application configuration file (web.xml) as
 * follows:
 * </p>
 * <blockquote><pre>
 * &lt;filter&gt;
 *   &lt;filter-name&gt;LogContextResetFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;
 *     com.socialcomputing.wps.server.wps.LogContextResetFilter
 *   &lt;/filter-class&gt;
 * &lt;/filter&gt;
 * ...
 * &lt;filter-mapping&gt;
 *   &lt;filter-name&gt;LogContextResetFilter&lt;/filter-name&gt;
 *   &lt;servlet-name&gt;TheServlet&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt;
 * </pre></blockquote>
 *
 */
public final class LogContextResetFilter implements Filter {
    
    //--------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public LogContextResetFilter() {
        super();
    }

    
    //--------------------------------------------------------------------
    // Filter contract support
    //--------------------------------------------------------------------

    /**
     * Called by the web container to indicate to a filter that it
     * is being placed into service. The servlet container calls the
     * init method exactly once after instantiating the filter. The
     * init method must complete successfully before the filter is
     * asked to do any filtering work.
     *
     * @param  filterConfig   filter configuration.
     *
     * @throws ServletException if any error occurs during filter
     *                          initialization.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize!
    }

    
    /**
     * The <code>doFilter</code> method of the filter is called by
     * the container each time a request/response pair is passed
     * through the chain due to a client request for a resource at
     * the end of the chain.
     *
     * @param  request    the <code>ServletRequest</code> object
     *                    that contains the client's request.
     * @param  response   the <code>ServletResponse</code> object
     *                    that contains the servlet's response.
     * @param  chain      the filter chain to invoke the next filter
     *                    in the chain, or if the calling filter is
     *                    the last filter in the chain, to invoke
     *                    the resource at the end of the chain.
     *
     * @throws ServletException if any error occurs during request
     *                          processing.
     */
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        // Reset MDC content.
        MDC.clear();

        // And process request.
        chain.doFilter(request, response);
    }

    /**
     * Called by the web container to indicate to a filter that it
     * is being taken out of service. This method is only called
     * once all threads within the filter's <code>doFilter</code>
     * method have exited or after a timeout period has passed.
     * After the web container calls this method, it will not call
     * the <code>doFilter</code> method again on this instance of
     * the filter.
     */
    public void destroy() {
        // Nothing to destroy!
    }
}