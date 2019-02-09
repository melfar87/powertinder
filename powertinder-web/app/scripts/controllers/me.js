'use strict';

/**
 * @ngdoc function
 * @name webtinFrontApp.controller:MeCtrl
 * @description
 * # MeCtrl
 * Controller of the webtinFrontApp
 */
angular.module('webtinFrontApp')
    .controller('MeCtrl', ['$resource', '$scope', 'baseUrl', function ($resource, $scope, baseUrl) {

        init($resource, baseUrl, $scope);

        function init($resource, baseUrl, $scope) {
            var Me = $resource(baseUrl + '/user/me');
            Me.get(function (response) {
                console.log(response);
                $scope.me = response;
                $scope.center.lat = $scope.me.pos.lat;
                $scope.center.lng = $scope.me.pos.lon;
                $scope.markers.mapMarker.lat = $scope.me.pos.lat;
                $scope.markers.mapMarker.lng = $scope.me.pos.lon;
            });
        }

        $scope.updatePosition = function () {
            console.log('update position : ' + $scope.markers.mapMarker.lat + " - " + $scope.markers.mapMarker.lng);

            var Position = $resource(baseUrl + '/user/position');
            var pos = new Position();
            pos.lat = $scope.markers.mapMarker.lat;
            pos.lon = $scope.markers.mapMarker.lng;
            pos.$save(function success() {
                console.log('success');
                init($resource, baseUrl, $scope);
            });
        }
        angular.extend($scope, {
                center: {
                    lat: 1,
                    lng: 1,
                    zoom: 12
                },
                markers: {
                    mapMarker: {
                        lat: 40.095,
                        lng: -3.823,
                        focus: true,
                        draggable: true
                    }
                },
                events: {
                    map: {
                        enable: ['dragend'],
                        logic: 'emit'
                    }
                }
        });

        $scope.$on('leafletDirectiveMarker.dragend', function(event){
            var draggedLat=event.targetScope.markers.mapMarker.lat;
            var draggedLng=event.targetScope.markers.mapMarker.lng;

            console.log(draggedLat,draggedLng); //here am getting 12 ,80  every time
            console.log(event); // Here i can see new coordinates
            console.log(event.targetScope.markers.mapMarker.lat,event.targetScope.markers.mapMarker.lng);
           });
    }]);
