import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferenceImportComponent } from './reference-import.component';

describe('ReferenceImportComponent', () => {
  let component: ReferenceImportComponent;
  let fixture: ComponentFixture<ReferenceImportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReferenceImportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReferenceImportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
