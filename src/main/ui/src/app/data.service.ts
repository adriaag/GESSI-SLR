import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Project } from './dataModels/project';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Reference } from './dataModels/reference';
import { ImportError } from './dataModels/importError';
import { ReferenceFromFileResponse } from './dataModels/referenceFromFileResponse';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  rootUrl: string = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(
      this.rootUrl+"/projects", this.setHttpHeader())
      .pipe(
        //tap(data => console.log("Anlagenstatus Daten:", data)),
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

  getReference(idReference: number): Observable<Reference> {
    return this.http.get<Project[]>(
      `${this.rootUrl}/reference?idReference=${idReference}`, this.setHttpHeader())
      .pipe(
        tap(data => console.log("Anlagenstatus Daten:", data)),
        catchError(this.handleError),
      )
  }

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
    console.log(idProject, 'idProject')
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

  private setHttpHeader() {
    const headers = new HttpHeaders().set('Accept', 'application/json').set('Content-Type', 'application/json');
    let options = { headers: headers };
    return options;
  }

  private handleError(error: Response): Observable<any> {
    console.error("observable error: ", error);
    return throwError(() => (error.statusText));
  }

  getSubmissions(mode: string | null, user: string | null) {
    return this.http.get(
      `${this.rootUrl}/submissions?mode=${mode}&user=${user}`,
      {headers: this.getHeaders()}
    );
  }

  getSubmission(id: string | null) {
    return this.http.get(
      `${this.rootUrl}/submissions/${id}`,
      {headers: this.getHeaders()}
    );
  }

  getComments(id: string | null,mode:string|null) {
    return this.http.get(
      `${this.rootUrl}/comments?user=${id}&mode=${mode}`,
      {headers: this.getHeaders()}
    );
  }

  setUserAbout(id: string | null, about: string | null) {
    return this.http.put(
      `${this.rootUrl}/users/${id}`,
      {about: about},
      {headers: this.getHeaders()});
  }

  likeSubmission(id: string) {
    return this.http.post(
      `${this.rootUrl}/submissions/${id}/votes`,null,{headers: this.getHeaders()});
  }
  
  unlikeSubmission(id: string) {
    return this.http.delete(
      `${this.rootUrl}/submissions/${id}/votes`,{headers: this.getHeaders()});
  }
  
  likeComment(id: string) {
    return this.http.post(
      `${this.rootUrl}/comments/${id}/votes`,null,{headers: this.getHeaders()});
  }
  
  unlikeComment(id: string) {
    return this.http.delete(
      `${this.rootUrl}/comments/${id}/votes`,{headers: this.getHeaders()});
  }

  likeReply(id: string) {
    return this.http.post(
      `${this.rootUrl}/replies/${id}/votes`,null,{headers: this.getHeaders()});
  }
  
  unlikeReply(id: string) {
    return this.http.delete(
      `${this.rootUrl}/replies/${id}/votes`,{headers: this.getHeaders()});
  }
  
  createSubmission(titol: string, text: string, url: string) {
    return this.http.post(`${this.rootUrl}/submissions`,
    {
      "title": titol,
      "text": text,
      "url": url
    },{headers: this.getHeaders()});
  }
  
  addComment(submission_id: string, contingut: string) {
    return this.http.post(`${this.rootUrl}/comments?submission_id=${submission_id}`,
    {content: contingut},
    {headers: this.getHeaders()});
  }
  
  addReply(comment_id: string, reply_id: string | null, contingut: string) {
    let url = "replies?comment_id=" + comment_id;
    if (reply_id != null) url += "&reply_id=" + reply_id; 
    return this.http.post(`${this.rootUrl}/` + url,
    {content: contingut},
    {headers: this.getHeaders()});
  }

  getHeaders() {
    return new HttpHeaders({
      Accept: 'application/json',
      'X-API-KEY': sessionStorage['userApiKey']
    });
  }
}