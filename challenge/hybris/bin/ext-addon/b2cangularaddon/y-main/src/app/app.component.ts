import { Component, Renderer } from '@angular/core';
import { GlobalVarService }    from './shared/occ/global-var.service';

@Component({
  selector: 'y-main',
  template: `<router-outlet></router-outlet>`,
})
export class AppComponent {
  constructor(private globalVarService: GlobalVarService,  private renderer: Renderer) {
    let rootElement = renderer.selectRootElement('y-main');
    globalVarService.siteUid = rootElement.getAttribute('siteUid');
    globalVarService.locale = rootElement.getAttribute('locale');
  }
}
