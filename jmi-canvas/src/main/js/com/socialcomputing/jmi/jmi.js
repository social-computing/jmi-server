var JMI = JMI || {}

/**
 * namespace creation function
 * create a JMI object to manipulate a java like namespace
 * dot is used as a object separator
 * 
 * @param  ns_string the namespace identifier (ie : com.socialcomputing.AClass)
 */
JMI.namespace = function(ns_string) {

    // if no argument or an empty string is given, return the JMI root namespace
    if(typeof ns_string === "undefined" || ns_string === "") return JMI; 
    
    var parts  = ns_string.split('.'); 
    var parent = JMI; 
    var i;
        
    // Iterating throw all namespace components and creating 
    // objects when necessary
    for(i = 0 ; i < parts.length ; i += 1) {
        if(typeof parent[parts[i]] === "undefined") {
            parent[parts[i]] = {};
        }
        parent = parent[parts[i]];
    }
    
    // returns the deepest lvl component (object) of the given namespace
    return parent;
};