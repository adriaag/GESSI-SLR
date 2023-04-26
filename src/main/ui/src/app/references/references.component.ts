import { AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import { DataService } from '../data.service';
import { Reference } from '../dataModels/reference';
import { Researcher } from '../dataModels/researcher';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Exclusion } from '../dataModels/exclusion';
import { MatDialog} from '@angular/material/dialog';
import { ReferenceInfoComponent } from '../reference-info/reference-info.component';
import { ReferenceClassifyComponent } from '../reference-classify/reference-classify.component';
import { Criteria } from '../dataModels/criteria';

@Component({
  selector: 'app-references',
  templateUrl: './references.component.html',
  styleUrls: ['./references.component.css']
})

export class ReferencesComponent implements OnChanges, AfterViewInit{

  @Input('references') referenceslist!: Reference[]
  @Input('idProject') idProject!: number
  @Input('exclusionCriteria') exclusionCriteria!: Criteria[]
  @Output() referencesUpdated = new EventEmitter();

  references: Reference[] = [];
  sortedData: Reference[] = [];
  dataSource!: MatTableDataSource<Reference>;
  displayedColumns: string[] = ['doi','ref', 'dl', 'year', 'auth', 'tit', 'ven', 'sta', 'cri', 'inf', 'cla', 'del'];
  filterValue: string = ""
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;


  constructor(private dataService: DataService, private dialog: MatDialog) {}

  ngOnChanges(changes: SimpleChanges) {
    this.references = this.referenceslist
    this.sortedData = this.references.slice();
    this.dataSource = new MatTableDataSource(this.referenceslist);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortData = this.sortData();
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.filterData();
    this.applyFilterWhenReloading()

  }

  ngAfterViewInit() {
    this.dataSource.sortData = this.sortData();
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.filterData();
    this.dataSource.paginator = this.paginator;
  }

  downloadExcel(): void {
    this.dataService.getExcelFile(this.idProject).subscribe((resposta) => {
        var newBlob = new Blob([resposta], { type: "application/vnd.ms-excel" });
        const url= window.URL.createObjectURL(newBlob);
        window.open(url);
    });
  }

  viewReference(ref: Reference) {
    this.dialog.open(ReferenceInfoComponent, {
      data : ref
    });
  }

  editReferenceDialog(ref: Reference) {
    let referenceEditDialog = this.dialog.open(ReferenceClassifyComponent, {
      data: {
        reference: ref,
        exclusionCriteria: this.exclusionCriteria
      }
    })
    referenceEditDialog.afterClosed().subscribe(result => {
      if(result !== undefined)
        this.updateReference(ref.idRef, result.type, result.criteria)
    })

  }

  updateReference(id: number, type: string, idCriteria: number[]) {
    this.dataService.editReferenceCriteria(id,this.idProject,type,idCriteria).subscribe((resposta) => {
      this.referencesUpdated.emit()
    })
  }

  requestDeleteReference(reference: Reference) {
    if(confirm("Are you sure to delete reference "+ reference.art.title + " ?")) {
      this.deleteReference(reference)
    }
  }

  deleteReference(reference: Reference): void {
    this.dataService.deleteReference(reference.idRef, this.idProject).subscribe((resposta) => {
      this.referencesUpdated.emit()
    })
      
  }
  
  //possible optimització precomputant-ho quan es carreguen les dades
  filterData() {
    let filterFunction = 
        (data: Reference, filter: string): boolean => {
          if (filter) {
            //obtenim una string amb totes les dades de reference que apareixen a la taula 
            var filterText = ""
            if(data.doi !== null) filterText += data.doi;
            if(data.idProjRef !== null)filterText += data.idProjRef;
            if(data.dl.name !== null)filterText += data.dl.name;
            if(data.art.any !== null)filterText += data.art.any;
            if(data.art.researchers !== null)for (let researcher of data.art.researchers){
              filterText += researcher.name;
            }
            if(data.art.title !== null)filterText += data.art.title;
            if(data.art.ven !== null && data.art.ven.name !== null)filterText += data.art.ven.name;
            if(data.state !== null)filterText += data.state;
            if (data.exclusionDTOList != null) {
              for (let exclusion of data.exclusionDTOList){
                filterText += exclusion.nameICEC;
              }
            }

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
    (refs: Reference[], sort: MatSort): Reference[] => {
      if (!sort.active || sort.direction === '') {
        return refs;
      }

      return refs.sort((a, b) => {
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
          case 'auth':
            return compareAuthors(a.art.researchers, b.art.researchers, isAsc);
          case 'tit':
            return compare(a.art.title, b.art.title, isAsc);
          case 'ven':
            var elem1 = a.art.ven
            var elem2 = b.art.ven
            if (elem1 === null) {
              if (elem2 === null) {
                return compare(null, null, isAsc);
              }
              return compare(null, elem2.name, isAsc);
            }
            if (elem2 == null) {
              return compare(a.art.ven.name, null, isAsc);
            }
            return compare(a.art.ven.name, b.art.ven.name, isAsc);
          case 'sta':
            return compare(a.state,b.state, isAsc);
          case 'cri':
            return compareCriteria(a.exclusionDTOList, b.exclusionDTOList, isAsc);
          default:
            return 0;
        }
      });
    }

    return sortFunction; 
  }

  applyFilter(event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = this.filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  //funció pensada per què quan es modifiqui una referència el filtre es mantingui
  applyFilterWhenReloading() {
    this.dataSource.filter = this.filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }

  }
}

function compare(a: Number | String | null, b: Number | String | null, isAsc: boolean) {
    if (a === null || b === null) {
      return (a !== null ? -1 : 1) * (isAsc ? 1 : -1);
    }
    return (a > b ? -1 : 1) * (isAsc ? 1 : -1);
}

function compareAuthors(a: Researcher[], b: Researcher[], isAsc: boolean) {
    if (a !== null) {
      if (b === null) {
        return -1 * (isAsc ? 1 : -1)
      }
      else{
        var nomsA = "";
        for(let elem of a) {
          nomsA += elem.name
        }

        var nomsB = "";
        for(let elem of b) {
          nomsB += elem.name
        }

        return compare(nomsA,nomsB,isAsc)
      }

    }
    else {
      if (b === null) {
        return -1 * (isAsc ? 1 : -1)
      }
      else {
        return 1 * (isAsc ? 1 : -1)
      }
    }
}

function compareCriteria(a: Exclusion[], b: Exclusion[], isAsc: boolean) {
  if (a !== null) {
    if (b === null) {
      return -1 * (isAsc ? 1 : -1)
    }
    else{
      var nomsA = "";
      for(let elem of a) {
        nomsA += elem.nameICEC
      }

      var nomsB = "";
      for(let elem of b) {
        nomsB += elem.nameICEC
      }

      return compare(nomsA,nomsB,isAsc)
    }

  }
  else {
    if (b === null) {
      return -1 * (isAsc ? 1 : -1)
    }
    else {
      return 1 * (isAsc ? 1 : -1)
    }
  }
}


