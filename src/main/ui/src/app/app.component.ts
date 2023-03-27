import { Component, OnInit } from '@angular/core';
import { Project } from './dataModels/project';
import { DataService } from './data.service';
import { Router } from '@angular/router'
import { ProjectService } from './project.service';
import { Reference } from './dataModels/reference';
import { ImportError } from './dataModels/importError';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  title = 'GESSI-SLR';
  projects: Project[] = [];
  selectedProject: number = NaN;
  selectedOption: String = "index"
  references: Reference[] = [];
  errors: ImportError[] = [];


  constructor(private dataService: DataService, public router: Router, private projectService: ProjectService) {}

  ngOnInit(): void {
    this.getProjects();
  }

  getProjects(): void {
    this.dataService.getProjects().subscribe((resposta)=> {
      //console.log(resposta , 'User resume response');
      this.projects = resposta;
    })
  }

  changeProject(value: any) {
    this.projectService.setProject(value.target.value);
    this.selectedProject = value.target.value;
    this.getProjectData()
    this.getProjectErrors()
  }

  changeOption(opt: String) {
    this.selectedOption = opt
  }

  getProjectData(): void {
    this.dataService.getReferences(this.selectedProject).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.references = resposta;
    })

  }

  getProjectErrors(): void {
    this.dataService.getErrors(this.selectedProject).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.errors = resposta;
    })

  }
}

