'use strict';

/**
 * @ngdoc overview
 * @name webtinFrontApp
 * @description
 * # webtinFrontApp
 *
 * Main module of the application.
 */
angular
  .module('webtinFrontApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .constant('baseUrl', 'http://localhost:8080')
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/matches.html',
        controller: 'MatchesCtrl',
        controllerAs: 'matches'
      })
      .when('/search', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl',
        controllerAs: 'main'
      })
      .when('/me', {
        templateUrl: 'views/me.html',
        controller: 'MeCtrl',
        controllerAs: 'me'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
