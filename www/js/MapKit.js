if (!navigator.userAgent.match(/(iPhone|iPod|iPad|Android|BlackBerry|IEMobile)/)) {

	var map;
	var js_script = document.createElement('script');
	js_script.type = "text/javascript";
	js_script.src = "http://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=places&callback=app.onDeviceReady";
	js_script.async = false;
	document.getElementsByTagName('head')[0].appendChild(js_script);

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
			var initialGMapslatlng = new google.maps.LatLng(options.lat, options.lon);

			var mapOptions = {
			  zoom: 12,
			  center: initialGMapslatlng,
			  mapTypeId: google.maps.MapTypeId.ROADMAP,
			  panControl: false,
			  }

			map = new google.maps.Map(document.getElementById('map'), mapOptions);

			success();
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
 		},

 		markerCallback: function(marker) {
 	      alert("Marker pressed!");
 	    }
 	}

 	MapKit = new MapKit();
}