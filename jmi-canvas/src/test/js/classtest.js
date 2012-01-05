/**
 * @author spiroid
 */

/**
 * Animal class definition
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
 */
// Redefine constructor 
var Cat = function(name, age, weight) {
    // call the parent class constructor
    Animal.call(this, name, age);
    this.weight = weight;
};


// Inherit from Animal methods 
Cat.prototype = new Animal();
Cat.prototype.constructor = Animal;


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