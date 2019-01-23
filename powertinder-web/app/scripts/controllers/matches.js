'use strict';

/**
 * @ngdoc function
 * @name webtinFrontApp.controller:MatchesCtrl
 * @description
 * # MatchesCtrl
 * Controller of the webtinFrontApp
 */
angular.module('webtinFrontApp')
  .controller('MatchesCtrl', ['$resource', '$scope', 'baseUrl', function ($resource, $scope, baseUrl) {

    init($resource, baseUrl, $scope);

    function init($resource, baseUrl, $scope) {
      var Matches = $resource(baseUrl + '/user/matches');
      Matches.get(function (response) {
        console.log(response);
        $scope.matches = response.matches;
      });
    }

    // refresh
    $scope.refresh = function () {
      init($resource, baseUrl, $scope);
    }

  }]);



