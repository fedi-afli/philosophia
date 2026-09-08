import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnavailabilityPickerComponent } from './unavailability-picker.component';

describe('UnavailabilityPickerComponent', () => {
  let component: UnavailabilityPickerComponent;
  let fixture: ComponentFixture<UnavailabilityPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnavailabilityPickerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UnavailabilityPickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
