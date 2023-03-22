import { Component, OnInit} from '@angular/core';
import { DataService } from '../data.service';
import { ActivatedRoute } from '@angular/router';
import { Reference } from '../dataModels/reference';
import { ProjectService } from '../project.service';

@Component({
  selector: 'app-references',
  templateUrl: './references.component.html',
  styleUrls: ['./references.component.css']
})

export class ReferencesComponent implements OnInit{
  references: Reference[] = []
  projectId: number  = NaN

  constructor(private dataService: DataService, private projectService: ProjectService) {}

  

  ngOnInit(){
    this.getProject();
    this.getReferences();
  }

  getProject(): void {
    this.projectService.getProject().subscribe((resposta) => {
      console.log(resposta , 'Projecte');
      if (resposta != this.projectId) {
        this.projectId = resposta
        this.getReferences()
      }

    })

  }

  getReferences(): void {
    this.dataService.getReferences(this.projectId).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.references = resposta;
    })
  }

}
