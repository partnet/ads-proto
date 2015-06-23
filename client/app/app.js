/*
 * Copyright (c) 2015 Partnet, Inc. Confidential and Proprietary.
 */

'use strict';

/**
 * @ngdoc overview
 * @author brandony-pn
 * @name GSAADSApp
 * @description This is the main module of the GSA ADS demo application. This module defines the application's dependencies,
 * configuration and routing.
 */
(function () {
  var app = angular.module('ADSProtoApp', ['components', 'drugEvents', 'ngRoute', 'ngResource', 'ui.bootstrap']);

  app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'drug-events/drug-events.html',
        controller: 'DrugEventsController',
        controllerAs: 'drugEvents'
      })
      .otherwise({
        redirectTo: '/'
      });
  }]);
})();
