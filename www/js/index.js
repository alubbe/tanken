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
        //add a 20px margin to the top of iOS7+ apps
        if (window.device) {
            if (window.device.version) {
                if (parseFloat(window.device.version) >= 7) {
                    $("#top-area").css("top", 20);
                }        
            }
        }

        app.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        console.log('Received Event: ' + id);
        app.showMap();
    },

    showMap: function() {
        var error = function() {
          console.log('error');
        };
        var success = function() {
          app.addGasStations(3, 5, 52.512303, 13.431191);
        };
        
        var options = {
            marginTop: $("#top-area").height() + parseInt($("#top-area").css("top")),
            marginBottom: $("#bottom-area").height(),
            lat: 52.512303,
            lng: 13.431191
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

    changeMapType: function() {
      var success = function() {
          console.log('Map Type Changed');
        };
        var error = function() {
          console.log('error');
        };
        MapKit.changeMapType(MapKitMapType.MAP_TYPE_SATELLITE, success, error);
    },

    addGasStations: function(fuel_id, radius, lat, lon) {
        var url = "http://mehr-tanken.de/list?result=xml&device=android&fuel_id=" + fuel_id + "&radius=" + radius + "&lon=" + lon + "&lat=" + lat + "&plz=-1";

        $.ajax({
            type: "GET",
            url: url,
            dataType: "xml",
            success: function(xml) {
                console.log("Ajax successful");
                $(xml).find('station').each(function(){
                    var id = $(this).attr('id'), //this is a string because int breaks on android given a certain size
                        price = parseFloat($(this).find('fuel').attr('price_current')),
                        lat = parseFloat($(this).find('coordinates').attr('lat')),
                        lng = parseFloat($(this).find('coordinates').attr('lon'));
                    app.addMapPin(id, price, lat, lng);
                });
            },
            error: function(xhr, type){
                alert('Ajax error!');
                console.log(xhr);
                console.log(type);
                // should run it again.
            }
        });

    },

    addMapPin: function(id, price, lat, lng) {
        var pin = {
            id: id,
            lat: lat,
            lng: lng,
            title: 'lol',
            icon: MapKit.iconColors.HUE_ROSE,
            showInfoWindow: false,
            markerCallback: "app.markerCallback"
        };
        MapKit.addMapPins([pin], function(){console.log("pin added!")}, function(){console.log("pin failed");});
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

    markerCallback: function(marker_id) {
        alert("Marker " + marker_id + " pressed!");
    }

};
