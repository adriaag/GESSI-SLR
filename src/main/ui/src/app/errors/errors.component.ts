import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { DataService } from '../data.service';
import { ImportError } from '../dataModels/importError';
import { ProjectService } from '../project.service';

@Component({
  selector: 'app-errors',
  templateUrl: './errors.component.html',
  styleUrls: ['./errors.component.css']
})
export class ErrorsComponent {
  errors: ImportError[] = []
  projectId: number  = NaN
  sortedData: ImportError[] = []
  dataSource!: MatTableDataSource<ImportError>;
  displayedColumns: string[] = ['dat','idDL','doi','bibtex'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;


  constructor(private dataService: DataService, private projectService: ProjectService) {}

  ngOnInit(){
    this.getProject();
    this.getErrors();
  }

  getProject(): void {
    this.projectService.getProject().subscribe((resposta) => {
      console.log(resposta , 'Projecte');
      if (resposta != this.projectId) {
        this.projectId = resposta
        this.getErrors()
      }

    })

  }

  getErrors(): void {
    this.dataService.getErrors(this.projectId).subscribe((resposta)=> {
      console.log(resposta , 'User resume response');
      this.errors = resposta;
      this.sortedData = this.errors.slice();
      this.dataSource = new MatTableDataSource(resposta);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sortData = this.sortData();
      this.dataSource.sort = this.sort;
      this.dataSource.filterPredicate = this.filterData();
    })
  }

  filterData() {
    let filterFunction = 
        (data: ImportError, filter: string): boolean => {
          if (filter) {
            //obtenim una string amb totes les dades de error que apareixen a la taula 
            var filterText = ""
            filterText += data.dateTime;
            filterText += data.idDl;
            filterText += data.doi;
            filterText += data.bibTex;

            filterText = filterText.toLowerCase()

            console.log(filterText, "Text per filtrar")
            
            if (filterText.indexOf(filter) != -1) {
                return true;
            }
            return false;
          } else {
            return true;
          }
       };    return filterFunction;
}
  
  sortData() {
    let sortFunction =
    (errs: ImportError[], sort: MatSort): ImportError[] => {
      if (!sort.active || sort.direction === '') {
        return errs;
      }

      return errs.sort((a, b) => {
        const isAsc = sort.direction === 'asc';
        switch (sort.active) {
          case 'dat':
            return compare(a.dateTime, b.dateTime, isAsc);
          case 'idDL':
            return compare(a.idDl, b.idDl, isAsc);
          case 'doi':
            return compare(a.doi, b.doi, isAsc);
          case 'bibTex':
            return compare(a.bibTex, b.bibTex, isAsc);
          default:
            return 0;
        }
      });
    }

    return sortFunction; 
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}

function compare(a: Number | String | Date, b: Number | String | Date, isAsc: boolean) {
    return (a > b ? -1 : 1) * (isAsc ? 1 : -1);
}