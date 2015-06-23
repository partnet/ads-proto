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

      var init = function () {
        _this.searchAlerts = [];
        _this.availableDrugIds = DrugEventsService.searchableDrugIds;
        _this.gender = 1;
      };

      init();

      _this.doSearch = function () {
        _this.searchAlerts = [];
        var query;

        if (angular.isDefined(_this.drugId)) {
          query = {
            api_key: DrugEventsService.apiKey
          };

          var searchQuery = 'patient.drug.openfda.brand_name:' + _this.drugId + '+AND+patient.patientsex:' + _this.gender;

          if (!isNaN(_this.age)) {
            searchQuery += '+AND+patient.patientonsetage:[' + (_this.age - 10 >= 0 ? _this.age - 10 : 0) + '+TO+' + (_this.age + 10 <= 120 ? _this.age + 10 : 120) + ']';
          }

          if (!isNaN(_this.weight)) {
            searchQuery += '+AND+patient.patientweight:[' + (_this.weight - 10 >= 0 ? _this.weight - 10 : 0) + '+TO+' + (_this.weight + 10) + ']';
          }

          query.search = searchQuery;
          query.count = 'patient.reaction.reactionmeddrapt.exact';
        } else {
          query = 'true';
        }

        DrugEventsService.searchEvents(query).then(function (response) {
          //_this.searchResults = response;
          console.log('RESULT: ' + JSON.stringify(response));
        }, function(response){
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
