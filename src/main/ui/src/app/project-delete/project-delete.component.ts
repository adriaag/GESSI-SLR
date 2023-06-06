import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { FormArray, FormControl } from '@angular/forms';
import { merge, Observable, of } from 'rxjs';
import { DataService } from '../data.service';
import { Project } from '../dataModels/project';

@Component({
  selector: 'app-project-delete',
  templateUrl: './project-delete.component.html',
  styleUrls: ['./project-delete.component.css']
})
export class ProjectDeleteComponent {
  @Input('project') selectedProject! : Project;
  @Input('usernames') usernames! : string[];
  @Input('digitalLibraries') digitalLibraries!: String[];
  @Input('trigger') trigger! : Boolean;
  @Output() projectDeleted = new EventEmitter();
  @Output() projectUpdated = new EventEmitter();

  nameMaxLength = 1000
  topicMaxLength = 500
  researchQuestionMaxLength = 5000
  protocolMaxLength = 5000
  commentsMaxLength = 5000
  infoMaxLength = 5000
  searchStringMaxLength = 5000


  name: FormControl = new FormControl()
  topic: FormControl = new FormControl()
  researchQuestion: FormControl = new FormControl()
  protocol: FormControl = new FormControl()
  involvedUsersUsername: FormArray = new FormArray<FormControl>([])
  involvedUsersInfo: FormArray = new FormArray<FormControl>([])
  dlId: FormArray = new FormArray<FormControl>([])
  dlSearchString: FormArray = new FormArray<FormControl>([])
  dlNumResults: FormArray = new FormArray<FormControl>([])
  comments: FormControl = new FormControl()
  protocolImgUpd!: File;
  protocolImg!: Blob;
  protocolImgUrl: string = "";
  imageToShow: any
  userDirty: { [index: number] : string} = {}
  dlIndex: { [name: string]: number} = {}

  selectionUser: boolean[] = []
  

  filteredUsernames: Observable<string[]> = new Observable<string[]>
  filteredDLs: Observable<String[]> = new Observable<String[]>

  constructor(private dataService: DataService) {}

  operationMsg: string = ""
  operationMsg2: string = ""
  operationMsg3: string = ""
  loading: boolean = false


  ngOnChanges(changes: SimpleChanges) {
    console.log('changes',changes)
    this.involvedUsersUsername = new FormArray<FormControl>([])
    this.involvedUsersInfo = new FormArray<FormControl>([])
    this.dlId = new FormArray<FormControl>([])
    this.dlSearchString = new FormArray<FormControl>([])
    this.dlNumResults= new FormArray<FormControl>([])
    this.selectionUser = []
    this.name.setValue(this.selectedProject.name)
    this.topic.setValue(this.selectedProject.topic)
    this.researchQuestion.setValue(this.selectedProject.researchQuestion)
    this.protocol.setValue(this.selectedProject.protocol)
    this.comments.setValue(this.selectedProject.comments)
    this.dataService.getProjectProtocolImg(this.selectedProject.id).subscribe({
      next: (data: Blob) => {
      this.protocolImg = data
      this.imageToShow = URL.createObjectURL(this.protocolImg);
      console.log(data)
      }
    })

    for (let user of this.selectedProject.involvedUsers) {
      this.involvedUsersUsername.push(new FormControl(user.username))
      this.involvedUsersInfo.push(new FormControl(user.involveInfo))
      this.selectionUser.push(false)
    }

    let ind = 1
    for (let name of this.digitalLibraries) {
      this.dlIndex[String(name)] = ind
      ++ind
    }
    for( let dl of this.selectedProject.digitalLibraries) {
      this.dlId.push(new FormControl(dl.idDL))
      this.dlSearchString.push(new FormControl(dl.searchString))
      this.dlNumResults.push(new FormControl(dl.numSearchResults))
    }

    merge(...this.involvedUsersUsername.controls.map(control => control.valueChanges))
    .subscribe((value) => {
      console.log('usuaris',value)
      this.filteredUsernames = of(this.filtreUsr(value))
    })

    this.filteredDLs = of(this.digitalLibraries.filter(option => !this.dlSelected(this.dlIndex[String(option)])))
  }

  deleteProjectConfirm() {
    let message: string = "Are you sure you wanna delete " + this.selectedProject.name + " ?\nNote: This action cannot be undone"
    if(confirm(message)) {   
      this.deleteProject()
    }
  }

  deleteDBConfirm() {
    let message: string = "Are you sure you want to recreate the database?\nCAUTION!: All stored data will be lost!"
    if (confirm(message)) {
      this.deleteDB()
    }
  }

  deleteProject() {
    this.dataService.deleteProject(this.selectedProject.id).subscribe((resposta)=> {
      this.operationMsg3 = "Project successfully deleted"
      this.projectDeleted.emit()
    })
  }
  
  deleteDB() {
      this.dataService.deleteDatabase().subscribe((resposta)=> {
        this.operationMsg2 = "Database successfully deleted"
        this.projectDeleted.emit()
      })

  }

  updateProject() {
    let projectUpd = this.selectedProject
    projectUpd.name = this.name.value
    projectUpd.topic = this.topic.value
    projectUpd.researchQuestion = this.researchQuestion.value
    projectUpd.protocol = this.protocol.value
    projectUpd.comments = this.comments.value
    projectUpd.involvedUsers = this.getInvolvedUsers()
    projectUpd.digitalLibraries = this.getDigitalLibraries()
    
    this.loading = true
    this.dataService.updateProject(projectUpd).subscribe({
      next: (value) => {
        //this.projectUpdated.emit(value)
        if (this.protocolImgUpd != null)
          this.dataService.updateProjectProtocolImg(this.selectedProject.id,this.protocolImgUpd).subscribe({
            next: (data) => {
              this.imageToShow = URL.createObjectURL(this.protocolImgUpd);
              this.loading = false
              this.operationMsg = "Project data successfully updated"
            },
            error: (error) => {
              this.loading = false
              this.operationMsg = "Error updating project"
            }
          })
        else {
          this.loading = false
          this.operationMsg = "Project data successfully updated"
        }
        
      },
      error: (error) => {
        this.loading = false
        this.operationMsg = "Error updating project"
      }
    })

  }

  private getInvolvedUsers(): {idProject: number, username: string, involveInfo: string}[] {
    let users = []
    for (let i = 0; i < this.involvedUsersInfo.length; ++i) {
      users.push({
        idProject: this.selectedProject.id,
        username: this.involvedUsersUsername.at(i).value,
        involveInfo: this.involvedUsersInfo.at(i).value
      })
    }
    return users;

  }

  
  getDigitalLibraries(): {idProject: number, idDL: number,searchString: string, numSearchResults: number}[]{
    let dls = []
    for (let i = 0; i < this.dlId.length; ++i) {
      dls.push({
        idProject: this.selectedProject.id,
        idDL: this.dlId.at(i).value,
        searchString: this.dlSearchString.at(i).value,
        numSearchResults: this.dlNumResults.at(i).value
      })
    }
    return dls;
  }

  handleFileInput(event: Event) {
    const target= event.target as HTMLInputElement;
    this.protocolImgUpd = (target.files)![0]
    this.imageToShow = URL.createObjectURL(this.protocolImgUpd);
  };

  addUser() {
    this.involvedUsersInfo.push(new FormControl())
    this.involvedUsersUsername.push(new FormControl())
    merge(...this.involvedUsersUsername.controls.map(control => control.valueChanges))
    .subscribe((value) => {
      console.log('usuaris',value)
      this.filteredUsernames = of(this.filtreUsr(value))
    })

  }
  deleteUser(index: number) {
    this.involvedUsersInfo.removeAt(index)
    this.involvedUsersUsername.removeAt(index)
  }

  changeUser(index: number) {
    console.log('canvis',this.involvedUsersUsername.at(index).value)
    this.userDirty[index] = this.involvedUsersUsername.at(index).value
    this.selectionUser[index] = true
  }

  filtreUsr(value: string): string[] {
    if (value !== null) {
      const filterValue = value.toLowerCase();
      return this.usernames.filter(option => 
        option.toLowerCase().includes(filterValue)
        && !this.userSelected(option))
    }

    return this.usernames.filter(option => !this.userSelected(option))
  }

  resetFilter(index: number) {
    this.userDirty[index] = this.involvedUsersUsername.at(index).value
    this.involvedUsersUsername.at(index).setValue('')
    this.filteredUsernames = of(this.usernames.filter(option => !this.userSelected(option)))
  }

  removeCtrlValue(index: number, mode: number) {
    this.filteredUsernames.subscribe({
      next: (filter) => {
        if ((mode === 0 && filter.length === 0) || (mode === 1 && filter.length > 0) || (mode === 1 && !this.selectionUser[index]) ) {
          let username = this.userDirty[index]
          this.userDirty[index] = ''
          if(username !== '' && username != undefined) 
            this.involvedUsersUsername.at(index).setValue(username)

          //else this.involvedUsersUsername.at(index).setValue('')     
        }

      }
    })
  }

  userSelected(username: string): boolean {
    for (let ctrl of this.involvedUsersUsername.controls) {
      if (ctrl.value === username) return true
    }
    return false

  }

  dlSelected(idDL: number): boolean {
    for (let ctrl of this.dlId.controls) {
      if (Number(ctrl.value) === idDL) return true
    }
    return false

  }

  addDL() {
    this.dlId.push(new FormControl())
    this.dlSearchString.push(new FormControl())
    this.dlNumResults.push(new FormControl())
  }

  
  deleteDL(index: number) {
    this.dlId.removeAt(index)
    this.dlSearchString.removeAt(index)
    this.dlNumResults.removeAt(index)
  }



  getValidDL() {
    console.log('validDL',this.digitalLibraries)
    this.filteredDLs = of(this.digitalLibraries.filter(option => !this.dlSelected(this.dlIndex[String(option)])))
    this.filteredDLs.subscribe((vlaue)=>{console.log('canvis',vlaue)})
  }

  getLength(controller: any) {
    if (controller.value === null) return 0
    else return controller.value.length
  }

  
}
