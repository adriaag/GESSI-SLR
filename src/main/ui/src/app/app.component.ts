import { Component, OnInit } from '@angular/core';
import { Project } from './dataModels/project';
import { DataService } from './data.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  title = 'GESSI-SLR';
  projects: Project[] = [];
  selectedProject: Number = NaN;


  constructor(private dataService: DataService) {}

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
    this.selectedProject = value.target.value;
}


  

}
