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

      var doReactionOutcomeRequest = function (deferred, outcomeQuery, index) {
        var thisQuery = angular.copy(outcomeQuery);

        thisQuery.search += '+AND+patient.reaction.reactionmeddrapt:"' + drugEventsFact.reactionResults[index].term + '"';
        delete thisQuery.limit;

        DrugEvents.get(angular.copy(thisQuery)).$promise.then(function (response) {
          drugEventsFact.reactionResults[index].outcomes = response.results;

          var outcomes = 0;
          Object.keys(response.results).map(function (key) {
            outcomes += response.results[key].count;
          });

          if (outcomes < drugEventsFact.reactionResults[index].count) {
            var unknown = drugEventsFact.reactionResults[index].outcomes.filter(function (value) {
              return value.term === 6;
            });

            if (unknown.length === 1) {
              unknown[0].count += drugEventsFact.reactionResults[index].count - outcomes;
            } else {
              drugEventsFact.reactionResults[index].outcomes.push({
                term: 6,
                count: drugEventsFact.reactionResults[index].count
              });
            }
          }
          index++;
          if (index < drugEventsFact.reactionResults.length) {
            doReactionOutcomeRequest(deferred, outcomeQuery, index);
          } else {
            deferred.resolve();
          }
        }, function (response) {
          switch (response.status) {
            case 404:
              drugEventsFact.reactionResults[index].outcomes = [{
                term: 6,
                count: drugEventsFact.reactionResults[index].count
              }];
          }
          index++;
          if (index < drugEventsFact.reactionResults.length) {
            doReactionOutcomeRequest(deferred, outcomeQuery, index);
          } else {
            deferred.resolve();
          }
        });
      };

      var serializer = function (params) {
        var ps = '';
        var keys = Object.keys(params);
        var count = 0;
        keys.map(function (key) {
          ps += key + '=' + params[key];
          if (++count < keys.length) {
            ps += '&';
          }
        });
        return ps;
      };

      var DrugEvents = $resource(restServerURI+"/reactions", {drug: '@drug'});
      
      var Indications = $resource(restServerURI, {drug: '@drug'});

      var drugEventsFact = {
        apiKey: '49N1YfEbCwRbQFIgCsSYSIohxMWkMryPpvSgRXbd',
        searchableDrugIds: Object.freeze([
          'Synthroid',
          'Crestor',
          'Nexium',
          'Ventolin HFA',
          'Advair Diskus',
          'Diovan',
          'Lantus Solostar',
          'Cymbalta',
          'Vyvanse',
          'Lyrica',
          'Spiriva Handihaler',
          'Lantus',
          'Celebrex',
          'Abilify',
          'Januvia',
          'Namenda',
          'Viagra',
          'Cialis'
        ])
      };

      drugEventsFact.searchEvents = function (query) {
        console.log('Query: ' + JSON.stringify(query));
        return DrugEvents.get(query).$promise.then(function (response) {
          drugEventsFact.reactionResults = response.reactions;
        });
      };
      
      drugEventsFact.searchIndications = function (query) {
          console.log('Query: ' + JSON.stringify(query));
          return Indications.get(query).$promise.then(function (response) {
            drugEventsFact.indicationResults = response.indicationCounts;
          });
       };

      return drugEventsFact;
    }]);
}());
