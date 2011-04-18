/**
 * This is a generated sub-class of _PlanService.as and is intended for behavior
 * customization.  This class is only generated when there is no file already present
 * at its target location.  Thus custom behavior that you add here will survive regeneration
 * of the super-class. 
 **/
 
package com.socialcomputing.wps.services.planservice
{
	import com.adobe.fiber.core.model_internal;
	import com.adobe.fiber.services.wrapper.HTTPServiceWrapper;
	import com.adobe.serializers.json.JSONSerializationFilter;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.http.HTTPMultiService;
	
	public class PlanService extends com.adobe.fiber.services.wrapper.HTTPServiceWrapper {
 		
		private static var jsonSerializerFilter:JSONSerializationFilter = new JSONSerializationFilter();
		private static var engineInstance:String = "0";
		private static var jsonFormat:String = "json";

		// Initialize service control
		// "http://192.168.111.12:8180/wps/services/engine/"
		public function PlanService(baseURL:String, planName:String) {

			_serviceControl = new mx.rpc.http.HTTPMultiService(baseURL);
			var operations:Array = new Array();
			var operation:mx.rpc.http.Operation;
			
			operation = new mx.rpc.http.Operation(null, "getPlan");
			//operation.url = "0/{planName}.json";
			operation.url = engineInstance + "/" + planName + "." + jsonFormat;
			operation.method = "GET";
			operation.argumentNames = new Array("entityId");
			//operation.properties = new Object();
			//operation.properties["urlParamNames"] = ["planName"];
			operation.resultType = Object;
			operations.push(operation);
			
			//operation.serializationFilter = jsonSerializerFilter;
			//operation.resultType = valueObjects.Plan;

			
			_serviceControl.operationList = operations;  
			model_internal::initialize();
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
		public function getPlan(entityId:int) : mx.rpc.AsyncToken
		{
			var _internal_operation:mx.rpc.AbstractOperation = _serviceControl.getOperation("getPlan");
			var _internal_token:mx.rpc.AsyncToken = _internal_operation.send(entityId) ;
			
			return _internal_token;
		}
	}
}
