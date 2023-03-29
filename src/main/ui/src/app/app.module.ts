import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HttpHeaders } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { MatSortModule } from '@angular/material/sort';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTableModule } from '@angular/material/table'
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';


import { AppComponent } from './app.component';
import { IndexComponent } from './index/index.component';
import { ReferencesComponent } from './references/references.component';
import { ReferenceInfoComponent } from './reference-info/reference-info.component';
import { ErrorsComponent } from './errors/errors.component';
import { ReferenceImportComponent } from './reference-import/reference-import.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { CriteriaComponent } from './criteria/criteria.component';
import { CriteriaEditComponent } from './criteria-edit/criteria-edit.component';
import { ReferenceClassifyComponent } from './reference-classify/reference-classify.component';
import { ProjectCreateComponent } from './project-create/project-create.component';

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    ReferencesComponent,
    ReferenceInfoComponent,
    ErrorsComponent,
    ReferenceImportComponent,
    CriteriaComponent,
    CriteriaEditComponent,
    ReferenceClassifyComponent,
    ProjectCreateComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MatSortModule,
    BrowserAnimationsModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    MatSelectModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
