import { Component, OnInit } from '@angular/core';
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

  dataLoaded: boolean = false
  trigger: boolean = false
  triggerProject: boolean = false


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
    this.dataLoaded = false
    this.selectedProject.setValue(null)
    this.triggerProject = !this.triggerProject
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
    this.dataLoaded = false
    this.triggerProject = !this.triggerProject
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
      this.dataLoaded = true
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

  deleteReference(ref: any) {
    this.references.splice(this.references.indexOf(ref),1)
    this.trigger = !this.trigger
  }

  updateProject(project: Project) {
    let index = 0
    while(this.projects[index].id != project.id) ++index;
    this.projects[index] = project
    this.selectedProject.setValue(project)
    this.triggerProject = !this.triggerProject
  }

  private getOrder( table: string, type: string): string {
    let project = this.selectedProject.value
    if(project == null) return ""
    let ret = ""
    switch(table) {
      case 'search':
        switch(type) {
          case 'col':
            ret = project.orderColSearch;
            break
          case 'dir':
            ret = project.orderDirSearch;
            break
          default:
            ret = ""
            break
        };
        break
      case 'screen':
        switch(type) {
          case 'col':
            ret = project.orderColScreen;
            break
          case 'dir':
            ret = project.orderDirScreen;
            break
          default:
            ret = ""
            break
        };
      break
      default:
        ret = ""
    }
    return ret !== 'null'? ret : "";
  }

  getOrderColSearch(): string {
    return this.getOrder('search','col')
  }

  getOrderDirSearch(): string {
    return this.getOrder('search','dir')
  }

  getOrderColScreen(): string {
    return this.getOrder('screen','col')
  }

  getOrderDirScreen(): string {
    return this.getOrder('screen','dir')
  }

  updateOrderSearch(order: {col: string, dir: string}) {
    this.updateOrder(order.col, order.dir, this.getOrderColScreen(), this.getOrderDirScreen())
  }

  updateOrderScreen(order: {col: string, dir: string}) {
    this.updateOrder(this.getOrderColSearch(), this.getOrderDirSearch(), order.col, order.dir)
  }

  private updateOrder(orderColSearch: string, orderDirSearch: string, 
    orderColScreen: string, orderDirScreen: string) {
      this.dataService.updateProjectOrder(this.getIdProject(), orderColSearch, orderDirSearch,
        orderColScreen, orderDirScreen).subscribe({
          next: (result) => {
            let i = this.getPosProject(this.getIdProject())
            this.projects[i].orderColSearch = orderColSearch
            this.projects[i].orderDirSearch = orderDirSearch
            this.projects[i].orderColScreen = orderColScreen
            this.projects[i].orderDirScreen = orderDirScreen

          }
        })

  }

  private getPosProject(id: number): number {
    let i = 0
    for(let proj of this.projects) {
      if(proj.id === id) return i;
      i += 1
    }
    return -1
  }

}
