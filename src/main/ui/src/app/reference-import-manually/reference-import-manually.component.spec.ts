import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferenceImportManuallyComponent } from './reference-import-manually.component';

describe('ReferenceImportManuallyComponent', () => {
  let component: ReferenceImportManuallyComponent;
  let fixture: ComponentFixture<ReferenceImportManuallyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReferenceImportManuallyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReferenceImportManuallyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
