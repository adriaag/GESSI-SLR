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

  doiMaxLength = 50;
  typeMaxLength = 50;
  nameVenMaxLength = 300;
  titleMaxLength = 200;
  keywordsMaxLength = 1000;
  numberMaxLength = 10;
  pagesMaxLength = 20;
  volumeMaxLength = 20;
  abstractMaxLength = 6000;
  affiliationNamesMaxLength = 300;

close(){
  let authors: string[] | null= null;
  if(this.authorNames.value !== null) {
    authors =  this.authorNames.value.split(";")
  }

  let affiliations: string[] | null = null;
  if(this.affiliationNames.value !== null) {
    affiliations =  this.affiliationNames.value.split(";")
  }
  let refData: AddReference = {
    doi: this.doi.value!,
    type: this.type.value,
    nameVen: this.nameVen.value,
    title: this.title.value,
    keywords: this.keywords.value,
    number: this.number.value,
    numpages: this.numpages.value === null? -1: this.numpages.value,
    pages: this.pages.value,
    volume: this.volume.value,
    any: this.any.value === null? -1: this.any.value,
    abstract: this.abstract.value,
    authorNames: authors,
    affiliationNames: affiliations
  }

  this.dialogRef.close(refData)
  
}

getLength(s: FormControl){
  return s.value === null? 0 : s.value.length
}

}
