import { Component, Inject, Injectable, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Criteria } from '../dataModels/criteria';
import { FormControl } from '@angular/forms';
import { DataService } from '../data.service';

@Component({
  selector: 'app-criteria-edit',
  templateUrl: './criteria-edit.component.html',
  styleUrls: ['./criteria-edit.component.css']
})
export class CriteriaEditComponent {
  
  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private dataService: DataService) {}
  
  criteria: Criteria = this.data.criteria
  idDuplicateCriteria: number = this.data.idDuplicateCriteria
  name = new FormControl(this.criteria.name)
  desc = new FormControl(this.criteria.text)
  type = new FormControl(this.criteria.type)
  inclusionDisabled = (this.idDuplicateCriteria === this.criteria.id)
  errors = ""

  submit(): void {
    if (this.criteria.id == -1) {
      this.createCriteria()
    }
  }

  createCriteria(): void {
    this.dataService.createCriteria(this.name.value!,this.desc.value!, this.type.value!, this.criteria.idProject).subscribe((resposta) => {
      this.errors = resposta

    })


  }


}
