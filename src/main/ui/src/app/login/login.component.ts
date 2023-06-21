import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthenticationService } from '../authentication.service';
import { User } from '../dataModels/user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  constructor(public dialogRef: MatDialogRef<LoginComponent>, private authSerivce: AuthenticationService) {
    dialogRef.disableClose = true;
  }

  username = new FormControl()
  password = new FormControl()
  hide = true

  forgotPassword() {
    if (this.username.value === null) {
      alert("Please, enter your username in order to recover your password")
    }
    else {
      if(confirm("In order to change your password, an email will"+
      " be sent to your email with more instructions.\nProceed?")){
        this.changePasswordRequest()
      }
    }
  }

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

  changePasswordRequest() {
    this.authSerivce.changePasswordRequest(this.username.value).subscribe({
      next: (data) => {
        alert("Message sent. Please, check your mailbox")
      }
    })
  }

}
