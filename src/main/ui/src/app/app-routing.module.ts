import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { IndexComponent } from './index/index.component';
import { ReferencesComponent } from './references/references.component';
import { ReferenceInfoComponent } from './reference-info/reference-info.component';
import { ErrorsComponent } from './errors/errors.component';

const routes: Routes = [
  {path: '', component: IndexComponent},
  {path: 'references', component: ReferencesComponent},
  {path: 'references/:id', component: ReferenceInfoComponent},
  {path: 'errors', component: ErrorsComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
