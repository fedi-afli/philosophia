import { TestBed } from '@angular/core/testing';

import { UnavailabilityServiceService } from './unavailability-service.service';

describe('UnavailabilityServiceService', () => {
  let service: UnavailabilityServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UnavailabilityServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
