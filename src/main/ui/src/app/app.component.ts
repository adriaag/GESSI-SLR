import { Component, OnInit } from '@angular/core';
import { Project } from './dataModels/project';
import { DataService } from './data.service';
import { Router } from '@angular/router'
import { Reference } from './dataModels/reference';
import { ImportError } from './dataModels/importError';
import { Criteria } from './dataModels/criteria';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  title = 'GESSI-SLR';
  projects: Project[] = [];
  selectedProject!: Project;
  selectedOption: String = "index"
  references: Reference[] = [];
  errors: ImportError[] = [];
  dlNames: String[] = [];
  IC: Criteria[] = [];
  EC: Criteria[] = [];


  constructor(private dataService: DataService, public router: Router) {}

  ngOnInit(): void {
    this.getProjects();
    this.getDLNames();
  }

  newReferences(): void {
    this.getProjectData();
    this.getProjectErrors();
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
    this.dataService.getReferences(this.selectedProject.id).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.references = resposta;
    })

  }

  getProjectErrors(): void {
    this.dataService.getErrors(this.selectedProject.id).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.errors = resposta;
    })

  }

  getProjectCriteria(): void {
    this.dataService.getCriteria(this.selectedProject.id).subscribe((resposta) => {
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

