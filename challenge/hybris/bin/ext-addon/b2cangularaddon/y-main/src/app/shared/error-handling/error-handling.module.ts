import { CommonModule }           from '@angular/common';
import { NgModule }               from '@angular/core';

import { ValidationService }      from './validation.service';

import { GlobalAlertsComponent }  from './global-alerts.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    GlobalAlertsComponent
  ],
  exports: [
    GlobalAlertsComponent
  ],
  providers: [
    ValidationService
  ],
})
export class ErrorHandlingModule { }
