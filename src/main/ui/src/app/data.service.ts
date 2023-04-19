import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Project } from './dataModels/project';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Reference } from './dataModels/reference';
import { ImportError } from './dataModels/importError';
import { ReferenceFromFileResponse } from './dataModels/referenceFromFileResponse';
import { CriteriaResponse } from './criteriaResponse';
import { environment } from 'src/environments/environment';

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
      `${this.rootUrl}/projects`, formData)
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  deleteProject(idProject: number): Observable<{message: string}> {
    return this.http.delete<{message: string}>(
      `${this.rootUrl}/projects/${idProject}`)
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  getReferences(idProject: number): Observable<Reference[]> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/references?idProject=${idProject}`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  deleteReference(idRef: number): Observable<string> {
    return this.http.delete<string>(
      `${this.rootUrl}/references/${idRef}`)
    .pipe(
      tap(data => console.log("Anlagenstatus Daten:", data)),
      catchError(this.handleError),
    )
  }

  /*getReference(idReference: number): Observable<Reference> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/reference?idReference=${idReference}`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }*/

  getExcelFile(idProject: number): Observable<Blob> {
    return this.http.get(
      `${this.rootUrl}/download?idProject=${idProject}`,{responseType: 'blob' })
      .pipe(
        catchError(this.handleError)
      )
  }

  getErrors(idProject: number): Observable<ImportError[]> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/errors?idProject=${idProject}`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  getDLNames(): Observable<String[]> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/dl`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  createReferenceFromFile(idProject: string, idDl: string, file: File): Observable<ReferenceFromFileResponse> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('dlNum',idDl);
    formData.append('idProject',idProject);
    const header = new HttpHeaders()//.set('Content-Type', 'application/x-www-form-urlencoded')
    return this.http.post<ReferenceFromFileResponse>(
      `${this.rootUrl}/newReferencesFromFile`,formData,{headers: header})
    .pipe(
      tap(data => console.log("Anlagenstatus Daten:", data)),
      catchError(this.handleError),
    )
  }

  getCriteria(idProject: number): Observable<CriteriaResponse> {
    return this.http.get<CriteriaResponse>(
      `${this.rootUrl}/criteria?idProject=${idProject}`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  createCriteria(name: string, text: string, type: string, idProject: number): Observable<{message: string}> {
    const formData: FormData = new FormData();
    formData.append('name', name);
    formData.append('text', text);
    formData.append('type', type);
    formData.append('idProject', String(idProject));
    return this.http.post<{message: string}>(
      `${this.rootUrl}/criteria`,formData)
    .pipe(
      tap(data => console.log("Anlagenstatus Daten:", data)),
      catchError(this.handleError),
    )
  }

  updateCriteria(id: number, name: string, text: string, type: string, idProject: number): Observable<string> {
    const formData: FormData = new FormData();
    formData.append('name', name);
    formData.append('text', text);
    formData.append('type', type);
    formData.append('idProject', String(idProject));
    return this.http.put<string>(
      `${this.rootUrl}/criteria/${id}`,formData)
    .pipe(
      tap(data => console.log("Anlagenstatus Daten:", data)),
      catchError(this.handleError),
    )
  }

  deleteCriteria(id: number): Observable<string> {
    return this.http.delete<string>(
      `${this.rootUrl}/criteria/${id}`)
    .pipe(
      tap(data => console.log("Anlagenstatus Daten:", data)),
      catchError(this.handleError),
    )
  }

  editReferenceCriteria(idRef: number, state: string, criteria: number[]) {
    const formData: FormData = new FormData();
    formData.append('state', state);
    formData.append('criteria', String(criteria));
    return this.http.put<string>(
      `${this.rootUrl}/references/${idRef}`,formData)
    .pipe(
      tap(data => console.log("Anlagenstatus Daten:", data)),
      catchError(this.handleError),
    )
  }

  deleteDatabase(): Observable<{message: string}> {
    return this.http.delete<{message: string}>(
      `${this.rootUrl}/`)
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

  private setHttpHeader() {
    const headers = new HttpHeaders().set('Accept', 'application/json').set('Content-Type', 'application/json');
    let options = { headers: headers };
    return options;
  }

  private handleError(error: any): Observable<any> {
    alert(error.error.error);
    console.log(error)
    return throwError(() => (error.statusText));
  }
}