import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CriteriaEditComponent } from '../criteria-edit/criteria-edit.component';
import { Criteria } from '../dataModels/criteria';
import { DataService } from '../data.service';

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

  constructor(private dialog: MatDialog, private dataService: DataService){}

  errors: String = ""

  openCriteriaDialog(selectedCriteria: Criteria| null, criteriaType: string){

    if (selectedCriteria === null) {
      selectedCriteria = {id: -1, name:"", text:"", type: criteriaType, idProject: NaN}
    }
    const critDialog = this.dialog.open(CriteriaEditComponent, {
      data: {
        criteria: selectedCriteria
      }
    })
    critDialog.afterClosed().subscribe(result => {
      if (result !== "") {
        if (selectedCriteria!.id === -1) {
          this.createCriteria(result.name, result.desc, result.type)
        }
        else {
          this.updateCriteria(selectedCriteria!.id, result.name, result.desc, result.type)
        }
      }
    });

  }

  requestDeleteCriteria(criteria: Criteria) {
    if(confirm("Are you sure to delete criteria "+ criteria.name + " ?")) {
      this.deleteCriteria(criteria)
    }
  }

  deleteCriteria(criteria: Criteria): void {
    this.dataService.deleteCriteria(criteria.id).subscribe((resposta) => {
      this.criteriaUpdated.emit()
    })
      
  }

  createCriteria(name: string, text: string, type: string ): void {
    this.dataService.createCriteria(name, text, type, this.idProject).subscribe((resposta) => {
      this.errors = resposta.message
      this.criteriaUpdated.emit()

    })
  }

  updateCriteria(id: number, name: string, text: string, type: string): void{
    this.dataService.updateCriteria(id, name, text, type, this.idProject).subscribe((resposta) => {
      this.errors = resposta
      this.criteriaUpdated.emit()
    })
  }
}
