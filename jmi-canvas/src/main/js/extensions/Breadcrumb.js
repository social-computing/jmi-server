/*global define, JMI */
JMI.namespace("extensions.Breadcrumb");

JMI.extensions.Breadcrumb = ( function() {

	var Breadcrumb = function(parent,map,namingFunc) {
		this.crumbs = [];
		this.counter = 0;
		this.namingFunc = namingFunc;
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
				var crumb = {};
				crumb.params = {};
				for (p in event.params) {
					crumb.params[p] = event.params[p];
				}
				breadcrumb.crumbs.push( crumb);
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
				if( breadcrumb.namingFunc) {
					var res = breadcrumb.namingFunc(event);
					if( res.shortTitle) {
						crumb.shortTitle = res.shortTitle;
					}
					if( res.longTitle) {
						crumb.longTitle = res.longTitle;
					}
				}
				if(!crumb.shortTitle) {
					crumb.shortTitle = 'Map ' + breadcrumb.counter;
				}
				if(!crumb.longTitle) {
					crumb.longTitle = crumb.shortTitle;
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
		getCrumb: function(crumb) {
			var c = document.createElement('li'),
				a = document.createElement('a'),
				breadcrumb = this;
			a.href = '';
			a.innerHTML = crumb.shortTitle;
			a.title = crumb.longTitle;
			a.addEventListener('click', function(event) {
				event.preventDefault();
				if( !crumb.error && !crumb.empty) {
					while( breadcrumb.crumbs.pop() !== crumb) {
					}
					breadcrumb.map.compute(crumb.params);
				}
			}, false);
			//a.addEventListener('dblclick', applet.menuHandler, false);
			c.appendChild(a);
			return c;
		}
	};

	return Breadcrumb;
}());
