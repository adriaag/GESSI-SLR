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

  constructor(private dataService: DataService){}

  fileToUpload!: File;
  digitalLibraryId: number = NaN;
  newDL: string = "";
  newName: string = "";
  errors: ImportError[] = [];
  refsImp: number = NaN;
  refsDupl: number = NaN;
  DLnew: string = "";
  errorFile: string = "";
  importBool: boolean = false;
  loading: boolean = false;
  manualRefLoading: boolean = false;
  manualMsg = ""

  handleFileInput(event: Event) {
    const target= event.target as HTMLInputElement;
    this.fileToUpload = (target.files)![0]
  };

  changeDL(value: any) {
    this.digitalLibraryId = value.target.value;
  }

  submitFile() {
    this.loading = true
    this.dataService.createReferenceFromFile(String(this.idProject), String(this.digitalLibraryId), this.fileToUpload).subscribe({
      next: (resposta)=> {
        this.loading = false
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
      },
      error: (resposta) => {
        this.loading = false
      }
    })

  }

  createReference(refData: AddReference) {
    this.manualRefLoading = true;
    this.manualMsg = "";
    this.dataService.createReferenceFromForm(refData, this.idProject).subscribe({
      next: (resposta)=> {
      console.log(resposta)
      this.newReferencesImported.emit();
      this.manualRefLoading = false;
      this.manualMsg = "Reference imported successfully"
      },
      error: (err) => {
        this.manualRefLoading = false;
      }
    })
  }

}
