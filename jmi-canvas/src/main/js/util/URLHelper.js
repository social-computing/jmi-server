JMI.namespace("util.controls.Transfo");


JMI.util.controls.Transfo = (function () {

    
})();

		
// Public static methods		
/**
 *  Converts a potentially relative URL to a fully-qualified URL.
 *  If the URL is always considered relative
 *  This code is taken form mx.utils.URLUtil, and file URL support is added
 *
 *  @param rootURL URL used to resolve the URL specified by the <code>url</code> parameter, if <code>url</code> is relative. //:String
 *  @param url URL to convert. //:String
 *
 *  @return Fully-qualified URL. //:String
 * 
 */
// TODO portage : voir si on garde la classe
//              : essayer de mettre les methodes directement dans le module
//              : equivalent de URLUtil à mettre en place 
JMI.util.controls.Transfo.getFullURL = function(rootURL, url) {
	if (url != null && !URLUtil.isHttpURL(url) && !URLHelper.isFileURL(url)) {
		if (url.indexOf("./") == 0) {
			url = url.substring(2);
		}
		
		if (URLUtil.isHttpURL(rootURL) || URLHelper.isFileURL(rootURL)) {
			// relative path, starting with a "/", "/dev/foo.bar"
			if (url.charAt(0) == '/') {
				url = url.substring(1);	
			}
			
			// relative path, "dev/foo.bar". //:Number
			var slashPos = rootURL.lastIndexOf("/") + 1;
			
			// Special case
			if (slashPos <= 8) {
				rootURL += "/";
				slashPos = rootURL.length;
			}
			
			url = rootURL.substring(0, slashPos) + url;
		}
	}
	return url;
};


/**
 *  Determines if the URL begins with the file:// scheme
 *
 *  @param url The URL to analyze. //:String
 *  @return <code>true</code> if the URL starts with "file://", false otherwise. //:Boolean
 */
JMI.util.controls.Transfo.isFileURL = function(url) {
    url = url || "";
	return url.indexOf("file://") === 0;
};