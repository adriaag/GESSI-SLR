import { Component, OnInit } from '@angular/core';
import { Project } from './dataModels/project';
import { DataService } from './data.service';
import { Reference } from './dataModels/reference';
import { ImportError } from './dataModels/importError';
import { Criteria } from './dataModels/criteria';
import { MatDialog } from '@angular/material/dialog';
import { ProjectCreateComponent } from './project-create/project-create.component';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  title = 'GESSI-SLR';
  projects: Project[] = [];
  selectedProject = new FormControl();
  selectedOption: String = "index"
  references: Reference[] = [];
  errors: ImportError[] = [];
  dlNames: String[] = [];
  IC: Criteria[] = [];
  EC: Criteria[] = [];


  constructor(private dataService: DataService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.getProjects();
    this.getDLNames();
  }

  //funció temporal per permetre resetejar la BD. La gestió de projectes s'ha de modificar
  getIdProject() {
    if(this.selectedProject.value !== null) {
      return this.selectedProject.value.id
    }
    else{
      return -1
    }
  }

  //funció temporal per permetre resetejar la BD. La gestió de projectes s'ha de modificar
  getNameProject(){
    if(this.selectedProject.value !== null) {
      return this.selectedProject.value.name
    }
    else{
      return "the database"
    }
  }

  createProjectDialog() {
    let createProjectDialog = this.dialog.open(ProjectCreateComponent)
    createProjectDialog.afterClosed().subscribe((resposta) => {
      this.createProject(resposta)
      
    })

  }

  compareProjectObjects(project1: Project, project2: Project) {
    return project1 && project2 && project1.id == project2.id;
  }

  createProject(name: string) {
    this.dataService.createProject(name).subscribe((resposta) => {
      this.getProjects()
      this.selectedProject.setValue(resposta)
      this.changeProject()
    })
  }

  newReferences(): void {
    this.getProjectData();
    this.getProjectErrors();
  }

  projectDeleted(): void {
    this.getProjects()
    this.selectedProject.setValue('')
  }

  getProjects(): void {
    this.dataService.getProjects().subscribe((resposta)=> {
      //console.log(resposta , 'User resume response');
      this.projects = resposta;
    })
  }

  changeProject() {
    this.getProjectData()
    this.getProjectErrors()
    this.getProjectCriteria()
  }

  changeOption(opt: String) {
    this.selectedOption = opt
  }

  getProjectData(): void {
    this.dataService.getReferences(this.selectedProject.value.id).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.references = resposta;
    })

  }

  getProjectErrors(): void {
    this.dataService.getErrors(this.selectedProject.value.id).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.errors = resposta;
    })

  }

  getProjectCriteria(): void {
    this.dataService.getCriteria(this.selectedProject.value.id).subscribe((resposta) => {
      this.EC = resposta.exclusionCriteria;
      this.IC = resposta.inclusionCriteria
    })
  }

  getDLNames(): void {
    this.dataService.getDLNames().subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.dlNames = resposta;
    })

  }


}

