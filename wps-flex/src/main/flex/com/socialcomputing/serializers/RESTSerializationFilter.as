package com.socialcomputing.serializers
{
	import mx.core.mx_internal;
	import mx.rpc.http.AbstractOperation;
	import mx.rpc.http.SerializationFilter;
	
	public class RESTSerializationFilter extends SerializationFilter {
		public static var URL_PARAMETERS_NAMES:String = "urlParametersNames";
		private static var URL_PARAMETERS:String = "urlParameters";
		private static var PARAM_BEGIN_TOKEN:String = "{";
		private static var PARAM_END_TOKEN:String = "}";
		
		public function RESTSerializationFilter()
		{
			super();
		}
		
		/**
		 * This method is called from the "send" method of the HTTP service invocation to convert the
		 * parameters into a request body.  The parameters of the original send call are put into the
		 * params array.  This method converts this set of parameters into to a single object which is used as the 
		 * data for the HTTP request body.  The default implementation produces an object where the 
		 * keys are the values in the Operation's argumentNames array and the values are the values of the parameters.
		 * When using the default implementation, you must set argumentNames to have the same number
		 * of elements as the parameters array.
		 * 
		 * <p>Note that this method is not used if you invoke the HTTP operation using the sendBody
		 * method which just takes a single object.  In that case, this step is skipped and only
		 * the serializeBody method is called.</p>
		 *
		 * @param operation The AbstractOperation being invoked.
		 * @param params the list of parameters passed to the send method
		 * @return the body to be used in the HTTP request
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 9
		 *  @playerversion AIR 1.1
		 *  @productversion Flex 3
		 */
		override public function serializeParameters(operation:AbstractOperation, params:Array):Object {
			if (params == null || params.length == 0)
				return params;
			
			// If a list of url parameters names are set then get the values in the
			// url parameters array
			var urlParamNames:Array;
			if(operation.properties != null &&
			   operation.properties.hasOwnProperty(URL_PARAMETERS_NAMES) && 
			   operation.properties[URL_PARAMETERS_NAMES] != null) {
			   
			   urlParamNames = operation.properties[URL_PARAMETERS_NAMES] as Array;
			} 
			
			// Special case for XML content type when only one parameter is provided.
			// If there is more than one parameter though, we do not have a reliable way
			// to turn the arguments into a body.
			/*
			if ((params.length - urlParamNames.length) == 1 && operation.contentType == mx_internal::CONTENT_TYPE_XML) {
				
				// Last parameter
				var parameter:Object = params[(params.length -1)];
				// Need to get extract URL paramters here
				return parameter;
			}
			*/
			
			var opArgNames:Array = operation.argumentNames;
			if (opArgNames == null || params.length != opArgNames.length)
				throw new ArgumentError("HTTPMultiService operation called with " + (opArgNames == null ? 0 : opArgNames.length) + " argumentNames and " + params.length + " number of parameters.  When argumentNames is specified, it must match the number of arguments passed to the invocation");

			return this.extractURLParameters(operation, urlParamNames, params);
		}
		
		
		
		/**
		 * This method is used if you need to take data from the request body object and encode
		 * it into the URL string.  It is given the incoming URL as configured on the operation
		 * or service.  This implementation just returns the incoming URL without any conversion.
		 *
		 * @param operation The AbstractOperation being invoked
		 * @param url the URL set on the service or operation
		 * @return the potentially modified URL to use for this request.
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 9
		 *  @playerversion AIR 1.1
		 *  @productversion Flex 3
		 */
		override public function serializeURL(operation:AbstractOperation, obj:Object, url:String):String {
			
			if(url != null) {
				if(operation.properties != null &&
					operation.properties.hasOwnProperty(URL_PARAMETERS) && 
					operation.properties[URL_PARAMETERS] != null) {
					
					// Iterate throw url parameters and replace all the occurence of
					// the url pattern PARAM_BEGIN_TOKEN + parameterName + PARAM_END_TOKEN by the corresponding value
					var parameters:Object = operation.properties[URL_PARAMETERS];
					for(var parameterName:String in parameters){
						var parameterPattern:String = PARAM_BEGIN_TOKEN + parameterName + PARAM_END_TOKEN;
						if(url.indexOf(parameterName) != -1) {
							url = url.replace(new RegExp(parameterPattern, "//g"), parameters[parameterName]);
						}
					}
				} 
			}
			return url;
		}
		
		/**
		 * Extract url parameters values from the parameters Array.
		 * The tuples of (paramNames, paramValues) are stored as an Object in the operation property URL_PARAMETERS
		 * 
		 * @param operation The AbstractOperation being invoked
		 * @param urlParamName an array of URL parameters names
		 * @param params an array of parameters values
		 * 
		 * @return the remaining parameters that are not extracted from 
		 */
		private function extractURLParameters(operation:AbstractOperation, urlParamNames:Array, params:Array):Object {
			
			var nonURLParams:Object = {};
			var urlParams:Object = {}
			var opArgNames:Array = operation.argumentNames;
			
			// Iterate throw all operation argument names
			for(var i:uint = 0 ;  i < opArgNames.length ; i++) {
				// If the url parameter name is found in the operation argument names, extract it's value
				// and place it in the operation property URL_PARAMETERS
				if(urlParamNames != null && urlParamNames.indexOf(opArgNames[i]) != -1) {
					urlParams[opArgNames[i]] = params[i];
				}
				// else put it in the nonURLParameters Object
				else {
					nonURLParams[opArgNames[i]] = params[i];
				}
			}

			// Place the url parameters values in the operation property URL_PARAMETERS
			if(urlParams != null) {
				// Create the operation properties object if it doesn't exist
				if(operation.properties == null) {
					operation.properties = {};
				} 
				operation.properties[URL_PARAMETERS] = urlParams;
			}
			
			// Return remaining non url parameters
			return nonURLParams;
		}
	}
}


