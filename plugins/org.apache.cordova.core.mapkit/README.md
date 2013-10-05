MapKit plugin for iOS and Android
=================================

Uses *Apple Maps* on iOS and *Google Maps v2* on Android

Currently only works/tested on Android and iOS. Requires Cordova 3.0+ (will not work on earlier versions without modifications).

![Cordova Map 1](http://i.imgur.com/Mf6oeXal.png)

![Cordova Map 2](http://i.imgur.com/XaaBGeGl.png)

![Cordova Map 3](http://i.imgur.com/3IoDj0Rl.png)

![Cordova Map 4](http://i.imgur.com/Bfzik6Ml.png)


Android specific
----------------

You need a [Google Maps Android v2 API KEY](https://code.google.com/apis/console/) from google and you need to specify it when you install the plugin

You can install this plugin:

With [plugman](https://npmjs.org/package/plugman)

    plugman install --platform android --project android-mapkit-example/ --plugin /path/to/MapKit --variable API_KEY="YOUR_API_KEY_FROM_GOOGLE"

Or with	Cordova Cli (tested on version 3.0.9)
	cordova plugin add https://github.com/raphaelquati/MapKit.git --variable API_KEY="YOUR_API_KEY_FROM_GOOGLE"

Sample code
-----------

    var app = {
        showMap: function() {
            var pins = [[49.28115, -123.10450], [49.27503, -123.12138], [49.28286, -123.11891]];
            var error = function() {
              console.log('error');
            };
            var success = function() {
              MapKit.addMapPins(pins, function() { 
                                          console.log('adMapPins success');  
                                      },
                                      function() { console.log('error'); });
            };
			
			var options = {
				height: 460, 		// height of the map (width is always full size for now)
				diameter: 1000,		// unused for now
				atBottom: true,		// bottom or top of the webview
				lat: -22.999521,	// initial camera position latitude
				lon: -43.344600		// initial camera position longitude
			};

			MapKit.showMap(options, success, error);			
        },
        hideMap: function() {
            var success = function() {
              console.log('Map hidden');
            };
            var error = function() {
              console.log('error');
            };
            MapKit.hideMap(success, error);
        },
        clearMapPins: function() {
            var success = function() {
              console.log('Map Pins cleared!');
            };
            var error = function() {
              console.log('error');
            };
            MapKit.clearMapPins(success, error);
        },
        changeMapType: function() {
            var success = function() {
              console.log('Map Type Changed');
            };
            var error = function() {
              console.log('error');
            };
			MapKit.changeMapType(MapKitMapType.MAP_TYPE_SATELLITE, success, error);
        }
    }

Configuration
-------------

Create an options object, and pass to showMap:

	var options = {
		height: 460, 		// height of the map (width is always full size for now)
		diameter: 1000,		// unused for now
		atBottom: true,		// bottom or top of the webview
		lat: -22.999521,	// initial camera position latitude
		lon: -43.344600		// initial camera position longitude
	};

	MapKit.showMap(options, success, error);


Sample App
----------

Checkout the sample/ application as a boilerplate!

Missing features
----------------

Info bubbles: Info bubbles supported with title, snippet, custom icon point and an image. Custom info bubbles not supported yet (i.e HTML bubbles etc..).

License
-------

Apache
