package com.socialcomputing.serializers.json
{
	import com.adobe.serialization.json.JSON;
	import com.adobe.serialization.json.JSONDecoder;
	import com.socialcomputing.serializers.RESTSerializationFilter;
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.JSONToWPSDecoder;
	
	import mx.rpc.http.AbstractOperation;

	public class JSONSerializationFilter extends RESTSerializationFilter
	{
		public function JSONSerializationFilter()
		{
			super();
		}
		
		/**
		 * Deserialize the result provided by the operation call 
		 * with the open source JSON deserializer AS3Corelib  
		 * Object is the only resultType supported for now
		 *
		 * @param operation The AbstractOperation being invoked.
		 * @param result The result returned by the operation call
		 * @return An object resulting of the JSON deserialization 
		 */
		override public function deserializeResult(operation:AbstractOperation, result:Object):Object {
			if(result == null) return result;
			
			if(operation.resultType != Object) {
				throw new ArgumentError("The only supported return type is Object");
			}
			var json:Object = JSON.decode(result as String);
			
			var env:Env = JSONToWPSDecoder.toEnv(json.Env);
			return json;
		}
	}
}