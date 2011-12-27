var JMI_MAP = JMI_MAP || {}

JMI_MAP.namespace = function(ns_string) {
    ns_string = ns_string || "";
    var parts  = ns_string.split('.'), 
        parent = JMI_MAP, 
        i;
    if(parts[0] === "JMI_MAP") {
        parts = parts.slice(1);
    }
    for(i = 0 ; i < parts.length ; i += 1) {
        if(typeof parent[parts[i]] === "undefined") {
            parent[parts[i]] = {};
        }
        parent = parent[parts[i]];
    }
    return parent;
};