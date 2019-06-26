import { Injectable }     from '@angular/core';
import { Headers }        from '@angular/http';

import { CookieService }  from 'ngx-cookie';

@Injectable()
export class OAuth2Service {
  constructor(
    private cookieService:CookieService
  ) { }

  private getAccessTokenFromCookie(): string {
    return decodeURIComponent(this.cookieService.get("angularToken"));
  }

  addAuthorizationToHeaders(headers: Headers): Headers {
    const accessToken = this.getAccessTokenFromCookie();

    if (accessToken) {
      headers.set('Authorization', 'bearer ' + accessToken);
    }

    return headers;
  }
}
