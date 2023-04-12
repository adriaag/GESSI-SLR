import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Criteria } from '../dataModels/criteria';
import { Exclusion } from '../dataModels/exclusion';
import { Reference } from '../dataModels/reference';

@Component({
  selector: 'app-reference-classify',
  templateUrl: './reference-classify.component.html',
  styleUrls: ['./reference-classify.component.css']
})
export class ReferenceClassifyComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any, public dialogRef: MatDialogRef<ReferenceClassifyComponent>) {}

  reference: Reference = this.data.reference
  exclusionCriteria: Criteria[] = this.data.exclusionCriteria
  type = new FormControl(this.reference.state)
  
  idExc = getIDs(this.reference.exclusionDTOList)
  selectedEC = new FormControl({value: this.idExc, disabled: this.reference.state !== "out"})

  stateChanged() {
    if(this.type.value === "out") {
      this.selectedEC.enable()
    }
    else {
      this.selectedEC.disable()
    }

  }

  close() {
    let criteriaIds: number[] = []
    if (this.type.value === "out") {
      criteriaIds = this.selectedEC.value!
    }
    let data = {
      type: this.type.value,
      criteria: criteriaIds
    }
    this.dialogRef.close(data)
  }
  
}
function getIDs(exclusionList: Exclusion[]) {
  var ids: number[] = []
  if(exclusionList !== null) {
    for(let exc of exclusionList){
      ids.push(exc.idICEC)
    }
  }
  return ids
}

