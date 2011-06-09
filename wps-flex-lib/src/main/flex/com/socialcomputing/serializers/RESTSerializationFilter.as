package com.socialcomputing.serializers
{
	import mx.core.mx_internal;
	import mx.rpc.http.AbstractOperation;
	import mx.rpc.http.SerializationFilter;
	
	public class RESTSerializationFilter extends SerializationFilter {
		public static var URL_PARAMETERS_NAMES:String = "urlParametersNames";
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
			return operation.arguments;
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
					operation.properties.hasOwnProperty(URL_PARAMETERS_NAMES) && 
					operation.properties[URL_PARAMETERS_NAMES] != null) {
					
					// Iterate throw url parameters and replace all the occurence of
					// the url pattern PARAM_BEGIN_TOKEN + parameterName + PARAM_END_TOKEN by the corresponding value
					var parameters:Object = operation.properties[URL_PARAMETERS_NAMES];
					for(var parameterName:String in parameters){
						var parameterPattern:String = PARAM_BEGIN_TOKEN + parameters[parameterName] + PARAM_END_TOKEN;
						if(url.indexOf(parameterPattern) != -1) {
							url = url.replace(new RegExp(parameterPattern, "//g"), operation.arguments[parameters[parameterName]]);
						}
					}
				} 
			}
			return url;
		}
		
	}
}


