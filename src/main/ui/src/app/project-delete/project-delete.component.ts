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
    let message: string = "Are you sure you wanna reset " + this.nameProject + " ?"
    if(confirm(message)) {   
      this.deleteProject()
    }
  }

  deleteDBConfirm() {
    let message: string = "Are you sure you want to recreate the database?\nCAUTION!: All stored data will be lost!"
    if (confirm(message)) {
      this.deleteDB()
    }
  }

  deleteProject() {
    this.dataService.deleteProject(this.idProject).subscribe((resposta)=> {
      this.operationMsg = "Project successfully deleted"
      this.projectDeleted.emit()
    })
  }
  
  deleteDB() {
      this.dataService.deleteDatabase().subscribe((resposta)=> {
        this.operationMsg = "Database successfully deleted"
        this.projectDeleted.emit()
      })

  }

}
