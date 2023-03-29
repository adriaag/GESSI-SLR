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
  name = new FormControl(this.criteria.name)
  desc = new FormControl(this.criteria.text)
  type = new FormControl(this.criteria.type)
  inclusionDisabled = (this.criteria.type !== "inclusion")

  getOutput() {
    let data = {
      name: this.name.value,
      desc: this.desc.value,
      type: this.type.value
    }
    return data

  }

}
