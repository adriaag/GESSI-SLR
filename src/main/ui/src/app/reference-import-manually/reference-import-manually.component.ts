import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AddReference } from '../dataModels/addReference';

@Component({
  selector: 'app-reference-import-manually',
  templateUrl: './reference-import-manually.component.html',
  styleUrls: ['./reference-import-manually.component.css']
})
export class ReferenceImportManuallyComponent {
  constructor(public dialogRef: MatDialogRef<ReferenceImportManuallyComponent>) {}

  doi = new FormControl();
  type = new FormControl();
  nameVen = new FormControl();
  title = new FormControl();
  keywords = new FormControl();
  number = new FormControl();
  numpages = new FormControl();
  pages = new FormControl();
  volume = new FormControl();
  any = new FormControl();
  abstract = new FormControl();
  authorNames = new FormControl();
  affiliationNames = new FormControl();

close(){
  
}

}
