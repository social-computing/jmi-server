/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 */
TestCase("RectangleTestCase", {    
    "test Rectangle instances creation": function() {
        // Create a rectangle giving no arguments = at 0 abs and 0 ord with 0 width and height
        var r1 = new JMI.script.Rectangle();
        assertEquals(0, r1.x);
        assertEquals(0, r1.y);
        assertEquals(0, r1.width);
        assertEquals(0, r1.height);
        
        // Create a rectangle with all parmeters
        var r2 = new JMI.script.Rectangle(11, 12, 21, 22);
        assertEquals(11, r2.x);
        assertEquals(12, r2.y);
        assertEquals(21, r2.width);
        assertEquals(22, r2.height);
        
        // Create a rectangle from another one
        var r3 = new JMI.script.Rectangle(r2);
        r2.x = 13;
        assertEquals(11, r3.x);
        assertEquals(12, r3.y);
        assertEquals(21, r3.width);
        assertEquals(22, r3.height);
        
        // Create with only width and height
        var r4 = new JMI.script.Rectangle(10, 10);
        assertEquals(0, r4.x);
        assertEquals(0, r4.y);
        assertEquals(10, r4.width);
        assertEquals(10, r4.height);
    },
    
    "test copy from another rectangle": function() {
        var r1 = new JMI.script.Rectangle(11, 12, 21, 22);
        var r2 = new JMI.script.Rectangle();
        r2.copy(r1);
        assertEquals(11, r2.x);
        assertEquals(12, r2.y);
        assertEquals(21, r2.width);
        assertEquals(22, r2.height);
    },
    
    "test union with another rectangle": function() {
        var r1 = new JMI.script.Rectangle(1, 1, 20, 20);
        
        // Union with a larger rectangle containg the first one
        r1.union(new JMI.script.Rectangle(0, 0, 30, 30));
        assertEquals(0, r1.x);
        assertEquals(0, r1.y);
        assertEquals(30, r1.width);
        assertEquals(30, r1.height);
        
        // Union with a smaller rectangle contained by the first one
        // TODO : is this correct ? check with franck
        /*
        r1.union(new JMI.script.Rectangle(1, 1, 20, 20));
        assertEquals(0, r1.x);
        assertEquals(0, r1.y);
        assertEquals(30, r1.width);
        assertEquals(30, r1.height);
        */
        
        // Union of 2 unlinked rectangles        
        r1 = new JMI.script.Rectangle(20, 50, 60, 30);
        r1.union(new JMI.script.Rectangle(150, 130, 50, 30));
        assertEquals(20, r1.x);
        assertEquals(50, r1.y);
        assertEquals(180, r1.width);
        assertEquals(110, r1.height);
        
        // Union of partially joined rectangles
        r1 = new JMI.script.Rectangle(0, 0, 10, 10);
        r1.union(new JMI.script.Rectangle(5, 5, 10, 10));
        assertEquals(0, r1.x);
        assertEquals(0, r1.y);
        assertEquals(15, r1.width);
        assertEquals(15, r1.height);
    },
    
    "test merge from another rectangle": function() {
        // If the current rectangle has non null dimensions
        var r1 = new JMI.script.Rectangle(0, 0, 10, 10);
        r1.merge(new JMI.script.Rectangle(5, 5, 10, 10));
        assertEquals(0, r1.x);
        assertEquals(0, r1.y);
        assertEquals(15, r1.width);
        assertEquals(15, r1.height);
        
        // If the current rectangle has a null width
        r1 = new JMI.script.Rectangle(0, 0, 0, 10);
        r1.merge(new JMI.script.Rectangle(5, 5, 10, 10));
        assertEquals(5, r1.x);
        assertEquals(5, r1.y);
        assertEquals(10, r1.width);
        assertEquals(10, r1.height);
        
        // If the current rectangle has a null height
        r1 = new JMI.script.Rectangle(0, 0, 10, 0);
        r1.merge(new JMI.script.Rectangle(5, 5, 10, 10));
        assertEquals(5, r1.x);
        assertEquals(5, r1.y);
        assertEquals(10, r1.width);
        assertEquals(10, r1.height);
    },
    
    "test inflate a rectangle with the given dimensions": function() {
        var r1 = new JMI.script.Rectangle(0, 0, 10, 10);
        r1.inflate(20, 20);
        assertEquals(-20, r1.x);
        assertEquals(-20, r1.y);
        assertEquals(50, r1.width);
        assertEquals(50, r1.height);
        
        r1 = new JMI.script.Rectangle(-5, 5, 10, 10);
        r1.inflate(20, 20);
        assertEquals(-25, r1.x);
        assertEquals(-15, r1.y);
        assertEquals(50, r1.width);
        assertEquals(50, r1.height);
        
        // only on one dimension
        r1 = new JMI.script.Rectangle(0, 0, 10, 10);
        r1.inflate(0, 20);
        assertEquals(0, r1.x);
        assertEquals(-20, r1.y);
        assertEquals(10, r1.width);
        assertEquals(50, r1.height);
    }
});
