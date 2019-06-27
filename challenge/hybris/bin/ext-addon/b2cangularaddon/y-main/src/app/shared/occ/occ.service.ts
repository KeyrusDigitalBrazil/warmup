import { Injectable }       from '@angular/core';
import { Headers }          from '@angular/http';
import { OAuth2Service }    from './oauth2.service';
import { GlobalVarService } from './global-var.service';

const baseOCCUrl = '/rest/v2/';

@Injectable()
export class OCCService {
  private headers = new Headers({'Content-Type': 'application/json'});

  constructor(private oauth2Service: OAuth2Service, private globalVarService: GlobalVarService) {
  }

  getOCCHeadersWithAccessToken(): Headers {
    return this.oauth2Service.addAuthorizationToHeaders(this.headers);
  }

  getBaseOCCUrlWithSite(): string {
    return baseOCCUrl + this.globalVarService.siteUid;
  }

  getLocaleParam(): string {
    return 'lang=' + this.globalVarService.locale;
  }
}
