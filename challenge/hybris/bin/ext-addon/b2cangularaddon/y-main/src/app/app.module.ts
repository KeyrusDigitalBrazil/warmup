import { BrowserModule }        from '@angular/platform-browser';
import { CookieModule }         from 'ngx-cookie';
import { NgModule }             from '@angular/core';
import { FormsModule }          from '@angular/forms';
import { HttpModule }           from '@angular/http';
import { RouterModule }         from '@angular/router';

import { AppRoutingModule }     from './app-routing.module';
import { MyAccountModule }      from './my-account/my-account.module';

import { AppComponent }         from './app.component';

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    CookieModule.forRoot(),
    MyAccountModule
  ],
  declarations: [
    AppComponent
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
