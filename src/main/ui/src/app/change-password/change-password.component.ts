import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent {

  constructor(private activatedRoute: ActivatedRoute, private router: Router, private authService: AuthenticationService){}

  password = new FormControl();
  password2 = new FormControl();
  hide = true
  hide2 = true
  token = ""

  close() {
    if (this.password.value !== this.password2.value) {
      alert("Passwords don't match")
      this.password2.reset()
      this.password.reset()  
    }
    else {
      this.changePassword()
    }
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
        this.token = params['token'];
      }
    );
  }

  changePassword() {
    this.authService.changePassword(this.token, this.password.value).subscribe({
      next:(data) => {
        this.router.navigate([''])
      }
    })


  }
}
