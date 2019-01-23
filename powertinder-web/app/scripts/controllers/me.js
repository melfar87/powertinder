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
            });
        }

        $scope.updatePosition = function () {
            console.log('update position : ' + $scope.pos.lat + " - " + $scope.pos.lon);

            var Position = $resource(baseUrl + '/user/position');
            var pos = new Position();
            pos.lat = $scope.pos.lat;
            pos.lon = $scope.pos.lon;
            pos.$save(function success() {
                console.log('success');
                init($resource, baseUrl, $scope);
            });
        }

    }]);
