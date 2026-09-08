import { TestBed } from '@angular/core/testing';

import { TeacherAvailabilityService } from './teacher-availability.service';

describe('TeacherAvailabilityService', () => {
  let service: TeacherAvailabilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeacherAvailabilityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
