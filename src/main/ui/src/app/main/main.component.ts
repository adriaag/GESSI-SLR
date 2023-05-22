import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Project } from '../dataModels/project';
import { DataService } from '../data.service';
import { Reference } from '../dataModels/reference';
import { ImportError } from '../dataModels/importError';
import { Criteria } from '../dataModels/criteria';
import { MatDialog } from '@angular/material/dialog';
import { ProjectCreateComponent } from '../project-create/project-create.component';
import { FormControl } from '@angular/forms';
import { LoginComponent } from '../login/login.component';
import { User } from '../dataModels/user';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  
  projects: Project[] = [];
  selectedProject = new FormControl();
  selectedOption: String = "index"
  references: Reference[] = [];
  errors: ImportError[] = [];
  dlNames: String[] = [];
  usernames: string[] = []
  IC: Criteria[] = [];
  EC: Criteria[] = [];


  constructor(private dataService: DataService, private authService: AuthenticationService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loginDialog();
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
      if (resposta != undefined) {
        this.createProject(resposta)
      }
      
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
    this.selectedProject.setValue(null)
    this.getProjects()
  }

  getProjects(): void {
    this.dataService.getProjects().subscribe({
      next: (resposta)=> {
      //console.log(resposta , 'User resume response');
      this.projects = resposta;
      this.defaultProject()
      }
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
    console.log("reference", this.references)

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
    this.dataService.getDLNames().subscribe({
      next: (resposta)=> {
        console.log(resposta , 'DLs');
        this.dlNames = resposta;
      }
  })
  }

  getUsernames(): void {
    this.dataService.getUsernames().subscribe({
      next: (resposta) => {
        this.usernames = resposta
        console.log(resposta , 'Users');
      }
    })
  }

  //funció temporal. L'ubicació de projects ha de canviar
  defaultProject(): void {
    console.log('selected project', this.selectedProject)
    if(this.selectedProject.value == null){
      if (this.projects.length > 0) {
        this.selectedProject.setValue(this.projects[0])
        this.changeProject()
      }
      else {
        this.createProjectDialogNoProjects()
      }
    }
  }

  //funció temporal
  createProjectDialogNoProjects() {
    let createProjectDialog = this.dialog.open(ProjectCreateComponent)
    createProjectDialog.afterClosed().subscribe((resposta) => {
      if (resposta != undefined) {
        this.createProject(resposta)
      }
      else {
        this.createProjectDialogNoProjects()
      }
      
    })

  }

  loginDialog() {
    let loginDialog = this.dialog.open(LoginComponent)
    loginDialog.afterClosed().subscribe((resposta) => {
      if (resposta != undefined) {
        this.login(resposta.user)
      }
      else {
        this.loginDialog()
      }
      
    })

  }

  login(user: User) {
    this.authService.login(user).subscribe({
      next: (resposta)=>{
      console.log(resposta)
      this.getProjects()
      this.getDLNames()
      this.getUsernames()
    },error: (error) => {
      this.loginDialog()

    }})

  }

  logout() {
    this.authService.logout()
    this.loginDialog()
  }

}
