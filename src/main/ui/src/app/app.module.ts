import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HttpHeaders, HTTP_INTERCEPTORS } from '@angular/common/http';
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
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatChipsModule} from '@angular/material/chips';


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
import { ProjectDeleteComponent } from './project-delete/project-delete.component';
import { ReferenceImportManuallyComponent } from './reference-import-manually/reference-import-manually.component';
import { LoginComponent } from './login/login.component';
import { AuthInterceptor } from './auth.interceptor';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { MainComponent } from './main/main.component';
import { ScreeningComponent } from './screening/screening.component';

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
    ProjectDeleteComponent,
    ReferenceImportManuallyComponent,
    LoginComponent,
    ChangePasswordComponent,
    MainComponent,
    ScreeningComponent,
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
    MatSelectModule,
    MatTooltipModule,
    MatAutocompleteModule,
    MatChipsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi:true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
