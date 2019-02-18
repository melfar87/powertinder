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

    $scope.sortCriteria = 'person.age';
    $scope.sortOrder = false;
    $scope.matches = [];

    init($resource, baseUrl, $scope);

    function init($resource, baseUrl, $scope) {
      $scope.loading = true;
      var Matches = $resource(baseUrl + '/user/matches');
      Matches.get(function (response) {
        $scope.loading = false;
        console.log(response);
        $scope.matches = response.matches;
      });
    }

    // refresh
    $scope.refresh = function () {
      init($resource, baseUrl, $scope);
    }

    $scope.sort = function (criteria) {
      $scope.sortCriteria = criteria;
      $scope.sortOrder = !$scope.sortOrder;
    }

    $scope.locate = function (id) {
      var Locate = $resource(baseUrl + '/user/locate/:userId');
      
      var found = $scope.matches.find(function (element) {
        debugger
        if (element.person._id === id) {
          element.person.searchingUser = true;

          Locate.get({ userId: id }, function (response) {
            console.log(response);
            element.person.mapsUrl = response.mapsUrl;  
            element.person.searchingUser = false;  
          });
        }
      });


    }

  }]);



