/*
 * Copyright (c) 2015 Partnet, Inc. Confidential and Proprietary.
 */

/**
 * @ngdoc overview
 * @author brandony-pn
 * @name drug-events:drug-events-controller.js
 *
 * @description
 * # DrugEventsController
 * The controller of the ADSProtoApp that manages the initial state and behavior of the drug-events view.
 */

(function () {
  'use strict';
  angular.module('drugEvents')
    .controller('DrugEventsController', ['DrugEventsService', function (DrugEventsService) {
      var _this = this;

      var resetSearch = function () {
        _this.searchResults = null;
        _this.searchAlerts = [];
        _this.svgResult = null;
      };

      var calculateSVGJson = function (searchResults) {
        var svgObject = {
          'name': _this.drugId,
          'children': searchResults
        };

        var svgString = JSON.stringify(svgObject);
        var termRegEx = new RegExp('"term"', 'g');
        svgString = svgString.replace(termRegEx, '"name"');
        var countRegEx = new RegExp('"count"', 'g');
        svgString = svgString.replace(countRegEx, '"size"');
        var outcomesRegEx = new RegExp('"outcomes"', 'g');
        svgString = svgString.replace(outcomesRegEx, '"children"');

        return JSON.parse(svgString);
      };

      var init = function () {
        _this.availableDrugIds = DrugEventsService.searchableDrugIds;
        _this.gender = 1;
        resetSearch();
      };

      init();

      _this.doSearch = function () {
        _this.runningQuery = true;
        resetSearch();

        var query = {
          api_key: DrugEventsService.apiKey
        };

        var searchParam = 'patient.drug.openfda.brand_name:' + _this.drugId + '+AND+patient.patientsex:' + _this.gender;

        if (!isNaN(_this.age)) {
          searchParam += '+AND+patient.patientonsetage:[' + (_this.age - 10 >= 0 ? _this.age - 10 : 0) + '+TO+' + (_this.age + 10 <= 120 ? _this.age + 10 : 120) + ']';
        }

        if (!isNaN(_this.weight)) {
          searchParam += '+AND+patient.patientweight:[' + (_this.weight - 10 >= 0 ? _this.weight - 10 : 0) + '+TO+' + (_this.weight + 10) + ']';
        }

        query.search = searchParam;
        query.count = 'patient.reaction.reactionmeddrapt.exact';
        query.limit = '20';


        DrugEventsService.searchEvents(query).then(function () {
          query.count = 'patient.reaction.reactionoutcome';
          DrugEventsService.calculateReactionOutcomes(query).then(function () {
            _this.searchResults = DrugEventsService.reactionResults;
            _this.svgResult = calculateSVGJson(_this.searchResults);
            _this.runningQuery = false;
          });
        }, function (response) {
          _this.runningQuery = false;
          switch (response.status) {
            case 404:
              _this.searchAlerts.splice(0, 1, {
                type: 'danger',
                msg: 'No results were found for the search criteria submitted.'
              });
          }
        });
      };
    }]);
}());
