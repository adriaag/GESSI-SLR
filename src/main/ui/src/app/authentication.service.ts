import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap, throwError } from 'rxjs';
import { catchError } from 'rxjs/internal/operators/catchError';
import { environment } from 'src/environments/environment';
import { User } from './dataModels/user';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  rootUrl: string = environment.authUrl;
  token: string | null = null

  constructor(private http: HttpClient) { }


  userAuthenticated(): boolean {
    return this.token !== null
  }

  getToken(): string|null {
    return this.token

  }

  login(user: User) {
    return this.http.post(`${this.rootUrl}/login`,user,{ headers: new HttpHeaders(), observe: 'response' })
    .pipe(
      tap(data => {
        this.token = data.headers.get('Authorization')
        console.log(this.token)
      }),
      catchError(this.handleError),
    )
  }

  logout() {
    /*return this.http.post(`${this.rootUrl}/logout`,{ headers: new HttpHeaders()})
    .pipe(
      tap(data => {
        console.log("Logout")
      }),
      catchError(this.handleError)
    )*/

    this.token = null
  }

  changePasswordRequest(username: string) {
    const formData: FormData = new FormData();
    formData.append('username', username)
    return this.http.post(`${this.rootUrl}/changePasswordRequest`,formData,{ headers: new HttpHeaders()})
    .pipe(
      tap(data => {
        console.log('Data:',data)
      }),
      catchError(this.handleError)
    )

  }

  changePassword(token: string, newPassword: string ) {
    const formData: FormData = new FormData();
    formData.append('token', token)
    formData.append('password', newPassword)
    return this.http.put(`${this.rootUrl}/password`,formData,{ headers: new HttpHeaders()})
    .pipe(
      tap(data => {
        console.log('Data:',data)
      }),
      catchError(this.handleError)
    )
  }

  private handleError(error: HttpErrorResponse): Observable<any> {
    switch (error.status){
      case 0:
        alert("Database not reacheable")
        break;
      case 400:
        alert("Bad request:\n"+error.error.message)
        break;
      case 401:
        alert("Username or password not correct")
        break;
      case 403:
        alert("Request not allowed")
        break;
      case 404:
        alert("Resource not found")
        break;
      case 409:
        alert("Entity already exists")
        break;
      case 500:
        alert("Internal server error")
        break;
      default:
        alert("Unkown error")
    }
    return throwError(() => error.status);
  }

  

}
