TestCase("JMITestCase", {
    // oldNS to store the existing root ns before test
    oldNS:{},
    
    setUp: function() {
        // Reset root namespace
        jstestdriver.console.log("reset root namespace before test");
        oldNS = JMI;
        JMI = {}; 
        JMI.namespace = oldNS.namespace;
    },
    
    tearDown: function() {
        // Restore root namespaces
        JMI = oldNS;
        jstestdriver.console.log("restore root namespace after test");
    },
    
    
    "test empty namespace": function() {
        // Empty argument return the root namespace
        assertEquals(JMI, JMI.namespace());
        
        // Empty string argument also return the root namespace
        assertEquals(JMI, JMI.namespace(""));
    },
   
    "test simple namespace with no dot": function() {
        // Adding a new subname to the namespace returns a new object
        // and this object is added to the JMI root namespace 
        var ns = JMI.namespace("com");
        assertEquals({}, ns);
        assertEquals({}, JMI.com);
        assertEquals(JMI.com, ns);
        
        // Calling the namespace with an empty argument again
        // should return the new root namespace with com as subname
        assertEquals(JMI, JMI.namespace());
    },
    
    "test add a full namespace as a package in java": function() {
        // Adding a new subname to the namespace returns a new object
        // and this object is added to the JMI root namespace 
        var ns = JMI.namespace("com.socialcomputing");
        assertEquals({}, ns);
        assertEquals({}, JMI.com.socialcomputing);
        assertEquals({socialcomputing:{}}, JMI.com);
    },
    
    "test extend an existing package" : function() {
        // Add com.socialcomputing to the root namespace
        var ns = JMI.namespace("com.socialcomputing");
        assertEquals({}, ns);
        assertEquals({}, JMI.com.socialcomputing);
        assertEquals({socialcomputing:{}}, JMI.com);
        
        // then add jmi to the com.socialcomputing namespace
        var nsjmi = JMI.namespace("com.socialcomputing.jmi");
        assertEquals({}, nsjmi);
        assertEquals({jmi:{}}, JMI.com.socialcomputing);
        assertEquals({socialcomputing:{jmi:{}}}, JMI.com);
        
        // finally add tools as another child to the com.socialcomputing namesapce
        var nstools = JMI.namespace("com.socialcomputing.tools");
        assertEquals({}, nstools);
        assertEquals({jmi:{}, tools:{}}, JMI.com.socialcomputing);
        assertEquals({socialcomputing:{jmi:{}, tools:{}}}, JMI.com);
    }
});