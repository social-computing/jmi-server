/**
 * @author spiroid
 */

/**
 * Animal class definition
 * Using the prototype javascript 'native' method
 */
var Animal = function(name, age){
    this.name = name;
    this.age  = age;
};
Animal.prototype.talk = function() {
    return 'mumble';
};
Animal.prototype.sleep = function() {
    return 'zzzzz';
};

/**
 * Create a class Cat that inherits from Animal
 * Using #3 classical pattern method - Rent and Set prototype 
 */
// Child constuctor   
var Cat = function(name, age, weight) {
    // Call the parent class constructor
    Animal.apply(this, arguments);
    this.weight = weight;
};

// Inherit from Animal methods
// Calling the animal constuctor with empty parameters 
Cat.prototype = new Animal(); 
// Redefine constructor : usefull for runtime instrospection
Cat.prototype.constructor = Cat;


// Override talk method
Cat.prototype.talk = function() {
    return 'maow';
};


TestCase("Constructor TestCase", {    
    "test class constructor": function() {
        var piggy = new Animal("piggy", 25);
        var tobby = new Animal("tobby", 32);
        
        assertEquals('piggy', piggy.name);
        assertEquals(25, piggy.age);
        assertEquals('mumble', piggy.talk());
        assertEquals('zzzzz', piggy.sleep());
        
        assertEquals('tobby', tobby.name);
        assertEquals(32, tobby.age);
        assertEquals('mumble', tobby.talk());
        assertEquals('zzzzz', tobby.sleep());
    }
});

TestCase("Inheritence TestCase", {    
    "test class constructor": function() {
        var piggy = new Animal("piggy", 25);
        var tobby = new Cat("tobby", 32, 4);
        
        assertEquals('piggy', piggy.name);
        assertEquals(25, piggy.age);
        assertEquals('mumble', piggy.talk());
        assertEquals('zzzzz', piggy.sleep());
        
        assertEquals('tobby', tobby.name);
        assertEquals(32, tobby.age);
        assertEquals(4, tobby.weight);
        assertEquals('maow', tobby.talk());
        assertEquals('zzzzz', tobby.sleep());
    }
});