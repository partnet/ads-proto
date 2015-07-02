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

  it('serializes parameters without encoding "+"', function () {
    $httpBackend.expectGET('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.brand_name%3ASynthroid+AND+patient.patientweight%3A%5B180+TO+200%5D&count=patient.reaction.reactionmeddrapt.exact&limit=20').respond();
    var params = {'search': 'patient.drug.openfda.brand_name:Synthroid+AND+patient.patientweight:[180+TO+200]',
                  'count': 'patient.reaction.reactionmeddrapt.exact',
                  'limit': '20'};
    DrugEventsService.searchEvents(params);
    $httpBackend.flush();
  });

  it('calculates reaction outcomes', function () {
    var params = {'search': 'patient.drug.openfda.brand_name:Synthroid',
                  'count': 'patient.reaction.reactionmeddrapt.exact',
                  'limit': '20'};
    $httpBackend.expectGET('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.brand_name%3ASynthroid+AND+patient.reaction.reactionmeddrapt%3A%22NAUSEA%22&count=patient.reaction.reactionmeddrapt.exact').respond({results: {'NAUSEA': {term: 5, count: 83}}});
    $httpBackend.expectGET('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.brand_name%3ASynthroid+AND+patient.reaction.reactionmeddrapt%3A%22DYSPNOEA%22&count=patient.reaction.reactionmeddrapt.exact').respond({results: {'DYSPNOEA': {term: 6, count: 72}}});
    DrugEventsService.reactionResults = [{term: 'NAUSEA', count: 20}, {term: 'DYSPNOEA', count: 15}];
    DrugEventsService.calculateReactionOutcomes(params);
    $httpBackend.flush();

    expect(DrugEventsService.reactionResults[0].outcomes.NAUSEA.term).toBe('Fatal');
    expect(DrugEventsService.reactionResults[0].outcomes.NAUSEA.count).toBe(83);
    expect(DrugEventsService.reactionResults[1].outcomes.DYSPNOEA.term).toBe('Unknown');
    expect(DrugEventsService.reactionResults[1].outcomes.DYSPNOEA.count).toBe(72);
  });

  it('sets outcome to "Unknown" when a 404 is returned', function () {
    var params = {'search': 'patient.drug.openfda.brand_name:Synthroid',
                  'count': 'patient.reaction.reactionmeddrapt.exact',
                  'limit': '20'};
    $httpBackend.expectGET('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.brand_name%3ASynthroid+AND+patient.reaction.reactionmeddrapt%3A%22NAUSEA%22&count=patient.reaction.reactionmeddrapt.exact').respond(404, '');
    DrugEventsService.reactionResults = [{term: 'NAUSEA', count: 20}];
    DrugEventsService.calculateReactionOutcomes(params);
    $httpBackend.flush();

    expect(DrugEventsService.reactionResults[0].outcomes[0].term).toBe('Unknown');
  });
});
