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
      };

      var calculateSVGJson = function (searchResults) {
        var svgObject = {
          'name': _this.drugId,
          'children': searchResults
        };

        var svgString = JSON.stringify(svgObject);
        var termRegEx = new RegExp('"reactionmeddrapt"', 'g');
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
        resetSearch();
        
        
        var drugQuery  = {
    	        drug: _this.drugId.toLowerCase()
        };
        
        var reactionQuery = {
    	        drug: _this.drugId.toLowerCase(),
    	        "patient.sex": _this.gender,
    	        "patient.age.low": _this.age - 10 >= 0 ? _this.age - 10 : 0,
    	        "patient.age.high": _this.age + 10 <= 120 ? _this.age + 10 : 120,
    	        "patient.weight.low": _this.weight - 10 >= 0 ? _this.weight - 10 : 0, 
    	        "patient.weight.high": _this.weight + 10
          };
        
     

        DrugEventsService.searchEvents(reactionQuery).then(function () {
          reactionQuery.count = 'patient.reaction.reactionoutcome';
          DrugEventsService.searchIndications(drugQuery).then(function () {
            _this.searchResults = DrugEventsService.reactionResults;
            _this.indicationResults = DrugEventsService.indicationResults;
            _this.svgResult = calculateSVGJson(_this.searchResults);
          });
        }, function (response) {
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
