'use strict';

describe('drug-events-service', function () {
  beforeEach(module('drugEvents'));

  var DrugEventsService, $httpBackend;

  beforeEach(inject(function(_DrugEventsService_, _$httpBackend_) {
    DrugEventsService = _DrugEventsService_;
    $httpBackend = _$httpBackend_;
  }));

  afterEach(function () {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('serializes parameters without encoding +', function () {
    $httpBackend.expectGET('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.brand_name:Aspirin+AND+patient.patientweight:[180+TO+200]&count=patient.reaction.reactionmeddrapt.exact&limit=20').respond();
    var params = {'search': 'patient.drug.openfda.brand_name:Aspirin+AND+patient.patientweight:[180+TO+200]',
                  'count': 'patient.reaction.reactionmeddrapt.exact',
                  'limit': '20'};
    DrugEventsService.searchEvents(params);
    $httpBackend.flush();
  });
});
