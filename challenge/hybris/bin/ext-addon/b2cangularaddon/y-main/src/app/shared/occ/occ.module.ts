import { NgModule }             from '@angular/core';

import { OAuth2Service }        from './oauth2.service';
import { OCCService }           from './occ.service';
import { UserService }          from './user.service';
import { GlobalVarService }     from './global-var.service';

@NgModule({
  providers: [
    OAuth2Service,
    OCCService,
    UserService,
    GlobalVarService
  ]
})
export class OCCModule { }
