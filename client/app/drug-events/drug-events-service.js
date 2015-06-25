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

      var DrugEvents = $resource(restServerURI+'/reactions', {drug: '@drug'});

      var Indications = $resource(restServerURI, {drug: '@drug'});

      var drugEventsFact = {
        apiKey: '49N1YfEbCwRbQFIgCsSYSIohxMWkMryPpvSgRXbd',
        searchableDrugIds: Object.freeze([
          'Abilify',                                
          'Advil',
          'Aleve',
          'Celebrex',
          'Claritin',
          'Colace',
          'Crestor',
          'Cymbalta',
          'Diovan',
          'Dulcolax',
          'Excedrin',
          'Gaviscon',
          'Lantus',
          'Lotrimin',
          'Lyrica',
          'Maalox Antacid',
          'Midol',
          'Nexium',
          'Synthroid',
          'Vyvanse'
        ])
      };

      drugEventsFact.searchEvents = function (query) {
        console.log('Query: ' + JSON.stringify(query));
        return DrugEvents.get(query).$promise.then(function (response) {
          drugEventsFact.reactionResults = response.reactions;
          drugEventsFact.numReports =response.numReports;
        });
      };

      drugEventsFact.searchIndications = function (query) {
          console.log('Query: ' + JSON.stringify(query));
          return Indications.get(query).$promise.then(function (response) {
            drugEventsFact.indicationResults = response.indicationCounts;
            drugEventsFact.aveDuration = response.averageTreatmentDuration;
            drugEventsFact.minDuration = response.minTreatmentDuration;
            drugEventsFact.maxDuration = response.maxTreatmentDuration;
          });
       };

      return drugEventsFact;
    }]);
}());
