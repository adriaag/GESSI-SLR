import { Component, EventEmitter, Inject, Input, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { DataService } from '../data.service';
//import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AddReference } from '../dataModels/addReference';
import { Article } from '../dataModels/article';

@Component({
  selector: 'app-reference-import-manually',
  templateUrl: './reference-import-manually.component.html',
  styleUrls: ['./reference-import-manually.component.css']
})
export class ReferenceImportManuallyComponent {

  constructor(private dataService: DataService){}

  @Input('dataLoading') dataLoading!: boolean
  @Output() importSubmitted = new EventEmitter<AddReference>();
  
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

  doiMaxLength = 100;
  typeMaxLength = 50;
  nameVenMaxLength = 300;
  titleMaxLength = 200;
  keywordsMaxLength = 1000;
  numberMaxLength = 10;
  pagesMaxLength = 20;
  volumeMaxLength = 20;
  abstractMaxLength = 10000;
  affiliationNamesMaxLength = 300;

close(){
  var authors: string[] | null= null;
  if(this.authorNames.value !== null) {
    //Tot això és per evitar problemes a l'enviar la informació. Al transformar-ho tot
    //a string ' ,' s'interpreta com una separació d'elements i la tupla
    //cognom, nom; s'interpreta com a dos noms.
    //Per evitar-ho es canvia la coma per ';' per tal d'evitar tenir més caràcters especials
    //dels estrictament necessaris
    authors =  this.authorNames.value.replace(/;/g," ; ").replace(/, /g,';').split(" ; ").slice(0, -1);
  }

  let affiliations: string[] | null = null;
  if(this.affiliationNames.value !== null) {
    affiliations =  this.affiliationNames.value.split(";").slice(0, -1);
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
    resum: this.abstract.value,
    authorNames: authors,
    affiliationNames: affiliations
  }

  this.importSubmitted.emit(refData)

  //this.dialogRef.close(refData)
  
}

getLength(s: FormControl){
  return s.value === null? 0 : s.value.length
}

reset() {
  this.doi.reset();
  this.authorNames.reset();
  this.type.reset();
  this.nameVen.reset();
  this.title.reset();
  this.keywords.reset();
  this.number.reset();
  this.numpages.reset();
  this.pages.reset();
  this.volume.reset();
  this.any.reset();
  this.abstract.reset();
  this.affiliationNames.reset();
}

getArticle(doi: string) {
  this.dataService.getArticle(doi).subscribe({
    next: (art: Article) => {
      if(art !== null) {
        this.authorNames.setValue(this.arrayToString(art.researchers))
        this.type.setValue(art.type);
        if(art.ven !== null) this.nameVen.setValue(art.ven.name);
        this.title.setValue(art.title);
        this.keywords.setValue(art.keywords);
        this.number.setValue(art.keywords);
        this.numpages.setValue(art.numpages);
        this.pages.setValue(art.pages);
        this.volume.setValue(art.volume);
        this.any.setValue(art.any);
        this.abstract.setValue(art.abstractA);
        this.affiliationNames.setValue(this.arrayToString(art.companies));
      }
    }
  })
}

private arrayToString(array: any): string {
  let ret: string = ""
  for (let a of array) {
    ret += a.name + ";"
  }
  return ret;

}

}
