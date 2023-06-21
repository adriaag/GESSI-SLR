import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferenceInfoComponent } from './reference-info.component';

describe('ReferenceInfoComponent', () => {
  let component: ReferenceInfoComponent;
  let fixture: ComponentFixture<ReferenceInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReferenceInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReferenceInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
