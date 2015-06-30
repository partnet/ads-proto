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
        _this.outcomeExpanded = null;
      };

      var init = function () {
        _this.availableDrugIds = DrugEventsService.searchableDrugIds;
        _this.gender = 1;
        resetSearch();
      };

      init();

      _this.expandOutcome = function (rowNum) {
        if (angular.isDefined(_this.outcomeExpanded) && _this.outcomeExpanded === rowNum) {
          _this.outcomeExpanded = null;
        } else {
          _this.outcomeExpanded = rowNum;
        }
      };

      _this.getOutcomeRowClass = function (term) {
        switch (term) {
          case 'Recovered':
          case 'Determined to be unrelated':
            return 'success';
          case 'Recovering':
            return 'info';
          case 'Ongoing':
            return 'warning';
          case 'Fatal':
            return 'danger';
          default:
            return '';
        }
      };

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
          var weightKgs = _this.weight / 2.2046;
          searchParam += '+AND+patient.patientweight:[' + (weightKgs - 10 >= 0 ? weightKgs - 10 : 0) + '+TO+' + (weightKgs + 10) + ']';
        }

        query.search = searchParam;
        query.count = 'patient.reaction.reactionmeddrapt.exact';
        query.limit = '20';


        DrugEventsService.searchEvents(query).then(function () {
          query.count = 'patient.reaction.reactionoutcome';
          DrugEventsService.calculateReactionOutcomes(query).then(function () {
            _this.searchSuccess = true;
            _this.searchResults = DrugEventsService.reactionResults;
            _this.svgResult = DrugEventsService.calculateSVGJson(angular.copy(_this.searchResults), _this.drugId);

            var sbr = angular.copy(_this.svgResult);
            Object.keys(sbr.children).map(function (key) {
              delete sbr.children[key].children;
            });
            _this.svgBubbleResult = sbr;
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

      _this.closeAlert = function () {
        _this.searchAlerts.splice(0, 1);
      };
    }]);
}());
