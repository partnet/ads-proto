/*
 * Copyright (c) 2015 Partnet, Inc. Confidential and Proprietary.
 */

/**
 * @ngdoc overview
 * @author brandony-pn
 * @name drug-events:drug-events-table-directive.js
 *
 * @description
 * # adsDEResultsTable
 * A directive for adding drug events to a tabular view.
 */
(function () {
  'use strict';
  angular.module('drugEvents')
    .directive('adsDEResultsTable', [
      function () {
        return {
          restrict: 'E',
          templateUrl: 'drug-events/drug-events-table.html'
        }
      }]);
}());
