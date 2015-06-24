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
    searchIndications: function () {
      var deferred = $q.defer();
      deferred.resolve();
      return deferred.promise;
    },
    reactionResults: [{term: 'DISEASE', count: 9000}]
  };

  it('searches using drugId and gender', function () {
    spyOn(MockDrugEventsService, 'searchEvents').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 2;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.searchEvents).toHaveBeenCalledWith(
      jasmine.objectContaining({
        drug: 'synthroid',
        'patient.sex': 2
      }));
  });

  it('adds age range to search if it is set', function () {
    spyOn(MockDrugEventsService, 'searchEvents').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.age = 70;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.searchEvents).toHaveBeenCalledWith(
      jasmine.objectContaining({
        drug: 'synthroid',
        'patient.age.low': 60,
        'patient.age.high': 80
      }));
  });

  it('does not allow the age range to go below 0', function () {
    spyOn(MockDrugEventsService, 'searchEvents').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.age = 5;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.searchEvents).toHaveBeenCalledWith(
      jasmine.objectContaining({
        drug: 'synthroid',
        'patient.age.low': 0,
        'patient.age.high': 15
      }));
  });

  it('does not allow the age range to go above 120', function () {
    spyOn(MockDrugEventsService, 'searchEvents').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.age = 115;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.searchEvents).toHaveBeenCalledWith(
      jasmine.objectContaining({
        drug: 'synthroid',
        'patient.age.low': 105,
        'patient.age.high': 120
      }));
  });

  it('adds weight range to search if it is set', function () {
    spyOn(MockDrugEventsService, 'searchEvents').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.weight = 180;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.searchEvents).toHaveBeenCalledWith(
      jasmine.objectContaining({
        drug: 'synthroid',
        'patient.weight.low': 170,
        'patient.weight.high': 190
      }));
  });

  it('does not allow the weight range to go below 0', function () {
    spyOn(MockDrugEventsService, 'searchEvents').and.callThrough();
    ctrl.drugId = 'Synthroid';
    ctrl.gender = 1;
    ctrl.weight = 5;
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchResults[0].term).toBe('DISEASE');
    expect(MockDrugEventsService.searchEvents).toHaveBeenCalledWith(
      jasmine.objectContaining({
        drug: 'synthroid',
        'patient.weight.low': 0,
        'patient.weight.high': 15
      }));
  });

  it('adds an alert if the search responds with a 404', function () {
    MockDrugEventsService.searchEvents = function () {
      var deferred = $q.defer();
      deferred.reject({status: 404});
      return deferred.promise;
    };
    ctrl.drugId = 'Synthroid';
    ctrl.doSearch();
    $rootScope.$digest();
    expect(ctrl.searchAlerts[0].type).toBe('danger');
  });
});
