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
            return 'Recovered';
          case 2:
            return 'Recovering';
          case 3:
            return 'Ongoing';
          case 4:
            return 'Determined to be unrelated';
          case 5:
            return 'Fatal';
          default:
            return 'Unknown';
        }
      };

      /**
       * @ngdoc function
       * @author brandony-pn
       *
       * @description
       * In an attempt to work with the limitations the openfda rest API, this recursive method was implemented to issue
       * 'reaction outcome' requests sequentially, rather than concurrently. Requests still that fail due to server frequency
       * constraints (429 status) will be re-tried up to 3 times.
       *
       * @param deferred the deferred promise from the reaction query
       * @param outcomeQuery the query object that describes the details of this reaction/outcome request
       * @param index the current reaction result index used to update the calculated object graph which is ultimately
       * used for client display
       */
      var doReactionOutcomeRequest = function (deferred, outcomeQuery, index) {
        var retries = 0;
        var calculateOutcomeQuery = function () {
          var oc = angular.copy(outcomeQuery);
          oc.search += '+AND+patient.reaction.reactionmeddrapt:"' + drugEventsFact.reactionResults[index].term + '"';
          delete oc.limit;
          return oc;
        };

        var issueNext = function () {
          if (++index < drugEventsFact.reactionResults.length) {
            doReactionOutcomeRequest(deferred, outcomeQuery, index);
          } else {
            deferred.resolve();
          }
        };

        var buildUnknownResult = function () {
          return {
            term: 'Unknown',
            count: drugEventsFact.reactionResults[index].count
          };
        };

        var makeRequest = function (thisQuery) {
          DrugEvents.get(thisQuery).$promise.then(function (response) {
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

              if (unknown.length === 1) {
                unknown[0].count += drugEventsFact.reactionResults[index].count - outcomes;
              } else {
                drugEventsFact.reactionResults[index].outcomes.push(buildUnknownResult());
              }
            }

            issueNext();
          }, function (response) {
            switch (response.status) {
              case 404:
                drugEventsFact.reactionResults[index].outcomes = [buildUnknownResult()];
                issueNext();
                break;
              case 429:
                if (retries++ < 3) {
                  makeRequest(thisQuery);
                }
            }
          });
        };

        makeRequest(calculateOutcomeQuery());
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
        return DrugEvents.get(query).$promise.then(function (response) {
          drugEventsFact.reactionResults = response.results;
        });
      };

      drugEventsFact.calculateReactionOutcomes = function (outcomeQuery) {
        var deferred = $q.defer();

        doReactionOutcomeRequest(deferred, outcomeQuery, 0);

        return deferred.promise;
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
