'use strict';

describe('drug-events-controller', function () {
  beforeEach(module('drugEvents'));

  var MockDrugEventsService, ctrl, $controller, $q, $rootScope;

  beforeEach(inject(function(_$controller_, _$q_, _$rootScope_) {
    $controller = _$controller_;
    $q = _$q_;
    $rootScope = _$rootScope_;
  }));

  beforeEach(function () {
    ctrl = $controller('DrugEventsController',
      {
        DrugEventsService: MockDrugEventsService
      });
  });

  MockDrugEventsService = {
    searchEvents: function () {
      var deferred = $q.defer();
      deferred.resolve();
      return deferred.promise;
    },
    calculateReactionOutcomes: function () {
      var deferred = $q.defer();
      deferred.resolve();
      return deferred.promise;
    },
    reactionResults: [{term: 'DISEASE', count: 9000}]
  };

  it('searches using drugId and gender', function () {
    spyOn(MockDrugEventsService, 'calculateReactionOutcomes').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 2;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.calculateReactionOutcomes).toHaveBeenCalledWith(
      jasmine.objectContaining({
        search: 'patient.drug.openfda.brand_name:Synthroid+AND+patient.patientsex:2'
      }));
  });

  it('adds age range to search if it is set', function () {
    spyOn(MockDrugEventsService, 'calculateReactionOutcomes').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.age = 70;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.calculateReactionOutcomes).toHaveBeenCalledWith(
      jasmine.objectContaining({
        search: 'patient.drug.openfda.brand_name:Synthroid+AND+patient.patientsex:1+AND+patient.patientonsetage:[60+TO+80]'
      }));
  });

  it('does not allow the age range to go below 0', function () {
    spyOn(MockDrugEventsService, 'calculateReactionOutcomes').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.age = 5;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.calculateReactionOutcomes).toHaveBeenCalledWith(
      jasmine.objectContaining({
        search: 'patient.drug.openfda.brand_name:Synthroid+AND+patient.patientsex:1+AND+patient.patientonsetage:[0+TO+15]'
      }));
  });

  it('does not allow the age range to go above 120', function () {
    spyOn(MockDrugEventsService, 'calculateReactionOutcomes').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.age = 115;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.calculateReactionOutcomes).toHaveBeenCalledWith(
      jasmine.objectContaining({
        search: 'patient.drug.openfda.brand_name:Synthroid+AND+patient.patientsex:1+AND+patient.patientonsetage:[105+TO+120]'
      }));
  });

  it('adds weight range to search if it is set', function () {
    spyOn(MockDrugEventsService, 'calculateReactionOutcomes').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.weight = 180;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.calculateReactionOutcomes).toHaveBeenCalledWith(
      jasmine.objectContaining({
        search: 'patient.drug.openfda.brand_name:Synthroid+AND+patient.patientsex:1+AND+patient.patientweight:[170+TO+190]'
      }));
  });

  it('does not allow the weight range to go below 0', function () {
    spyOn(MockDrugEventsService, 'calculateReactionOutcomes').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.weight = 5;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.calculateReactionOutcomes).toHaveBeenCalledWith(
      jasmine.objectContaining({
        search: 'patient.drug.openfda.brand_name:Synthroid+AND+patient.patientsex:1+AND+patient.patientweight:[0+TO+15]'
      }));
  });

  it('adds an alert if the search responds with a 404', function () {
    MockDrugEventsService.searchEvents = function () {
      var deferred = $q.defer();
      deferred.reject({status: 404});
      return deferred.promise;
    };
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchAlerts[0].type).toBe('danger');
  });
});
