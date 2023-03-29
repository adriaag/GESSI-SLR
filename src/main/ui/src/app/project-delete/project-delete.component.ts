import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DataService } from '../data.service';

@Component({
  selector: 'app-project-delete',
  templateUrl: './project-delete.component.html',
  styleUrls: ['./project-delete.component.css']
})
export class ProjectDeleteComponent {
  @Input('idProject') idProject!: number;
  @Input('nameProject') nameProject!: string;
  @Output() projectDeleted = new EventEmitter();

  constructor(private dataService: DataService) {}

  operationMsg: string = ""

  deleteProjectConfirm() {
    let message: string = ""
    if(this.idProject < 0) {
      message = "Are you sure you wanna reset " + this.nameProject + " ?"
    }
    else{
      message =  "Are you sure you wanna delete the project " + this.nameProject + " and all the references and criterias?"
    }
    if(confirm(message)) {
      this.deleteProject()
    }
  }

  deleteProject() {
    this.dataService.deleteProject(this.idProject).subscribe((resposta)=> {
      this.operationMsg = resposta.message
      this.projectDeleted.emit()
    })

  }

}
