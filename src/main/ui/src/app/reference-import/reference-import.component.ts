import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DataService } from '../data.service';
import { AddReference } from '../dataModels/addReference';
import { ImportError } from '../dataModels/importError';
import { ReferenceImportManuallyComponent } from '../reference-import-manually/reference-import-manually.component';

@Component({
  selector: 'app-reference-import',
  templateUrl: './reference-import.component.html',
  styleUrls: ['./reference-import.component.css']
})
export class ReferenceImportComponent {
  @Input('dlNames') DLNames!: String[]
  @Input('idProject') idProject!: number
  @Output() newReferencesImported = new EventEmitter();

  constructor(private dialog: MatDialog, private dataService: DataService){}

  fileToUpload!: File;
  digitalLibraryId: Number = NaN;
  newDL: number = NaN;
  newName: string = "";
  errors: ImportError[] = [];
  refsImp: number = NaN;
  refsDupl: number = NaN;
  DLnew: string = "";
  errorFile: string = "";
  importBool: boolean = false;

  handleFileInput(event: Event) {
    const target= event.target as HTMLInputElement;
    this.fileToUpload = (target.files)![0]
    /** do something with the file **/
  };

  changeDL(value: any) {
    this.digitalLibraryId = value.target.value;
  }

  submitFile() {
    this.dataService.createReferenceFromFile(String(this.idProject), String(this.digitalLibraryId), this.fileToUpload).subscribe((resposta)=> {
      this.importBool = resposta.importBool
      this.errorFile = resposta.errorFile
      this.newDL = resposta.newDL;
      this.newName = resposta.newName;
      this.errors = resposta.errors;
      this.refsImp = resposta.refsImp;
      this.refsDupl = resposta.refsDupl;
      this.DLnew = resposta.DLnew;

      if(this.importBool) {
        this.newReferencesImported.emit();
      }

    })

  }

  createReference(refData: AddReference) {
    this.dataService.createReferenceFromForm(refData, this.idProject).subscribe((resposta)=> {
      console.log(resposta)
      this.newReferencesImported.emit();
    })
  }

  openImportManuallyDialog(){
 
    const manRefDialog = this.dialog.open(ReferenceImportManuallyComponent, {
      height: '100%'}
    )

    manRefDialog.afterClosed().subscribe(result => {
      if (result !== undefined) {
        this.createReference(result)
      }

    })
  }

}
