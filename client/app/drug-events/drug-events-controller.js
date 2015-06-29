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
        _this.indicationResults = undefined;
        _this.searchAlerts = [];
        _this.svgResult = null;
        _this.outcomeExpanded = null;
        _this.numReports = undefined;
        _this.aveDuration = undefined;
        _this.minDuration = undefined;
        _this.maxDuration = undefined;
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
          case 1:
          case 4:
            return 'success';
          case 2:
            return 'info';
          case 3:
            return 'warning';
          case 5:
            return 'danger';
          default:
            return '';
        }
      };

      _this.getOutcomeName = function (term) {
        return DrugEventsService.convertOutcomeTerm(term);
      };

      _this.searchDrugs = function (val) {
        var query = {term: val.toLowerCase()};
        return DrugEventsService.searchDrugs(query);
      };

      _this.doSearch = function () {
        _this.runningQuery = true;
        resetSearch();


        var drugQuery = {
          drug: _this.drugId.toLowerCase()
        };

        var reactionQuery = {
          drug: _this.drugId.toLowerCase(),
          'patient.sex': _this.gender,
          'patient.age.low': _this.age - 10 >= 0 ? _this.age - 10 : 0,
          'patient.age.high': _this.age + 10 <= 120 ? _this.age + 10 : 120,
          'patient.weight.low': _this.weight - 10 >= 0 ? _this.weight - 10 : 0,
          'patient.weight.high': _this.weight + 10
        };


        DrugEventsService.searchEvents(reactionQuery).then(function () {
          reactionQuery.count = 'patient.reaction.reactionoutcome';
          DrugEventsService.searchIndications(drugQuery).then(function () {
            _this.searchResults = DrugEventsService.reactionResults;
            _this.indicationResults = DrugEventsService.indicationResults;
            _this.numReports = DrugEventsService.numReports;
            _this.aveDuration = DrugEventsService.aveDuration;
            _this.minDuration = DrugEventsService.minDuration;
            _this.maxDuration = DrugEventsService.maxDuration;
            _this.svgResult = DrugEventsService.calculateSVGJson(_this.searchResults, _this.drugId);

            var sbr = angular.copy(_this.svgResult);
            var zeroPrrs = sbr.children.filter(function (events) {
              return events.prr === 0;
            });
            Object.keys(sbr.children).map(function (key) {
              delete sbr.children[key].children;
              if(zeroPrrs.length <= 0) {
                sbr.children.size = sbr.children.prr;
              }
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
    }]);
}());
