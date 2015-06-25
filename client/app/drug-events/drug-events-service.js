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
      var restServerURI = 'https://api.fda.gov/drug/event.json';

      var convertOutcomeTerm = function (term) {
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

      var doReactionOutcomeRequest = function (deferred, outcomeQuery, index) {
        var thisQuery = angular.copy(outcomeQuery);

        thisQuery.search += '+AND+patient.reaction.reactionmeddrapt:"' + drugEventsFact.reactionResults[index].term + '"';
        delete thisQuery.limit;

        DrugEvents.get(angular.copy(thisQuery)).$promise.then(function (response) {
          drugEventsFact.reactionResults[index].outcomes = response.results;

          var outcomes = 0;
          Object.keys(response.results).map(function (key) {
            outcomes += response.results[key].count;
            response.results[key].term = convertOutcomeTerm(response.results[key].term);
          });

          if (outcomes < drugEventsFact.reactionResults[index].count) {
            var unknown = drugEventsFact.reactionResults[index].outcomes.filter(function (value) {
              return value.term.toUpperCase() === 'UNKNOWN';
            });

            if (unknown.length == 1) {
              unknown[0].count += drugEventsFact.reactionResults[index].count - outcomes;
            } else {
              drugEventsFact.reactionResults[index].outcomes.push({
                term: 'Unknown',
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
                term: 'Unknown',
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

      var DrugEvents = $resource(restServerURI, {},
        {
          get: {
            paramSerializer: serializer
          }
        });

      var drugEventsFact = {
        apiKey: '49N1YfEbCwRbQFIgCsSYSIohxMWkMryPpvSgRXbd',
        searchableDrugIds: Object.freeze([
          'Advair Diskus',
          'Advil',
          'Aleve',
          'Cepacol',
          'Childrens Dimetapp',
          'Claritin',
          'Colace',
          'Cortaid',
          'Crestor',
          'Cymbalta',
          'Diovan',
          'Dulcolax',
          'Excedrin',
          'Gaviscon',
          'Lantus Solostar',
          'Lyrica',
          'Nexium',
          'Synthroid',
          'Ventolin HFA',
          'Vyvanse'
        ])
      };

      drugEventsFact.searchEvents = function (query) {
        console.log('Query: ' + JSON.stringify(query));
        return DrugEvents.get(query).$promise.then(function (response) {
          drugEventsFact.reactionResults = response.results;
        });
      };

      drugEventsFact.calculateReactionOutcomes = function (outcomeQuery) {
        var index = 0;
        var deferred = $q.defer();

        doReactionOutcomeRequest(deferred, outcomeQuery, index);

        return deferred.promise;
      };

      drugEventsFact.calculateSVGJson = function (searchResults, drugName) {
        var svgObject = {
          'name': drugName,
          'children': searchResults
        };

        Object.keys(svgObject.children).map(function (key) {
          delete svgObject.children[key].count;
        });

        var termRegEx = new RegExp('"term"', 'g');
        var countRegEx = new RegExp('"count"', 'g');
        var outcomesRegEx = new RegExp('"outcomes"', 'g');

        var svgString = JSON.stringify(svgObject).replace(termRegEx, '"name"').replace(countRegEx,
          '"size"').replace(outcomesRegEx, '"children"');

        console.log('SVG Result: ' + svgString);

        return JSON.parse(svgString);
      };

      return drugEventsFact;
    }]);
}());
