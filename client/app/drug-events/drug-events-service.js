/*
 * Copyright (c) 2015 Partnet, Inc. Confidential and Proprietary.
 */

/**
 * @ngdoc overview
 * @author brandony-pn
 * @name drug-events:drug-events-service.js
 *
 * @description
 * # DrugEventsService
 * The ADSProtoApp service that handles user request data and provides FDA adverse events information.
 */
(function () {
  'use strict';
  angular.module('drugEvents')
    .factory('DrugEventsService', ['$q', '$resource', function ($q, $resource) {
      var restServerURI = '/rest-api/faers/drugs/:drug';
      var Drugs = $resource(restServerURI);
      var DrugEvents = $resource(restServerURI + '/reactions', {drug: '@drug'});
      var Indications = $resource(restServerURI, {drug: '@drug'});

      var drugEventsFact = {
        apiKey: '49N1YfEbCwRbQFIgCsSYSIohxMWkMryPpvSgRXbd',
        searchDrugs: searchDrugs
      };

      function searchDrugs(val) {
        return Drugs.query(val).$promise.then(function (response) {
          return response;
        });
      }

      drugEventsFact.searchEvents = function (query) {
        return DrugEvents.get(query).$promise.then(function (response) {
          drugEventsFact.reactionResults = response.reactions;
          drugEventsFact.numReports = response.numReports;
        });
      };

      drugEventsFact.searchIndications = function (query) {
        return Indications.get(query).$promise.then(function (response) {
          drugEventsFact.indicationResults = response.indicationCounts;
          drugEventsFact.aveDuration = response.averageTreatmentDuration;
          drugEventsFact.minDuration = response.minTreatmentDuration;
          drugEventsFact.maxDuration = response.maxTreatmentDuration;
        });
      };

      drugEventsFact.convertOutcomeTerm = function (term) {//jshint ignore:line
        switch (term) {
          case 1:
            return 'Recovered/resolved';
          case 2:
            return 'Recovering/resolving';
          case 3:
            return 'Not recovered/not resolved';
          case 4:
            return 'Determined an unrelated reaction to this event';
          case 5:
            return 'Fatal';
          default:
            return 'Unknown';
        }
      };

      drugEventsFact.calculateSVGJson = function (searchResults, drugName) {
        var svgObject = {
          'name': drugName,
          'children': searchResults
        };

        var termRegEx = new RegExp('"term"', 'g');
        var countRegEx = new RegExp('"count"', 'g');
        var outcomesRegEx = new RegExp('"outcomes"', 'g');

        var svgString = JSON.stringify(svgObject).replace(termRegEx, '"name"').replace(countRegEx,
          '"size"').replace(outcomesRegEx, '"children"');

        return JSON.parse(svgString);
      };

      return drugEventsFact;
    }]);
}());
