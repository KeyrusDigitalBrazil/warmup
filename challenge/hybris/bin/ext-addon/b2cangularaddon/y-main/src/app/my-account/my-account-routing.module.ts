import { NgModule }     from '@angular/core';
import { RouterModule } from '@angular/router';

import { UpdateEmailComponent } from './update-email.component';
import { UpdatePasswordComponent } from './update-password.component';
import { UpdateProfileComponent } from './update-profile.component';

@NgModule({
  imports: [RouterModule.forChild([
    {
      path: 'my-account/update-email',
      component: UpdateEmailComponent
    },
    {
      path: 'my-account/update-password',
      component: UpdatePasswordComponent
    },
    {
      path: 'my-account/update-profile',
      component: UpdateProfileComponent
    }
  ])],
  exports: [RouterModule]
})
export class MyAccountRoutingModule {}
