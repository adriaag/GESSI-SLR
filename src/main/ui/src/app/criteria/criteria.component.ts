import { Component, Input } from '@angular/core';
import { Criteria } from '../dataModels/criteria';

@Component({
  selector: 'app-criteria',
  templateUrl: './criteria.component.html',
  styleUrls: ['./criteria.component.css']
})
export class CriteriaComponent {
  @Input("inclusionCriteria") inclusionCriteria!: Criteria[]
  @Input("exclusionCriteria") exclusionCriteria!: Criteria[]
}
