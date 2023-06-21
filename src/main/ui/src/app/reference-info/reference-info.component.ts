import { Component, Inject, Injectable, OnInit} from '@angular/core';
import { Reference } from '../dataModels/reference';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-reference-info',
  templateUrl: './reference-info.component.html',
  styleUrls: ['./reference-info.component.css']
})
export class ReferenceInfoComponent{

  constructor(@Inject(MAT_DIALOG_DATA) public reference: Reference) {}


}
