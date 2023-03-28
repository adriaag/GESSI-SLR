import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CriteriaEditComponent } from '../criteria-edit/criteria-edit.component';
import { Criteria } from '../dataModels/criteria';

@Component({
  selector: 'app-criteria',
  templateUrl: './criteria.component.html',
  styleUrls: ['./criteria.component.css']
})
export class CriteriaComponent {
  @Input("inclusionCriteria") inclusionCriteria!: Criteria[]
  @Input("exclusionCriteria") exclusionCriteria!: Criteria[]

  constructor(private dialog: MatDialog){}

  openCriteriaDialog(selectedCriteria: Criteria| null){
    if (selectedCriteria === null) {
      selectedCriteria = {id: -1, name:"", text:"", type:"", idProject:NaN}
    }
    this.dialog.open(CriteriaEditComponent, {
      data: selectedCriteria
    });

  }
}
