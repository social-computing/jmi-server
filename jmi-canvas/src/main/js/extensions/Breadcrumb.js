/*global define, JMI */
JMI.namespace("extensions.Breadcrumb");

JMI.extensions.Breadcrumb = ( function() {

	var Breadcrumb = function(parent,map,parameters) {
		this.crumbs = [];
		this.counter = 0;
		this.namingFunc = parameters.namingFunc ? parameters.namingFunc : this.defaultNaming;
		if(!parent) {
			throw 'JMI breadcrumb: parent id not set';
		}
		if( typeof parent === "string") {
			this.parent = document.getElementById(parent);
			if(this.parent === null) {
				throw 'JMI breadcrumb: unknown parent element ' + parent;
			}
		} else if( typeof parent === "object") {
			this.parent = parent;
		} else {
			throw 'JMI breadcrumb: invalid parent ' + parent;
		}
		if( map && !(map instanceof JMI.components.CanvasMap) && !(map instanceof JMI.components.SwfMap)) {
			throw 'JMI breadcrumb: invalid map ' + map;
		}
		this.map = map;
		if( this.map) {
			var p, breadcrumb = this;
			this.map.addEventListener(JMI.Map.event.START, function(event) {
				var crumb = breadcrumb.crumbs.length > 0 ? breadcrumb.crumbs[breadcrumb.crumbs.length-1] : null;
				if( crumb && crumb.self) {
					delete crumb.self;
				}
				else {
					crumb = {};
					crumb.params = {};
					for (p in event.params) {
						crumb.params[p] = event.params[p];
					}
					breadcrumb.crumbs.push( crumb);
				}
			} );
			this.map.addEventListener(JMI.Map.event.EMPTY, function(event) {
				var crumb = breadcrumb.crumbs[breadcrumb.crumbs.length-1];
				crumb.shortTitle = 'Empty';
				crumb.longTitle = 'Sorry map is empty';
				crumb.empty = true;
				breadcrumb.display();
			});
			this.map.addEventListener(JMI.Map.event.ERROR, function(event) {
				var crumb = breadcrumb.crumbs[breadcrumb.crumbs.length-1];
				crumb.shortTitle = 'Error';
				crumb.longTitle = event.message;
				crumb.error = true;
				breadcrumb.display();
			});
			this.map.addEventListener(JMI.Map.event.READY, function(event) {
				breadcrumb.counter++;
				var crumb = breadcrumb.crumbs[breadcrumb.crumbs.length-1];
				if( !crumb.shortTitle) {
					var res = breadcrumb.namingFunc(event);
					if( res.shortTitle) {
						crumb.shortTitle = res.shortTitle;
						crumb.longTitle = crumb.shortTitle;
					}
					if( res.longTitle) {
						crumb.longTitle = res.longTitle;
					}
				}
				breadcrumb.display();
			} );
		}
	};

	Breadcrumb.prototype = {
		constructor : JMI.extensions.Breadcrumb,

		display: function() {
			var i, lu = document.createElement('lu');
			lu.className = 'jmi-breadcrumb';
			for( i = 0; i < this.crumbs.length; ++i) {
				lu.appendChild( this.getCrumb(this.crumbs[i]));
			}
			if(this.parent.firstChild) {
				this.parent.removeChild(this.parent.firstChild);
			}
			this.parent.appendChild(lu);
		},
		flush: function() {
			this.crumbs.length = 0;
		},
		getCrumb: function(crumb) {
			var c = document.createElement('li'),
				a = document.createElement('a'),
				breadcrumb = this;
			a.href = '';
			a.innerHTML = crumb.shortTitle;
			a.title = crumb.longTitle;
			a.crumb = crumb;
			a.addEventListener('click', function(event) {
				event.preventDefault();
				if( !event.target.crumb.error && !event.target.crumb.empty) {
					while( breadcrumb.crumbs.pop() !== event.target.crumb) {
					}
					event.target.crumb.self = true;
					breadcrumb.crumbs.push(event.target.crumb);
					breadcrumb.map.compute(event.target.crumb.params);
				}
			}, false);
			//a.addEventListener('dblclick', applet.menuHandler, false);
			c.appendChild(a);
			return c;
		},
		defaultNaming: function() {
			return {'shortTitle': 'Map ' + this.counter, 'longTitle': 'Map ' + this.counter};
		}
	};

	return Breadcrumb;
}());
