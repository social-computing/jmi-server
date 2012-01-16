/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 */
TestCase("DimensionTestCase", {    
    "test Dimension instances creation": function() {
        // Create a dimension giving no arguments = with 0 width and height
        var d1 = new JMI.script.Dimension();
        assertEquals(0, d1.width);
        assertEquals(0, d1.height);
        
        // Create with width and height values
        d1 = new JMI.script.Dimension(4, 6);
        assertEquals(4, d1.width);
        assertEquals(6, d1.height);
        
        // Create from a rectangle instance
        d1 = new JMI.script.Dimension(new JMI.script.Rectangle(5, 5, 10, 20));
        assertEquals(10, d1.width);
        assertEquals(20, d1.height);
    },
    
    "test create a rectangle width height and width of a dimension": function() {
        // Create with width and height values
        var d1 = new JMI.script.Dimension(4, 6);

        // Get a rectangle from the dimension
        var r1 = d1.toRectangle();
        assertEquals(0, r1.x);
        assertEquals(0, r1.y);
        assertEquals(4, r1.width);
        assertEquals(6, r1.height);
    },
    
    "test resize a dimension from another dimension": function() {
        // Create with width and height values
        var d1 = new JMI.script.Dimension(4, 6);
        
        // Resize from a dimension greater than the first one
        var d2 = d1.resize(new JMI.script.Dimension(5, 7));
        assertEquals(5, d2.width);
        assertEquals(7, d2.height);
        
        // Resize from a dimension with only width greater than the first dimension 
        d2 = d1.resize(new JMI.script.Dimension(5, 4));
        assertEquals(5, d2.width);
        assertEquals(6, d2.height);
        
        // Resize from a dimension with only height greater than the first dimension 
        d2 = d1.resize(new JMI.script.Dimension(3, 8));
        assertEquals(4, d2.width);
        assertEquals(8, d2.height);
    }
});
