
var exec = require('cordova/exec');

function MapKit() {
	this.iconColors = {
		HUE_RED: 0.0,
		HUE_ORANGE: 30.0,
		HUE_YELLOW: 60.0,
		HUE_GREEN: 120.0,
		HUE_CYAN: 180.0,
		HUE_AZURE: 210.0,
		HUE_BLUE: 240.0,
		HUE_VIOLET: 270.0,
		HUE_MAGENTA: 300.0,
		HUE_ROSE: 330.0
	};
}


MapKit.prototype = {
	showMap: function(options, success, error) {
		exec(success, error, 'MapKit', 'showMap', [options]);
	},

	addMapPins: function(pins, success, error) {
		exec(success, error, 'MapKit', 'addMapPins', [pins]);
	},

	clearMapPins: function(success, error) {
		exec(success, error, 'MapKit', 'clearMapPins', []);
	},

	hideMap: function(success, error) {
		exec(success, error, 'MapKit', 'hideMap', []);
	},

	changeMapType: function(mapType, success, error) {
		exec(success, error, 'MapKit', 'changeMapType', [mapType ? { "mapType": mapType } :{ "mapType": 0 }]);
	}

}

module.exports = new MapKit();
