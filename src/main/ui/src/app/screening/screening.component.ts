import { Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormArray, FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable, SortDirection } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { merge, Observable, of} from 'rxjs';
import { DataService } from '../data.service';
import { Criteria } from '../dataModels/criteria';
import { ConsensusCriteria } from '../dataModels/consensusCriteria';
import { Reference } from '../dataModels/reference';
import { UserDesignation } from '../dataModels/userDesignation';

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
  @Input('trigger') trigger!: boolean //Pensat per tal que s'executi ngOnChanges quan
                                      //es modifiquin els inputs. Angular compara els 
                                      //inputs per referència. 

  criterias: Criteria[] = [];

  references: Reference[] = [];
  sortedData: Reference[] = [];
  dataSource!: MatTableDataSource<Reference>;
  displayedColumns: string[] = ['ref','tit', 'usr1', 'sta1','icec1','usr2','sta2','icec2', 'cons','disc','consDes'];
  filterValue: string = ""

  sortColumn: string = ""
  sortDirection: SortDirection = ""

  refind: { [id: number] : number; } = {}
  critind: { [id: number] : number; } = {}
  userDirty: { [idProjRef: number] : {usr1: string, usr2: string} } = {}
  
  crit1: FormArray = new FormArray<FormControl>([])
  critEnabled1: FormArray = new FormArray<FormControl>([])
  usr1:  FormArray = new FormArray<FormControl>([])
  crit2: FormArray = new FormArray<FormControl>([])
  critEnabled2: FormArray = new FormArray<FormControl>([])
  usr2:  FormArray = new FormArray<FormControl>([])

  critDisc: FormArray = new FormArray<FormControl>([])
  critDiscEnabled: FormArray = new FormArray<FormControl>([])
  
  
  focusedCriteria: number[] = []
  focusedUserAlt: string = ''
  filteredOptionsUsr: Observable<string[]> = new Observable<string[]>
  filteredOptionsCrit: Observable<Criteria[]> = new Observable<Criteria[]>
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('crit1Input') crit1Input!: ElementRef<HTMLInputElement>;
  @ViewChild('crit2Input') crit2Input!: ElementRef<HTMLInputElement>;
  @ViewChild('crit3Input') crit3Input!: ElementRef<HTMLInputElement>;

  constructor(private dataService: DataService, private dialog: MatDialog) {}

  ngOnChanges(changes: SimpleChanges) {
    this.references = this.referenceslist
    this.criterias = this.exclusionCriteria.concat(this.inclusionCriteria)
    this.uploadUsr1()
    this.sortedData = this.references.slice();
    this.dataSource = new MatTableDataSource(this.referenceslist);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortData = this.sortData();
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = this.filterData();

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

    merge(...this.critDisc.controls.map(control => control.valueChanges))
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
    this.applyFilterWhenReloading()
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
    this.userDirty = {};

    var ind = 0
    var f
    var c
    var cEnable1
    var cEnable2
    var cEnable3

    for (let ref of this.referenceslist) {
      cEnable1 = new FormControl()
      if (ref.usersCriteria1 === null) {
        f = new FormControl()
        c = new FormControl()
        cEnable1.disable()
      }
      else {
        f = new FormControl(ref.usersCriteria1.username)
        c = new FormControl(ref.usersCriteria1.criteriaList)
        if (ref.usersCriteria1.processed) cEnable1.disable()
      } 
      this.usr1.push(f)
      this.crit1.push(c)
      this.critEnabled1.push(cEnable1)

      cEnable2 = new FormControl()
      if (ref.usersCriteria2 === null) {
        f = new FormControl()
        c = new FormControl()
        cEnable2.disable()
      }
      else {
        f = new FormControl(ref.usersCriteria2.username)
        c = new FormControl(ref.usersCriteria2.criteriaList)
        if (ref.usersCriteria2.processed) cEnable2.disable()
      }
      this.usr2.push(f)
      this.crit2.push(c)
      this.critEnabled2.push(cEnable2)

      cEnable3 = new FormControl()

      this.critDisc.push(new FormControl(ref.exclusionDTOList))

      if(ref.consensusCriteriaProcessed) cEnable3.disable()
      let state = this.getConsensusState(ref)
      if(state === '' || state === "YES") cEnable3.disable()
      this.critDiscEnabled.push(cEnable3)

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
    console.log(this.focusedUserAlt, 'Focused user filtre')
    if (value !== null) {
      const filterValue = value.toLowerCase();
      return this.usernames.filter(option => 
        option.toLowerCase().includes(filterValue)
        && option != this.focusedUserAlt)
    }

    return this.usernames.filter(option => option != this.focusedUserAlt)
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
          console.log('uc actualitzat')
          
        }
      })
  }

  resetFilter(idProjRef: number, numD : number) {
    console.log('reset filter')
    let usrCtrl: FormArray = this.getUserCtrl(numD)
    let udAlt: UserDesignation = this.getUserDesignation(this.refind[idProjRef],3-numD)
    
    let username = usrCtrl.at(this.refind[idProjRef]).value
    if(this.userDirty[idProjRef] !== undefined) {
      if(numD === 1) this.userDirty[idProjRef].usr1 = username
      else this.userDirty[idProjRef].usr2 = username
    }
    else {
      if(numD === 1) this.userDirty[idProjRef] = {usr1: username, usr2: ''}
      else this.userDirty[idProjRef] = {usr1: '', usr2: username}
    }
    
    usrCtrl.at(this.refind[idProjRef]).setValue('')
    if(udAlt !== null){
      this.filteredOptionsUsr = of(this.usernames.filter(option => option != udAlt.username))
      this.focusedUserAlt = udAlt.username
    }
    else {
      this.filteredOptionsUsr = of(this.usernames)
      this.focusedUserAlt = ''
    }
    
    //val !== null? this.filteredOptionsUsr1 = of(this.filtreUsr(val)): this.filteredOptionsUsr1 = of(this.usernames)
  }

  removeCtrlValue(idProjRef: number, numD: number) {
    this.filteredOptionsUsr.subscribe({
      next: (filter) => {
        if (filter.length === 0) {
          let usrCtrl = this.getUserCtrl(numD)
          let usernames = this.userDirty[idProjRef]
          if (usernames !== undefined) {
            let username
            numD === 1? username = usernames.usr1 : username = usernames.usr2 
            if(username !== '' && typeof username === "string") {
              usrCtrl.at(this.refind[idProjRef]).setValue(username)
            }
            else usrCtrl.at(this.refind[idProjRef]).setValue('')
          }
          else usrCtrl.at(this.refind[idProjRef]).setValue('')               
        }
      }
    })
  }

  recoverUsername(idProjRef: number, numD: number) {
    console.log(this.filteredOptionsUsr)
    this.filteredOptionsUsr.subscribe({
      next: (filter) => {
        if (filter.length > 0) {
          let usrCtrl = this.getUserCtrl(numD)
          let usernames = this.userDirty[idProjRef]
          if (usernames !== undefined) {
            let username
            numD === 1? username = usernames.usr1 : username = usernames.usr2 
            if(username !== '' && typeof username === "string") {
              usrCtrl.at(this.refind[idProjRef]).setValue(username)
            }
            else usrCtrl.at(this.refind[idProjRef]).setValue('')
          }
          else usrCtrl.at(this.refind[idProjRef]).setValue('')   
        }
        
      }
    })

  }

  resetFilterCriteria(usersCriteria : UserDesignation) {
    if (usersCriteria !== null) {
      this.filteredOptionsCrit = of(this.criterias.filter((option) => !usersCriteria.criteriaList.includes(option.id)))
      this.focusedCriteria = usersCriteria.criteriaList
    }
    else {
      this.filteredOptionsCrit = of(this.criterias)
    }
  }

  resetFilterCriteriaDecision(consCrit: ConsensusCriteria) {
    if (consCrit !== null) {
      this.filteredOptionsCrit = of(this.criterias.filter((option) => !consCrit.idICEC.includes(option.id)))
      this.focusedCriteria = consCrit.idICEC
    }
    else {
      this.filteredOptionsCrit = of(this.criterias)
    }

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

  getCriteriaNamesDecision(ref: Reference) {
    if (ref.exclusionDTOList !== null) {
      let criteria: Criteria[] = []
      for (let conCrit of ref.exclusionDTOList.idICEC)
        criteria.push(this.criterias.at(this.critind[conCrit])!)
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
      this.crit2Input.nativeElement.value = '';
    
    }
    critCtrl.at(this.refind[ref.idProjRef]).setValue(null);
  }

  selectedCriteriaDecision(event: MatAutocompleteSelectedEvent, ref: Reference) {
    this.crit3Input.nativeElement.value = '';
    ref.exclusionDTOList.idICEC.push(event.option.value);
    this.critDisc.at(this.refind[ref.idProjRef]).setValue(null);

  }

  addUserDesignationCriteria(uc: UserDesignation, idProjRef: number) {
    this.dataService.updateReferenceUserDesignationCriteria(this.idProject, uc).subscribe({
      next: (value) => {
        let state = this.getConsensusState(this.references.at(this.refind[idProjRef])!)
        if(state === "NO" || state == "PARTLY")
          this.critDiscEnabled.at(this.refind[idProjRef]).enable()
      }
    })
  }

  addConsensusCriteria(cc: ConsensusCriteria) {
    this.dataService.editReferenceCriteria(cc.idRef, this.idProject, cc.idICEC).subscribe({
      next: (value)=> {

      }
    })

  }

  updateCriteria1(ref: Reference) {
    if (this.critEnabled1.at(this.refind[ref.idProjRef]).enabled) {
      ref.usersCriteria1.processed = true
      this.critEnabled1.at(this.refind[ref.idProjRef]).disable()
      this.critDisc.at(this.refind[ref.idProjRef]).setValue(null)
      ref.exclusionDTOList.idICEC = []
      ref.consensusCriteriaProcessed = false
    }
    else {
      if(ref.usersCriteria1 !== null) {
        ref.usersCriteria1.processed = false     
        this.critEnabled1.at(this.refind[ref.idProjRef]).enable()
        this.critDiscEnabled.at(this.refind[ref.idProjRef]).disable()
      }
      
    }
    this.addUserDesignationCriteria(ref.usersCriteria1, ref.idProjRef)

  }

  updateCriteria2(ref: Reference) {
    if (this.critEnabled2.at(this.refind[ref.idProjRef]).enabled) {
      ref.usersCriteria2.processed = true     
      this.critEnabled2.at(this.refind[ref.idProjRef]).disable()
      this.critDisc.at(this.refind[ref.idProjRef]).setValue(null)
      ref.exclusionDTOList.idICEC = []
      ref.consensusCriteriaProcessed = false
    }
    else {
      if(ref.usersCriteria2 !== null) {
        ref.usersCriteria2.processed = false
        this.critEnabled2.at(this.refind[ref.idProjRef]).enable()
        this.critDiscEnabled.at(this.refind[ref.idProjRef]).disable()
      }
      
    }
    this.addUserDesignationCriteria(ref.usersCriteria2, ref.idProjRef)

  }

  updateCriteriaDecision(ref: Reference) {
    if (this.critDiscEnabled.at(this.refind[ref.idProjRef]).enabled) {
      ref.consensusCriteriaProcessed = true
      this.critDiscEnabled.at(this.refind[ref.idProjRef]).disable()
    }
    else {
      if(ref.exclusionDTOList !== null) 
      ref.consensusCriteriaProcessed = false
        this.critDiscEnabled.at(this.refind[ref.idProjRef]).enable()
    }
    this.addConsensusCriteria(ref.exclusionDTOList)

  }

  private getUserCtrl(numD: number): FormArray {
    return   numD === 1? this.usr1 : this.usr2
  }

  private getUserDesignation(id:number, numD: number): UserDesignation {
    return   numD === 1? this.referenceslist[id].usersCriteria1 : this.referenceslist[id].usersCriteria2
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

  removeCriDecision(ref: Reference, critId: number) {
    const index = ref.exclusionDTOList.idICEC.indexOf(critId)

    if (index >= 0) {
      ref.exclusionDTOList.idICEC.splice(index, 1);
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

  getConsensusState(ref: Reference): string {
    let sta1 = this.getState(ref, 1)
    let sta2 = this.getState(ref, 2)

    if ((sta1 === 'in' || sta1 == 'out') && (sta2 === 'in' || sta2 === 'out')) {
      if (sta1 === sta2) {
        if (sta1 == 'in') return 'YES'
        else {
          let clist1 = ref.usersCriteria1.criteriaList
          let clist2 = ref.usersCriteria2.criteriaList
          if(compareArrays(clist1,clist2))
            return 'YES'
          else return 'PARTLY'
        }

      }
      else {
        return 'NO'
      }

    }
    return ''

  }

  getFinalDecision(ref: Reference) {
    let consState = this.getConsensusState(ref)
    if (consState === "YES") return this.getState(ref,1).toUpperCase()
    if (ref.consensusCriteriaProcessed){
      if(ref.exclusionDTOList.idICEC.length > 0) return 'OUT'
      else return 'IN'
    }
    return ''

  }

  //possible optimització precomputant-ho quan es carreguen les dades
  filterData() {
    let filterFunction = 
        (data: Reference, filter: string): boolean => {
          if (filter) {
            //obtenim una string amb totes les dades de reference que apareixen a la taula 
            var filterText = ""
            if(data.idProjRef !== null)filterText += data.idProjRef;              
            if(data.art.title !== null)filterText += data.art.title;          
            if(data.art.abstractA !== null)filterText += data.art.abstractA
            if(data.usersCriteria1 !== null)filterText += data.usersCriteria1.username
            if(data.usersCriteria2 !== null)filterText += data.usersCriteria2.username
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
      if(this.sort !== undefined) {
        this.sortColumn = sort.active
        this.sortDirection = sort.direction
      }
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
          case 'abs':
            return compare(String(a.art.abstractA),String(b.art.abstractA), isAsc);
          case 'usr1':
            return compareUc(a.usersCriteria1,b.usersCriteria1, isAsc)
          case 'sta1':
            return compare(this.getState(a,1),this.getState(b,1), isAsc)
          case 'usr2':
              return compareUc(a.usersCriteria2,b.usersCriteria2, isAsc)
          case 'sta2':
              return compare(this.getState(a,2),this.getState(b,2), isAsc)
          case 'cons':
              return compare(this.getConsensusState(a), this.getConsensusState(b), isAsc)
          case'consDes':
              return compare(this.getFinalDecision(a), this.getFinalDecision(b), isAsc)
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

function compareArrays(a: number[], b: number[]) {
  if (a.length != b.length) return false
  let dic : { [id: number] : boolean } = {}
  for(let num of a) dic[num] = true
  for(let num of b) 
    if (dic[num] === undefined) return false
  
  return true
}

function compareUc(a: UserDesignation, b: UserDesignation, isAsc: boolean) {
  let usr1 = a === null? null: a.username
  let usr2 = b === null? null: b.username
  return compare(usr1,usr2,isAsc)
}


