import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-project-create',
  templateUrl: './project-create.component.html',
  styleUrls: ['./project-create.component.css']
})
export class ProjectCreateComponent {
  constructor(public dialogRef: MatDialogRef<ProjectCreateComponent>) {}

  projectName = new FormControl()

  close(projectName: string): void {
    console.log(projectName)
    this.dialogRef.close(projectName)
  }

}
