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
      };

      init();

      _this.doSearch = function () {
        var query;

        if (angular.isDefined(_this.drugId)) {
          query = {
            api_key: DrugEventsService.apiKey,
          };

          var searchQuery = 'patient.drug.openfda.generic_name:' + _this.drugId + '+AND+patient.patientsex' + _this.gender;

          if (!isNaN(_this.age)) {
            searchQuery += '+AND+patient.patientonsetage:' + _this.age;
          }

          if (!isNaN(_this.weight)) {
            searchQuery += '+AND+patient.patientweight:' + _this.weight;
          }
          query['search'] = searchQuery;
          query['count'] = 'patient.reaction.reactionmeddrapt.exact';
        } else {
          query = 'true'
        }
        DrugEventsService.searchEvents(query).then(function (response) {
          _this.searchResults = response;
        });
      };
    }]);
}());
