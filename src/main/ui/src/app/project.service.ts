import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private idProject = new BehaviorSubject<number>(NaN);

  constructor(private dataService: DataService) { }

  getProject(): Observable<number> {
    return this.idProject.asObservable()
  }

  setProject(id : number): void {
    this.idProject.next(id)
  }
}
