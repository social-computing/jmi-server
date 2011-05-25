/**
 * This is a generated sub-class of _PlanService.as and is intended for behavior
 * customization.  This class is only generated when there is no file already present
 * at its target location.  Thus custom behavior that you add here will survive regeneration
 * of the super-class. 
 **/
 
package com.socialcomputing.wps.services.planservice
{
	import com.socialcomputing.serializers.RESTSerializationFilter;
	import com.socialcomputing.serializers.json.JSONSerializationFilter;
	
	import mx.rpc.AbstractOperation;
	import mx.rpc.AsyncToken;
	import mx.rpc.http.HTTPMultiService;
	import mx.rpc.http.Operation;
	import mx.rpc.http.SerializationFilter;
		
	public class PlanService extends HTTPMultiService {
 		
		private static var restSerializationFilter:JSONSerializationFilter = new JSONSerializationFilter();
		private static var engineInstance:String = "0";
		private static var jsonFormat:String = "json";

		// Initialize service control
		// "http://192.168.111.12:8180/wps/services/engine/"
		public function PlanService(baseURL:String) {
			super(baseURL);
			var operations:Array = new Array();
			var operation:mx.rpc.http.Operation;

			operation = new mx.rpc.http.Operation(null, "getPlan");
			operation.url = engineInstance + "/{name}." + jsonFormat;
			operation.method = "GET";

			operation.serializationFilter = restSerializationFilter;
			operation.properties = new Object();
			operation.properties[RESTSerializationFilter.URL_PARAMETERS_NAMES] = ["name"];
			operation.resultType = Object;
			operations.push(operation);
			this.operationList = operations;  
		}
		
		
		/**
		 * This method is a generated wrapper used to call the 'getPlan' operation. It returns an mx.rpc.AsyncToken whose 
		 * result property will be populated with the result of the operation when the server response is received. 
		 * To use this result from MXML code, define a CallResponder component and assign its token property to this method's return value. 
		 * You can then bind to CallResponder.lastResult or listen for the CallResponder.result or fault events.
		 *
		 * @see mx.rpc.AsyncToken
		 * @see mx.rpc.CallResponder 
		 *
		 * @return an mx.rpc.AsyncToken whose result property will be populated with the result of the operation when the server response is received.
		 */
		public function getPlan(name:String, width:int, height:int, parameters:Object) : mx.rpc.AsyncToken
		{
			var _internal_operation:mx.rpc.AbstractOperation = this.getOperation("getPlan");
			parameters["name"] = name;
			parameters["width"] = width;
			parameters["height"] = height;
			_internal_operation.arguments = parameters;
			return _internal_operation.send() ;
		}
	}
}
