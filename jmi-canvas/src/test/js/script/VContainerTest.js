/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 */
TestCase("VcontainerTestCase", {    
    "test Vcontainer instances creation": function() {
        var vContainer1 = new JMI.script.VContainer(25, false);
        var vContainer2 = new JMI.script.VContainer("hello");
        
        assertEquals(25, vContainer1.value);
        assertEquals(false, vContainer1.isBound);
     
        assertEquals("hello", vContainer2.value);
        assertEquals(false, vContainer2.isBound);
    }
});