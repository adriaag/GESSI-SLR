import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DataService } from '../data.service';
import { ImportError } from '../dataModels/importError';

@Component({
  selector: 'app-reference-import',
  templateUrl: './reference-import.component.html',
  styleUrls: ['./reference-import.component.css']
})
export class ReferenceImportComponent {
  @Input('dlNames') DLNames!: String[]
  @Input('idProject') idProject!: Number
  @Output() newReferencesImported = new EventEmitter();

  constructor(private dataService: DataService){}

  fileToUpload!: File;
  digitalLibraryId: Number = NaN;
  newDL: number = NaN;
  newName: string = "";
  errors: ImportError[] = [];
  refsImp: number = NaN;
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
      this.DLnew = resposta.DLnew;

      if(this.importBool) {
        this.newReferencesImported.emit();
      }

    })

  }

}
