import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { User } from '../dataModels/user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  constructor(public dialogRef: MatDialogRef<LoginComponent>) {}

  username = new FormControl()
  password = new FormControl()
  hide = true

  close() {
    let user : User = {
      username: this.username.value,
      password: this.password.value
    }
    let data = {
      user: user
    }
    this.dialogRef.close(data)
  }

}
