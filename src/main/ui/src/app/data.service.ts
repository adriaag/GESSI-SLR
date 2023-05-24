import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Project } from './dataModels/project';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Reference } from './dataModels/reference';
import { ImportError } from './dataModels/importError';
import { ReferenceFromFileResponse } from './dataModels/referenceFromFileResponse';
import { CriteriaResponse } from './criteriaResponse';
import { environment } from 'src/environments/environment';
import { AddReference } from './dataModels/addReference';
import { UserDesignation } from './dataModels/userDesignation';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  rootUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(
      this.rootUrl+"/projects", this.setHttpHeader())
      .pipe(
        //tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  createProject(nameProject: string): Observable<Project> {
    const formData: FormData = new FormData();
    formData.append('name', nameProject);
    return this.http.post<Project>(
      `${this.rootUrl}/projects`, formData,this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  deleteProject(idProject: number): Observable<{message: string}> {
    return this.http.delete<{message: string}>(
      `${this.rootUrl}/projects/${idProject}`,this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  getReferences(idProject: number): Observable<Reference[]> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/projects/${idProject}/references`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  updateReferenceUserDesignation(idRef: number, idProject: number, username: string, numDesignation: number): Observable<UserDesignation> {
    const formData: FormData = new FormData();
    formData.append('numDesignation',String(numDesignation));
    formData.append('username', username);

    return this.http.put<UserDesignation>(`${this.rootUrl}/projects/${idProject}/references/${idRef}/userDesignations`,formData,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  updateReferenceUserDesignationCriteria(idProject: number, userDesignation: UserDesignation): Observable<{message: string}> {
    const formData: FormData = new FormData();
    formData.append('username', userDesignation.username);
    formData.append('processed', String(userDesignation.processed));
    formData.append('criteriaList', String(userDesignation.criteriaList));
    return this.http.put<{message: string}>(`${this.rootUrl}/projects/${idProject}/references/${userDesignation.idRef}/userDesignations/${userDesignation.numDesignation}`,formData,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  deleteReference(idRef: number, idProject: number): Observable<string> {
    return this.http.delete<string>(
      `${this.rootUrl}/projects/${idProject}/references/${idRef}`,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  getExcelFile(idProject: number): Observable<Blob> {
    return this.http.get(
      `${this.rootUrl}/projects/${idProject}/export/references`,{responseType: 'blob'})
      .pipe(
        catchError(this.handleError)
      )
  }

  getErrors(idProject: number): Observable<ImportError[]> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/projects/${idProject}/errors`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  getDLNames(): Observable<String[]> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/digitalLibraries`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  createReferenceFromFile(idProject: string, idDl: string, file: File): Observable<ReferenceFromFileResponse> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('dlNum',idDl);
    return this.http.post<ReferenceFromFileResponse>(
      `${this.rootUrl}/projects/${idProject}/references`,formData,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  createReferenceFromForm(refData: AddReference, idProject: number): Observable<Reference> {
    return this.http.post<ReferenceFromFileResponse>(
      `${this.rootUrl}/projects/${idProject}/manualreferences`,refData,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  getCriteria(idProject: number): Observable<CriteriaResponse> {
    return this.http.get<CriteriaResponse>(
      `${this.rootUrl}/projects/${idProject}/criterias`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  createCriteria(name: string, text: string, type: string, idProject: number): Observable<{message: string}> {
    const formData: FormData = new FormData();
    formData.append('name', name);
    formData.append('text', text);
    formData.append('type', type);
    return this.http.post<{message: string}>(
      `${this.rootUrl}/projects/${idProject}/criterias`,formData,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  updateCriteria(id: number, name: string, text: string, type: string, idProject: number): Observable<string> {
    const formData: FormData = new FormData();
    formData.append('name', name);
    formData.append('text', text);
    formData.append('type', type);
    return this.http.put<string>(
      `${this.rootUrl}/projects/${idProject}/criterias/${id}`,formData,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  deleteCriteria(id: number, idProject:number): Observable<string> {
    return this.http.delete<string>(
      `${this.rootUrl}/projects/${idProject}/criterias/${id}`,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  editReferenceCriteria(idRef: number, idProject: number, criteria: number[]) {
    const formData: FormData = new FormData();
    formData.append('criteria', String(criteria));
    return this.http.put<string>(
      `${this.rootUrl}/projects/${idProject}/references/${idRef}`,formData,this.setHttpHeader())
    .pipe(
      tap(data => console.log("Data:", data)),
      catchError(this.handleError),
    )
  }

  getUsernames(): Observable<string[]> {
    return this.http.get<string[]>(
      `${this.rootUrl}/users`,this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  deleteDatabase(): Observable<{message: string}> {
    return this.http.delete<{message: string}>(
      `${this.rootUrl}/`,this.setHttpHeader())
      .pipe(
        tap(data => console.log("Data:", data)),
        catchError(this.handleError),
      )
  }

  private setHttpHeader() {
    return {headers: new HttpHeaders()};
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
        alert("Login required to perform this action")
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