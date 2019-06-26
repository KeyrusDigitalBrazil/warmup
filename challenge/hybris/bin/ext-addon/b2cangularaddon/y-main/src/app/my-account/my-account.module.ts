import { CommonModule }           from '@angular/common';
import { NgModule }               from '@angular/core';
import { FormsModule }            from '@angular/forms';
import { HttpModule }             from '@angular/http';
import { RouterModule }           from '@angular/router';

import { ErrorHandlingModule }    from '../shared/error-handling/error-handling.module';
import { MyAccountRoutingModule } from './my-account-routing.module';
import { OCCModule }              from '../shared/occ/occ.module';

import { UpdateEmailComponent }   from './update-email.component';
import { UpdatePasswordComponent} from './update-password.component';
import { UpdateProfileComponent } from './update-profile.component';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ErrorHandlingModule,
    MyAccountRoutingModule,
    OCCModule
  ],
  declarations: [
    UpdateEmailComponent,
    UpdatePasswordComponent,
    UpdateProfileComponent
  ],
  providers: [
  ]
})
export class MyAccountModule { }