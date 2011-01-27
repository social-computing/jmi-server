
package com.socialcomputing.utils.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

public class ExtendedRequest implements HttpServletRequest {
        private HttpServletRequest        origReq;
        private Hashtable                        params;

        /**
         * Creates a ExtendedRequest object. If the input is of type multipart/form-data
         * the input is parsed, and any uploaded file objects will be saved to
         * a temporary directory. Remember to call <CODE>deleteTemporaryFiles()</CODE> when
         * you are done, to delete these temporary files.
         *
         * @param request is the request object passed to the <CODE>service()</CODE>,
         *                <CODE>doGet()</CODE> or <CODE>doPost()</CODE> method.
         * @param tmpDir is the directory that may be used to hold temporary files.
        */
        public ExtendedRequest(HttpServletRequest request, File tmpDir) throws IOException {
                origReq = request;
                if(request.getContentType() != null &&
                                MultipartFormDataParser.getMIMEHeaderPrimaryValue(request.getContentType()).equals("multipart/form-data") &&
                                request.getMethod() != null &&
                                request.getMethod().equals("POST"))
                {
                        MultipartFormDataParser        parser = new MultipartFormDataParser(request.getContentType(),
                                                                                request.getInputStream(), request.getContentLength(), tmpDir);
                        params = parser.parse();
                } else {
                        params = null;
                }
        }

        /**
         * Creates a ExtendedRequest object. If the input is of type multipart/form-data
         * the input is parsed, and any uploaded file objects will be saved in memory.
         *
         * @param request is the request object passed to the <CODE>service()</CODE>,
         *                <CODE>doGet()</CODE> or <CODE>doPost()</CODE> method.
        */
        public ExtendedRequest(HttpServletRequest request) throws IOException {
                this(request, null);
        }

        /**
         * Deletes any temporary files created. This method should be called when
         * the ExtendedRequest was created with the temporary directory option.
         */
        public void deleteTemporaryFiles() {
                if(params != null)
                        MultipartFormDataParser.deleteTemporaryFiles(params);
        }

        /**
         * Returns the parameter names for this request as an enumeration
         * of strings, or an empty enumeration if there are no parameters
         * or the input stream is empty.
         */
        public Enumeration getParameterNames() {
                if(params == null) {
                        return origReq.getParameterNames();
                } else {
                        return params.keys();
                }
        }

        /**
         * Returns a string containing the lone value of the specified
         * parameter, or null if the parameter does not exist. If the
         * value is an uploaded file, the client-side filename
         * is returned.
         *
         * <P>Servlet writers should use this method only when they are
         * sure that
         * there is only one value for the parameter.  If the parameter
         * has (or could have) multiple values, servlet writers should
         * use getParameterValues. If a multiple valued parameter name
         * is passed as an argument, the return value is implementation
         * dependent.
         * </P>
         *
         * @param name the name of the parameter whose value is required.
         * @see #getParameterValues
         */
        public String getParameter(String name) {
                if(params == null) {
                        return origReq.getParameter(name);
                } else {
                        Object[]                vals;

                        vals = (Object[]) params.get(name);
                        if(vals == null) {
                                return null;
                        } else {
                                return vals[0].toString();
                        }
                }
        }

        /**
         * Returns the values of the specified parameter for the request as
         * an array of strings, or null if the named parameter does not
         * exist. If any of the parameter's values are uploaded files, the
         * client-side filenames are returned.
         *
         * @param name the name of the parameter whose value is required.
         * @see #getParameter
         */
        public String[] getParameterValues(String name) {
                if(params == null) {
                        return origReq.getParameterValues(name);
                } else {
                        Object[]                vals;
                        String[]                stringVals;
                        int                                i;

                        vals = (Object[]) params.get(name);
                        if(vals == null) {
                                return null;
                        } else {
                                stringVals = new String[vals.length];
                                for(i = 0; i < vals.length; i++) {
                                        stringVals[i] = vals[i].toString();
                                }
                                return stringVals;
                        }
                }
        }

        /**
         * Returns the lone uploaded file value of the specified
         * parameter, or null if the parameter does not exists
         * or is not an uploaded file value.
         *
         * @param name the name of the parameter whose value is required.
         * @see #getFileParameterValues
         */
        public UploadedFile getFileParameter(String name) {
                if(params == null) {
                        return null;
                } else {
                        Object[]                vals;

                        vals = (Object[]) params.get(name);
                        if(vals == null) {
                                return null;
                        } else if(!(vals[0] instanceof UploadedFile)) {
                                return null;
                        } else {
                                return (UploadedFile) vals[0];
                        }
                }
        }

        /**
         * Returns the uploaded file values of the specified parameter,
         * or null if the specified parameter does not exist. The array
         * contains null for a value that is not an uploaded file.
         *
         * @param name the name of the parameter whose value is required.
         * @see #getFileParameter
         */
        public UploadedFile[] getFileParameterValues(String name) {
                if(params == null) {
                        return null;
                } else {
                        Object[]                vals;
                        UploadedFile[]        fileVals;
                        int                                i;

                        vals = (Object[]) params.get(name);
                        if(vals == null) {
                                return null;
                        } else {
                                fileVals = new UploadedFile[vals.length];
                                for(i = 0; i < vals.length; i++) {
                                        if(vals[i] instanceof UploadedFile) {
                                                fileVals[i] = (UploadedFile) vals[i];
                                        } else {
                                                fileVals[i] = null;
                                        }
                                }
                                return fileVals;
                        }
                }
        }

        /**
         * Returns true when the specified parameter exists, false
         * otherwise.
         *
         * @param name the name of the parameter whose value is required.
         */
        public boolean getBooleanParameter(String name) {
                return getParameter(name) != null;
        }

        /**
         * Returns the lone value of the specified parameter converted to
         * an <CODE>int</CODE>, or the specified default if the value can not be
         * converted or the parameter does not exist.
         *
         * @param name the name of the parameter whose value is required
         * @param defaultValue the default value to use
         */
        public int getIntParameter(String name, int defaultValue) {
                String val = getParameter(name);

                if(val != null) {
                        try {
                                return Integer.parseInt(val);
                        } catch(NumberFormatException nfe) {
                                //
                        }
                }
                return defaultValue;
        }

        /**
         * Returns the lone value of the specified parameter converted to
         * a <CODE>double</CODE>, or the specified default if the value can not be
         * converted or the parameter does not exist.
         *
         * @param name the name of the parameter whose value is required
         * @param defaultValue the default value to use
         */
        public double getDoubleParameter(String name, double defaultValue) {
                String val = getParameter(name);

                if(val != null) {
                        try {
                                return Double.valueOf(val).doubleValue();
                        } catch(NumberFormatException nfe) {
                                //
                        }
                }
                return defaultValue;
        }

        /**
         * Returns the date value associated with the three parameters
         * that start with the specified prefix. The prefix is appended
         * with "<CODE>_day</CODE>", "<CODE>_month</CODE>" and "<CODE>_year</CODE>"
         * to find the day of the month, the month and the year respectivily.
         *
         * <P>The day is a number between 1 and 31 (inclusive) and the month is
         * a number between 1 and 12 (inclusive). The year is the real year,
         * <EM>not</EM> the year minus 1900. If the conversion failed or one of
         * the parameters does not exist, <CODE>null</CODE> is returned.
         *
         * @param prefix the prefix of the parameter whose value is required
         * @see org.gjt.vinny.servlet.HTMLWriter#printDateSelect
         */
        public java.util.Date getDateParameter(String prefix) {
                String        dayVal, monthVal, yearVal;
                //int                day, month, year;

                dayVal = getParameter(prefix + "_day");
                monthVal = getParameter(prefix + "_month");
                yearVal = getParameter(prefix + "_year");
                if(dayVal != null && dayVal.length() > 0 &&
                        monthVal != null && monthVal.length() > 0 &&
                        yearVal != null && yearVal.length() > 0)
                {
                        try {
                                GregorianCalendar cal = new GregorianCalendar();
                                cal.set(
                                        Integer.parseInt(yearVal) - 1900,
                                        Integer.parseInt(monthVal) - 1,
                                        Integer.parseInt(dayVal));
                                return cal.getTime();
                        } catch(NumberFormatException nfe) {
                                //
                        }
                }
                return null;
        }


        /*
         * These methods implement the ServletRequest interface
         */

        public Object getAttribute(String name) {
                return origReq.getAttribute(name);
        }

        public String getCharacterEncoding() {
                return origReq.getCharacterEncoding();
        }


        public int getContentLength() {
                return origReq.getContentLength();
        }

        public String getContentType() {
                return origReq.getContentType();
        }

        public ServletInputStream getInputStream() throws IOException {
                return origReq.getInputStream();
        }

        public String getProtocol() {
                return origReq.getProtocol();
        }

        public BufferedReader getReader() throws IOException {
                return origReq.getReader();
        }

        /*public String getRealPath(String virtualPath) {
                return origReq.getRealPath(virtualPath);
        }*/

        public String getRemoteAddr() {
                return origReq.getRemoteAddr();
        }

        public String getRemoteHost() {
                return origReq.getRemoteHost();
        }

        public String getScheme() {
                return origReq.getScheme();
        }

        public String getServerName() {
                return origReq.getServerName();
        }

        public int getServerPort() {
                return origReq.getServerPort();
        }

        /*
         * These methods implement the HttpServletRequest interface
         */

        public String getAuthType() {
                return origReq.getAuthType();
        }

        public Cookie[] getCookies() {
                return origReq.getCookies();
        }

        public long getDateHeader(String name) {
                return origReq.getDateHeader(name);
        }

        public String getHeader(String name) {
                return origReq.getHeader(name);
        }

        public Enumeration getHeaderNames() {
                return origReq.getHeaderNames();
        }

        public int getIntHeader(String name) {
                return origReq.getIntHeader(name);
        }

        public String getMethod() {
                return origReq.getMethod();
        }

        public String getPathInfo() {
                return origReq.getPathInfo();
        }

        public String getPathTranslated() {
                return origReq.getPathTranslated();
        }

        public String getQueryString() {
                return origReq.getQueryString();
        }

        public String getRemoteUser() {
                return origReq.getRemoteUser();
        }

        public String getRequestedSessionId() {
                return origReq.getRequestedSessionId();
        }

        public String getRequestURI() {
                return origReq.getRequestURI();
        }

        public StringBuffer getRequestURL() {
                return origReq.getRequestURL();
        }

        public String getServletPath() {
                return origReq.getServletPath();
        }

        public HttpSession getSession(boolean create) {
                return origReq.getSession(create);
        }

        public boolean isRequestedSessionIdFromCookie() {
                return origReq.isRequestedSessionIdFromCookie();
        }

        public boolean isRequestedSessionIdFromUrl() {
                return origReq.isRequestedSessionIdFromURL();
        }

        public boolean isRequestedSessionIdValid() {
                return origReq.isRequestedSessionIdValid();
        }

        public boolean isUserInRole(java.lang.String role) {
                return origReq.isUserInRole( role);
        }

        public boolean isRequestedSessionIdFromURL() {
                return origReq.isRequestedSessionIdFromURL();
        }

        public java.security.Principal getUserPrincipal() {
                return origReq.getUserPrincipal();
        }

        public HttpSession getSession() {
                return origReq.getSession();
        }

        public java.util.Enumeration getHeaders(java.lang.String name) {
                return origReq.getHeaders( name);
        }

        public java.lang.String getContextPath() {
                return origReq.getContextPath();
        }

        public void setAttribute(java.lang.String name,java.lang.Object o)        {
                origReq.setAttribute( name, o);
        }

        public void removeAttribute(java.lang.String name)        {
                origReq.removeAttribute( name);
        }

        public boolean isSecure() {
                return origReq.isSecure( );
        }

        public javax.servlet.RequestDispatcher getRequestDispatcher(java.lang.String path) {
                return origReq.getRequestDispatcher( path);
        }

        public java.lang.String getRealPath(java.lang.String path) {
                return null;//origReq.getRealPath( path);
        }

        public java.util.Locale getLocale() {
                return origReq.getLocale( );
        }

        public java.util.Enumeration getLocales() {
                return origReq.getLocales( );
        }

        public java.util.Enumeration getAttributeNames() {
                return origReq.getAttributeNames( );
        }
        public java.util.Map getParameterMap() {
                return origReq.getParameterMap( );
        }

        public void setCharacterEncoding(java.lang.String env) throws java.io.UnsupportedEncodingException {
                origReq.setCharacterEncoding(env);
        }

		public int getRemotePort() {
			return origReq.getRemotePort();
		}

		public String getLocalAddr() {
			return origReq.getLocalAddr();
		}

		public String getLocalName() {
			return origReq.getLocalName();
		}

		public int getLocalPort() {
			return origReq.getLocalPort();
		}
}
