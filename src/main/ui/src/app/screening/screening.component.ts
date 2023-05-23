import { Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { map, merge, Observable, of, startWith } from 'rxjs';
import { DataService } from '../data.service';
import { Criteria } from '../dataModels/criteria';
import { Exclusion } from '../dataModels/exclusion';
import { Reference } from '../dataModels/reference';
import { UserDesignation } from '../dataModels/userDesignation';
import { ReferenceClassifyComponent } from '../reference-classify/reference-classify.component';

@Component({
  selector: 'app-screening',
  templateUrl: './screening.component.html',
  styleUrls: ['./screening.component.css']
})
export class ScreeningComponent {
  @Input('references') referenceslist!: Reference[]
  @Input('idProject') idProject!: number
  @Input('exclusionCriteria') exclusionCriteria!: Criteria[]
  @Input('usernames') usernames!: string[]
  @Output() referencesUpdated = new EventEmitter();

  references: Reference[] = [];
  sortedData: Reference[] = [];
  dataSource!: MatTableDataSource<Reference>;
  displayedColumns: string[] = ['ref','tit', 'abs', 'usr1', 'sta1', 'icec1'];
  filterValue: string = ""

  refind: { [id: number] : number; } = {}
  usr1: FormArray = new FormArray<FormControl>([]);
  usrFilter: string = ""

  critind: { [id: number] : number; } = {}
  crit1: FormArray = new FormArray<FormControl>([])
  crit1Filter: string = ""
  focusedCriteria1: number[] = []

  filteredOptionsUsr1: Observable<string[]> = new Observable<string[]>
  filteredOptionsCrit1: Observable<Criteria[]> = new Observable<Criteria[]>
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('crit1Input') crit1Input!: ElementRef<HTMLInputElement>;

  constructor(private dataService: DataService, private dialog: MatDialog) {}

  ngOnChanges() {
    this.references = this.referenceslist
    this.uploadUsr1()
    this.sortedData = this.references.slice();
    this.dataSource = new MatTableDataSource(this.referenceslist);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortData = this.sortData();
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.filterData();
    this.applyFilterWhenReloading()

    merge(...this.usr1.controls.map(control => control.valueChanges))
    .subscribe((value) => {
      this.filteredOptionsUsr1 = of(this.filtreUsr(value))
    })

    merge(...this.crit1.controls.map(control => control.valueChanges))
    .subscribe((value) => {
      this.filteredOptionsCrit1 = of(this.filtreCrit(value))
    })



  }

  ngAfterViewInit() {
    this.dataSource.sortData = this.sortData();
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.filterData();
    this.dataSource.paginator = this.paginator;
  }

  uploadUsr1() {
    this.refind = {};
    this.usr1 = new FormArray<FormControl>([])
    var ind = 0
    var f
    var c
    for (var ref of this.referenceslist) {
      ref.usersCriteria1 === null? f = new FormControl(): f = new FormControl(ref.usersCriteria1.username)
      this.usr1.push(f)
      ref.usersCriteria1 === null? c = new FormControl(): c = new FormControl(ref.usersCriteria1.criteriaList)
      this.crit1.push(c)
      this.refind[ref.idProjRef] = ind
      ind += 1 
    }

    ind = 0
    for (let cri of this.exclusionCriteria) {
      this.critind[cri.id] = ind
      ind += 1
    }
  }

  filtreUsr(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.usernames.filter(option => option.toLowerCase().includes(filterValue));
  }

  filtreCrit(name: any): Criteria[] {
    console.log(typeof name)
    if (typeof name === "string") {
      const filterValue = name.toLowerCase();
      let filteredData: Criteria[] = []
      this.filteredOptionsCrit1.subscribe({
        next: (data) => {
          filteredData = data
        }
      })

      console.log('Filter',filteredData)

      return this.exclusionCriteria.filter(
        option => option.name.toLowerCase().includes(filterValue)
        && !this.focusedCriteria1.includes(option.id)
      )
    }
    return this.exclusionCriteria
  }

  changeUser(numDes: number, ref: Reference) {
    let user = this.usr1.at(this.refind[ref.idProjRef]).value
    this.dataService.updateReferenceUserDesignation(ref.idRef, this.idProject, user, numDes).subscribe({
      next: (value) => {
        this.referencesUpdated.emit()
      }
    })
  }

  resetFilter(idProjRef: number) {
    let val = this.usr1.at(this.refind[idProjRef]).value
    val !== null? this.filteredOptionsUsr1 = of(this.filtreUsr(val)): this.filteredOptionsUsr1 = of(this.usernames)
  }

  resetFilterCriteria(usersCriteria : UserDesignation) {
    if (usersCriteria !== null) {
      this.filteredOptionsCrit1 = of(this.exclusionCriteria.filter((option) => !usersCriteria.criteriaList.includes(option.id)))
      this.focusedCriteria1 = usersCriteria.criteriaList
    }
    else {
      this.filteredOptionsCrit1 = of(this.exclusionCriteria)
    }



  }

  getCtrl(i: number): FormControl<string> {
    return this.usr1.at(this.refind[i]) as FormControl<string>

  }

  getCritCtrl(i: number): FormControl<number[]> {
    return this.crit1.at(this.refind[i]) as FormControl<number[]>
  }

  getCriteriaNames(ref: Reference, numD: number) {
    var uc;
    numD == 1? uc = ref.usersCriteria1: uc = ref.usersCriteria2

    if(uc !== null) {
      let criteria:Criteria[] = []
      for (let id of uc.criteriaList)
        criteria.push(this.exclusionCriteria.at(this.critind[id])!)
      return criteria
    }
    return []
  }

  selectedCriteria1(event: MatAutocompleteSelectedEvent, ref: Reference): void {
    console.log(event.option.value)
    ref.usersCriteria1.criteriaList.push(Number(event.option.value));
    this.crit1Input.nativeElement.value = '';
    this.crit1.at(this.refind[ref.idProjRef]).setValue('');
    this.addUserDesignationCriteria(ref.usersCriteria1)
  }

  addUserDesignationCriteria(uc: UserDesignation) {
    this.dataService.updateReferenceUserDesignationCriteria(this.idProject, uc).subscribe({
      next: (value) => {
          this.referencesUpdated.emit()
      }
    })
  }

  removeCri1(ref: Reference, critId: number) {
    const index = ref.usersCriteria1.criteriaList.indexOf(critId)

    if (index >= 0) {
      ref.usersCriteria1.criteriaList.splice(index, 1);
    }

  }

  getState(ref: Reference): string {
    if (ref.usersCriteria1 !== null) {
      if (ref.usersCriteria1.criteriaList.length > 0) {
        return 'out'
      }
      else {
        if (ref.usersCriteria1.processed) {
          return 'in'
        }
        else {
          return 'undiecided'
        }
      }
    }
    return ''

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

  //possible optimització precomputant-ho quan es carreguen les dades
  filterData() {
    let filterFunction = 
        (data: Reference, filter: string): boolean => {
          if (filter) {
            //obtenim una string amb totes les dades de reference que apareixen a la taula 
            var filterText = ""
            if(data.doi !== null) filterText += data.doi;
            if(data.idProjRef !== null)filterText += data.idProjRef;
            if(data.dl !== null && data.dl.name !== null)filterText += data.dl.name;
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
            if(data.art.abstractA !== null)filterText += data.art.abstractA

            filterText = filterText.toLowerCase()

            //console.log(filterText, "Text per filtrar")
            
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
          case 'ref':
            return compare(a.idRef, b.idRef, isAsc);
          case 'tit':
            return compare(a.art.title, b.art.title, isAsc);
          case 'sta':
            return compare(a.state,b.state, isAsc);
          case 'cri':
            return compareCriteria(a.exclusionDTOList, b.exclusionDTOList, isAsc);
          case 'abs':
            return compare(String(a.art.abstractA),String(b.art.abstractA), isAsc);
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

