/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 */
TestCase("PointTestCase", {    
    "test Point instances creation": function() {
        // Create a point giving no arguments = at 0 abs and 0 ord
        var point1 = new JMI.script.Point();
        assertEquals(0, point1.x);
        assertEquals(0, point1.y);
        
        // create a point from the previous one
        var point2 = new JMI.script.Point(point1);
        assertEquals(0, point2.x);
        assertEquals(0, point2.y);
        point2.x = 5;
        assertEquals(5, point2.x);
        assertEquals(0, point1.x);
        
        // Create a point with 2 arguments
        var point3 = new JMI.script.Point(6, 7);
        assertEquals(6, point3.x);
        assertEquals(7, point3.y);        
    },
    
    "test Point clone": function() {
        // Create a point giving no arguments = at 0 abs and 0 ord
        var point1 = new JMI.script.Point();
        
        // create a point from the previous one
        var point2 = point1.clone();
        assertEquals(0, point2.x);
        assertEquals(0, point2.y);
        point2.x = 5;
        assertEquals(5, point2.x);
        assertEquals(0, point1.x);
    },
    
    "test Add 2 points": function() {
        // Create a point giving no arguments = at 0 abs and 0 ord
        var point1 = new JMI.script.Point();
        
        var point2 = point1.add(new JMI.script.Point(2, 3));
        assertEquals(2, point1.x);
        assertEquals(3, point1.y);
        assertEquals(point1, point2);
    },
    
    "test Substract 2 points": function() {
        var point1 = new JMI.script.Point(2, 3);
        
        // The point to substract is < to the current point
        var point2 = point1.substract(new JMI.script.Point(1, 1));
        assertEquals(1, point1.x);
        assertEquals(2, point1.y);
        
        // Test method chaining
        assertEquals(point1, point2);
        
        // The abs of the point to substract is > to the abs of the current point
        point1.substract(new JMI.script.Point(3, 0));
        assertEquals(2, point1.x);
        assertEquals(2, point1.y);
        
        // The ord of the point to substract is > to the ord of the current point
        point1.substract(new JMI.script.Point(0, 3));
        assertEquals(2, point1.x);
        assertEquals(1, point1.y);
        
        // The point to substract is > to the current point
        point1.substract(new JMI.script.Point(3, 2));
        assertEquals(1, point1.x);
        assertEquals(1, point1.y);
        
        // The point to substract is = to the current point
        point1.substract(new JMI.script.Point(1, 1));
        assertEquals(0, point1.x);
        assertEquals(0, point1.y);
    },
    
    "test Pivot a point": function() {
        var point1 = new JMI.script.Point(3, 2).pivot();
        var point2 = new JMI.script.Point(-2, 3);
        assertEquals(point2, point1);
    },
    
    "test Scale a 2^16 normalized point": function() {
        var point1 = JMI.script.Point.Scale(new JMI.script.Point((3 << 16), (2 << 16)), 4);
        assertEquals(new JMI.script.Point(12, 8), point1);
        
        point1 = JMI.script.Point.Scale(new JMI.script.Point((3.1 << 16), (2.8 << 16)), 4);
        assertEquals(new JMI.script.Point(12, 8), point1);
    }
});
