import { Component, OnInit} from '@angular/core';
import { DataService } from '../data.service';
import { Reference } from '../dataModels/reference';
import { Researcher } from '../dataModels/researcher';
import { ProjectService } from '../project.service';

@Component({
  selector: 'app-references',
  templateUrl: './references.component.html',
  styleUrls: ['./references.component.css']
})

export class ReferencesComponent implements OnInit{
  references: Reference[] = []
  projectId: number  = NaN
  sortedData: Reference[] = []

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
      this.sortedData = this.references.slice();
    })
  }

  downloadExcel(): void {
    this.dataService.getExcelFile(this.projectId).subscribe((resposta) => {
        var newBlob = new Blob([resposta], { type: "application/vnd.ms-excel" });
        const url= window.URL.createObjectURL(newBlob);
        window.open(url);
    });
  }
  
  sortData(sort: any) {
    const data = this.references.slice();
    if (!sort.active || sort.direction === '') {
      this.sortedData = data;
      return;
    }

    this.sortedData = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case 'doi':
          return compare(a.art.doi, b.art.doi, isAsc);
        case 'ref':
          return compare(a.idRef, b.idRef, isAsc);
        case 'dl':
          return compare(a.dl.name, b.dl.name, isAsc);
        case 'year':
          return compare(a.art.any, b.art.any, isAsc);
        /*case 'auth':
          return compare(a.art.researchers, b.art.researchers, isAsc);*/
        default:
          return 0;
      }
    });

    
  }
}

function compare(a: Number | String, b: Number | String, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}

/*function compare(a: Researcher[], b: Researcher[], isAsc: boolean) {
    if (a != null) {
      if (b == null) {
        return -1 * (isAsc ? 1 : -1)
      }
      else {
        for (let elem of a) {

        }
      }
    }
}*/


