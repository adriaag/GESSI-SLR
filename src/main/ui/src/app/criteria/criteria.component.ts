import { Component, EventEmitter, Input, Output } from '@angular/core';
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
  @Input("idDuplicateCriteria") idDuplicateCriteria!: number
  @Input("idProject") idProject!: number

  @Output() criteriaUpdated = new EventEmitter();

  constructor(private dialog: MatDialog){}

  errors: String = ""

  openCriteriaDialog(selectedCriteria: Criteria| null, criteriaType: string){

    if (selectedCriteria === null) {
      selectedCriteria = {id: -1, name:"", text:"", type: criteriaType, idProject:this.idProject}
    }
    const critDialog = this.dialog.open(CriteriaEditComponent, {
      data: {
        criteria: selectedCriteria, 
        idDuplicateCriteria: this.idDuplicateCriteria
      }
    })
    critDialog.afterClosed().subscribe(result => {
      console.log(result)
      if (result === "") {
        this.criteriaUpdated.emit()
      }
      else {
        this.errors = result
      }
    });

  }
}
