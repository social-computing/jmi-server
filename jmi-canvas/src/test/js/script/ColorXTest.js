/**
 * @author Jonathan Dray <jonathan@social-computing.com>
 */
TestCase("ColorXTestCase", {    
    "test RGB components of a color": function() {
        var c1 = new JMI.script.ColorX(255 << 16);
        assertEquals(255, c1.getRed());
        assertEquals(0, c1.getGreen());
        assertEquals(0, c1.getBlue());
        
        c1 = new JMI.script.ColorX(255 << 8);
        assertEquals(0, c1.getRed());
        assertEquals(255, c1.getGreen());
        assertEquals(0, c1.getBlue());

        c1 = new JMI.script.ColorX(255);
        assertEquals(0, c1.getRed());
        assertEquals(0, c1.getGreen());
        assertEquals(255, c1.getBlue());

        c1 = new JMI.script.ColorX(128 << 16);
        assertEquals(128, c1.getRed());
        assertEquals(0, c1.getGreen());
        assertEquals(0, c1.getBlue());
        
        c1 = new JMI.script.ColorX((255 << 16) + (255 << 8) + 255);
        assertEquals(255, c1.getRed());
        assertEquals(255, c1.getGreen());
        assertEquals(255, c1.getBlue());

        c1 = new JMI.script.ColorX(0);
        assertEquals(0, c1.getRed());
        assertEquals(0, c1.getGreen());
        assertEquals(0, c1.getBlue());        
    },
    
    "test rgb() string equivalent of the color": function() {
        var c1 = new JMI.script.ColorX(0);
        assertEquals("rgb(0, 0, 0)", c1.getRGBString());
    },
    
    "test hex value of the color": function() {
        assertEquals("#000000", new JMI.script.ColorX(0).toHex());
        assertEquals("#ff0000", new JMI.script.ColorX(255 << 16).toHex());
        //c1 = new JMI.script.ColorX(0);
    }
});
