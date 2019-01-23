'use strict';

/**
 * @ngdoc function
 * @name webtinFrontApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the webtinFrontApp
 */
angular.module('webtinFrontApp')
  .controller('MainCtrl', ['$resource', '$scope', 'baseUrl', function ($resource, $scope, baseUrl) {

    init($resource, baseUrl, $scope);

    // refresh
    $scope.refresh = function () {
      init($resource, baseUrl, $scope);
    }

    function init($resource, baseUrl, $scope) {
      var Recommendations = $resource(baseUrl + '/user/recs');
      Recommendations.get(function (response) {
        console.log(response);
        $scope.recs = response.results;
      });
    }

    // like
    $scope.like = function (id) {
      console.log("like " + id);

      var Like = $resource(baseUrl + '/user/like/:userId');

      Like.get({ userId: id }, function (response) {
        console.log(response);

        var found = $scope.recs.find(function (element) {
          return element._id === id;
        });

        if (found != undefined && response.match) {
          found.match = true;
        } else {
          found.match = false;
        }
      })
    }

    // likeAll
    $scope.likeAll = function() {
      console.log('like all');
      likeAll();
    }

    async function likeAll() {
      for (const rec of $scope.recs) {
        $scope.like(rec._id);
        await sleep(500);
      }
    }

    function sleep (time) {
      return new Promise((resolve) => setTimeout(resolve, time));
    }

    // super like
    $scope.superLike = function (id) {
      console.log('super like : ' + id);
      var SuperLike = $resource(baseUrl + '/user/superlike/:userId');

      SuperLike.get({ userId: id }, function (response) {
        console.log(response);

        var found = $scope.recs.find(function (element) {
          return element._id === id;
        });

        if (found != undefined && response.match) {
          found.match = true;
        } else {
          found.match = false;
        }
      })

    }

    // pass
    $scope.pass = function (id) {
      console.log("pass " + id);

      var Pass = $resource(baseUrl + '/user/pass/:userId');
      Pass.get({ userId: id }, function (response) {
        console.log(response);

        var found = $scope.recs.find(function (element) {
          return element._id === id;
        });

        if (found != undefined) {
          found.match = false;
        }
      })
    }

  }]);