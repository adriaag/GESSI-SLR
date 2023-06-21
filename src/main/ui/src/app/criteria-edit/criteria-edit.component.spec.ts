import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CriteriaEditComponent } from './criteria-edit.component';

describe('CriteriaEditComponent', () => {
  let component: CriteriaEditComponent;
  let fixture: ComponentFixture<CriteriaEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CriteriaEditComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CriteriaEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
