'use strict';

/**
 * @ngdoc function
 * @name webtinFrontApp.controller:NavCtrl
 * @description
 * # NavCtrl
 * Navigation Controller of the powertinder webapp
 */
angular.module('webtinFrontApp')
  .controller('navCtrl', ['$location', '$scope', function ($location, $scope) {

    $scope.isActive = function (destination) {
      return destination === $location.path();
    }
  }]);



