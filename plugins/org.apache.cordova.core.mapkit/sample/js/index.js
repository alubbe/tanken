/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicity call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.receivedEvent('deviceready');
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);

    },
    showMap: function() {
        var pins = [
            {
                lat: -22.999658,
                lon: -43.353827,
                title: "A Cool Title",
                snippet: "A Really Cool Snippet",
                image: {
                    type: "asset",
                    resource: "www/img/logo.png",
  				  width: 50,
  				  height: 50
                },
                icon: MapKit.iconColors.HUE_ROSE
            },
            {
                lat: -22.999780,
                lon: -43.348087,
                title: "A Cool Title, with no Snippet",
                icon: {
                  type: "asset",
                  resource: "www/img/logo.png", //an image in the asset directory
                  pinColor: MapKit.iconColors.HUE_VIOLET //iOS only
                }
            },
            {
                lat: -22.998180,
                lon: -43.359003,
                title: "Awesome Title",
                snippet: "Awesome Snippet",
                icon: MapKit.iconColors.HUE_GREEN
            }
        ];
        var error = function() {
          console.log('error');
        };
        var success = function() {
          document.getElementById('hide_map').style.display = 'block';
          document.getElementById('show_map').style.display = 'none';
          MapKit.addMapPins(pins, function() { 
                                      console.log('adMapPins success');  
                                      document.getElementById('clear_map_pins').style.display = 'block';
                                  },
                                  function() { console.log('error'); });
        };
		
		var options = {
			height: 460,
			diameter: 1000,
			atBottom: true,
			lat: -22.999521,
			lon: -43.344600
		};
		
        MapKit.showMap(options, success, error);
    },
    hideMap: function() {
        var success = function() {
          document.getElementById('hide_map').style.display = 'none';
          document.getElementById('clear_map_pins').style.display = 'none';
          document.getElementById('show_map').style.display = 'block';
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
};
