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
      $scope.loading = true;
      var Recommendations = $resource(baseUrl + '/user/recs');
      Recommendations.get(function (response) {
        console.log(response);
        $scope.recs = response.results;
        updateRemainingLikes($resource, baseUrl, $scope);
        $scope.loading = false;
      });
    }

    // like
    $scope.like = function (id) {
      console.log("like " + id);

      var Like = $resource(baseUrl + '/user/like/:userId');
      $scope.loading = true;

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

        $scope.remainingLikes = response.likes_remaining;

        $scope.loading = false;
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
      $scope.loading = true;

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

        $scope.loading = false;
        $scope.remainingSuperLikes = response.super_likes.remaining;
        $scope.resetsAt = response.resets_at;
      })
    }

    // pass
    $scope.pass = function (id) {
      console.log("pass " + id);

      $scope.loading = true;

      var Pass = $resource(baseUrl + '/user/pass/:userId');
      Pass.get({ userId: id }, function (response) {
        console.log(response);

        var found = $scope.recs.find(function (element) {
          return element._id === id;
        });

        if (found != undefined) {
          found.match = false;
        }

        $scope.loading = false;

      })
    }

  }]);

function updateRemainingLikes($resource, baseUrl, $scope) {
  var Meta = $resource(baseUrl + '/user/meta');
  Meta.get(function (response) {
    console.log(response);
    $scope.remainingLikes = response.rating.likes_remaining;
    $scope.remainingSuperLikes = response.rating.super_likes.remaining;
    $scope.resetsAt = response.rating.super_likes.resets_at;
  });
}
