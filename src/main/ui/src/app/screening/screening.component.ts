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
  @Input('inclusionCriteria') inclusionCriteria!: Criteria[]
  @Input('usernames') usernames!: string[]
  @Output() referencesUpdated = new EventEmitter();

  criterias: Criteria[] = [];

  references: Reference[] = [];
  sortedData: Reference[] = [];
  dataSource!: MatTableDataSource<Reference>;
  displayedColumns: string[] = ['ref','tit', 'abs', 'usr1', 'sta1','icec1','usr2','sta2','icec2'];
  filterValue: string = ""

  refind: { [id: number] : number; } = {}
  critind: { [id: number] : number; } = {}
  
  crit1: FormArray = new FormArray<FormControl>([])
  critEnabled1: FormArray = new FormArray<FormControl>([])
  usr1:  FormArray = new FormArray<FormControl>([])
  crit2: FormArray = new FormArray<FormControl>([])
  critEnabled2: FormArray = new FormArray<FormControl>([])
  usr2:  FormArray = new FormArray<FormControl>([])
  
  
  focusedCriteria: number[] = []
  focusedUser: string = ''
  filteredOptionsUsr: Observable<string[]> = new Observable<string[]>
  filteredOptionsCrit: Observable<Criteria[]> = new Observable<Criteria[]>
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('crit1Input') crit1Input!: ElementRef<HTMLInputElement>;

  constructor(private dataService: DataService, private dialog: MatDialog) {}

  ngOnChanges(changes: SimpleChanges) {
    console.log(changes)
    this.references = this.referenceslist
    this.criterias = this.exclusionCriteria.concat(this.inclusionCriteria)
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
      console.log('usuaris',value)
      this.filteredOptionsUsr = of(this.filtreUsr(value))
    })

    merge(...this.crit1.controls.map(control => control.valueChanges))
    .subscribe((value) => {
      console.log('criteris',value)
      this.filteredOptionsCrit = of(this.filtreCrit(value))
    })

    merge(...this.usr2.controls.map(control => control.valueChanges))
    .subscribe((value) => {
      console.log('usuaris2',value)
      this.filteredOptionsUsr = of(this.filtreUsr(value))
    })

    merge(...this.crit2.controls.map(control => control.valueChanges))
    .subscribe((value) => {
      console.log('criteris2',value)
      this.filteredOptionsCrit = of(this.filtreCrit(value))
    })



  }

  ngAfterViewInit() {
    this.dataSource.sortData = this.sortData();
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.filterData();
    this.dataSource.paginator = this.paginator;
  }

  uploadUsr1() {
    this.usr1 =  new FormArray<FormControl>([])
    this.crit1 =  new FormArray<FormControl>([])
    this.critEnabled1 = new FormArray<FormControl>([])
    this.usr2 =  new FormArray<FormControl>([])
    this.crit2 =  new FormArray<FormControl>([])
    this.critEnabled2 = new FormArray<FormControl>([])
    
    this.refind = {};
    this.critind = {};

    var ind = 0
    var f
    var c
    var cEnable1
    var cEnable2

    for (var ref of this.referenceslist) {
      ref.usersCriteria1 === null? f = new FormControl(): f = new FormControl(ref.usersCriteria1.username)
      this.usr1.push(f)

      ref.usersCriteria2 === null? f = new FormControl(): f = new FormControl(ref.usersCriteria2.username)
      this.usr2.push(f)

      cEnable1 = new FormControl()

      if(ref.usersCriteria1 === null) {
        c = new FormControl()
        cEnable1.disable()
      }
      else {
        c = new FormControl(ref.usersCriteria1.criteriaList)
        if (ref.usersCriteria1.processed) cEnable1.disable()
      }
      this.crit1.push(c)
      this.critEnabled1.push(cEnable1)

      cEnable2 = new FormControl()

      if(ref.usersCriteria2 === null) {
        c = new FormControl()
        cEnable2.disable()
      }
      else {
        c = new FormControl(ref.usersCriteria2.criteriaList)
        if (ref.usersCriteria2.processed) cEnable2.disable()
      }
      this.crit2.push(c)
      this.critEnabled2.push(cEnable2)


      this.refind[ref.idProjRef] = ind
      ind += 1 
    }
    
    
    ind = 0
    for (let cri of this.criterias) {
      this.critind[cri.id] = ind
      ind += 1
    }
  }

  filtreUsr(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.usernames.filter(option => option.toLowerCase().includes(filterValue));
  }

  filtreCrit(name: any): Criteria[] {
    //console.log(name)
    if (typeof name === "string") {
      const filterValue = name.toLowerCase();
      return this.criterias.filter(
        option => option.name.toLowerCase().includes(filterValue)
        && !this.focusedCriteria.includes(option.id)
      )
    }
    return this.criterias
  }

  changeUser(numDes: number, ref: Reference) {
    let usrCtrl = this.getUserCtrl(numDes) 
    let uc
    if (numDes === 1) uc = ref.usersCriteria1
    else uc = ref.usersCriteria2

    let user = usrCtrl.at(this.refind[ref.idProjRef]).value
    if (uc === null || uc.username != user)
      this.dataService.updateReferenceUserDesignation(ref.idRef, this.idProject, user, numDes).subscribe({
        next: (value) => {
          if(value.numDesignation == 1) {
            this.critEnabled1.at(this.refind[ref.idProjRef]).enable()
            ref.usersCriteria1 = value
          }
          else {
            this.critEnabled2.at(this.refind[ref.idProjRef]).enable()
            ref.usersCriteria2 = value
          }
          
        }
      })
  }

  resetFilter(idProjRef: number, numD : number) {
    let uc: FormArray = this.getUserCtrl(numD)
    this.focusedUser = uc.at(this.refind[idProjRef]).value
    uc.at(this.refind[idProjRef]).setValue('')
    this.filteredOptionsUsr = of(this.usernames)
    //val !== null? this.filteredOptionsUsr1 = of(this.filtreUsr(val)): this.filteredOptionsUsr1 = of(this.usernames)
  }

  recoverUsername(idProjRef: number, numD: number) {
    let usrCtrl = this.getUserCtrl(numD)
    if (this.focusedUser !== '' && typeof usrCtrl.at(this.refind[idProjRef]).value === "string") {
      usrCtrl.at(this.refind[idProjRef]).setValue(this.focusedUser)
      this.focusedUser = ''
    }


  }

  resetFilterCriteria(usersCriteria : UserDesignation) {
    console.log('reset', usersCriteria)
    if (usersCriteria !== null) {
      this.filteredOptionsCrit = of(this.criterias.filter((option) => !usersCriteria.criteriaList.includes(option.id)))
      this.focusedCriteria = usersCriteria.criteriaList
      console.log('uc no nul')
    }
    else {
      this.filteredOptionsCrit = of(this.criterias)
      console.log('uc nul')
    }
    console.log(this.criterias.filter((option) => !usersCriteria.criteriaList.includes(option.id)))



  }

  getCriteriaNames(ref: Reference, numD: number) {
    var uc;
    numD == 1? uc = ref.usersCriteria1: uc = ref.usersCriteria2

    if(uc !== null) {
      let criteria:Criteria[] = []
      for (let id of uc.criteriaList)
        criteria.push(this.criterias.at(this.critind[id])!)
      return criteria
    }
    return []
  }

  selectedCriteria(event: MatAutocompleteSelectedEvent, ref: Reference, numD: number): void {
    console.log('selected',event.option.value)
    let critCtrl = this.getCriteriaCtrl(numD)
    if (numD == 1) {
      console.log(this.crit1.at(this.refind[ref.idProjRef]).getRawValue())
      this.crit1Input.nativeElement.value = '';
      ref.usersCriteria1.criteriaList.push(Number(event.option.value));
      
      console.log(ref.usersCriteria1.criteriaList)
    }
    else {
      ref.usersCriteria2.criteriaList.push(Number(event.option.value));
      //this.crit2Input.nativeElement.value = '';
    
    }
    critCtrl.at(this.refind[ref.idProjRef]).setValue(null);
  }

  addUserDesignationCriteria(uc: UserDesignation) {
    this.dataService.updateReferenceUserDesignationCriteria(this.idProject, uc).subscribe({
      next: (value) => {
          //this.referencesUpdated.emit()
      }
    })
  }

  updateCriteria1(ref: Reference) {
    if (this.critEnabled1.at(this.refind[ref.idProjRef]).enabled) {
      ref.usersCriteria1.processed = true
      this.addUserDesignationCriteria(ref.usersCriteria1)
      this.critEnabled1.at(this.refind[ref.idProjRef]).disable()
    }
    else {
      if(ref.usersCriteria1 !== null) {
        ref.usersCriteria1.processed = false
        this.addUserDesignationCriteria(ref.usersCriteria1)
        this.critEnabled1.at(this.refind[ref.idProjRef]).enable()
      }
      
    }

  }

  updateCriteria2(ref: Reference) {
    if (this.critEnabled2.at(this.refind[ref.idProjRef]).enabled) {
      ref.usersCriteria2.processed = true
      this.addUserDesignationCriteria(ref.usersCriteria2)
      this.critEnabled2.at(this.refind[ref.idProjRef]).disable()
    }
    else {
      if(ref.usersCriteria2 !== null) {
        ref.usersCriteria2.processed = false
        this.addUserDesignationCriteria(ref.usersCriteria2)
        this.critEnabled2.at(this.refind[ref.idProjRef]).enable()
      }
      
    }

  }

  private getUserCtrl(numD: number): FormArray {
    return   numD === 1? this.usr1 : this.usr2
  }

  private getCriteriaCtrl(numD: number): FormArray {
    return   numD === 1? this.crit1 : this.crit2
  }

  removeCri(ref: Reference, critId: number, numD: number) {
    let uc
    numD == 1? uc = ref.usersCriteria1: uc = ref.usersCriteria2
    const index = uc.criteriaList.indexOf(critId)

    if (index >= 0) {
      uc.criteriaList.splice(index, 1);
      //this.addUserDesignationCriteria(ref.usersCriteria1)
    }

  }

  getState(ref: Reference, numD: number): string {
    let uc
    numD == 1? uc = ref.usersCriteria1: uc = ref.usersCriteria2

    if (uc !== null) {
      if (uc.processed) {
        if (uc.criteriaList.length > 0) return 'out'
        else return 'in'
      }
      else return 'undiecided'
    }
    return 'unassigned'

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
  //deprecated
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

