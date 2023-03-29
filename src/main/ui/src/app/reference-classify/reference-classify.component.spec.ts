import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferenceClassifyComponent } from './reference-classify.component';

describe('ReferenceClassifyComponent', () => {
  let component: ReferenceClassifyComponent;
  let fixture: ComponentFixture<ReferenceClassifyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReferenceClassifyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReferenceClassifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
