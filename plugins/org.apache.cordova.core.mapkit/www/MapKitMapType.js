/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

/**
 * The MapKitMapType interface encapsulates map types.
 */
var MapKitMapType = function(c) {
   this.code = c || null;
};

MapKitMapType.MAP_TYPE_NONE = 0;
MapKitMapType.MAP_TYPE_NORMAL = 1;
MapKitMapType.MAP_TYPE_SATELLITE = 2;
MapKitMapType.MAP_TYPE_TERRAIN = 3;
MapKitMapType.MAP_TYPE_HYBRID = 20;

module.exports = MapKitMapType;


