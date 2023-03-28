import { Component, Inject, Injectable, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Criteria } from '../dataModels/criteria';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-criteria-edit',
  templateUrl: './criteria-edit.component.html',
  styleUrls: ['./criteria-edit.component.css']
})
export class CriteriaEditComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public criteria: Criteria) {}
    name = new FormControl(this.criteria.name)
    desc = new FormControl(this.criteria.text)
    type = new FormControl(this.criteria.type)
}
