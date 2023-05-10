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
  rootUrl: string = environment.apiUrl;
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
      }),
      catchError(this.handleError),
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
