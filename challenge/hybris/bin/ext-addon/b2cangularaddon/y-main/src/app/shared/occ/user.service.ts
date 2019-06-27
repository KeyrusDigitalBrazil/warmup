import { Injectable }     from '@angular/core';
import { Headers, Http, RequestOptions, URLSearchParams }  from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { CookieService }  from 'ngx-cookie';

import { OCCService }     from './occ.service';

import { Titles }         from './titles';
import { User }           from './user';
import { Error }          from '../error-handling/error';
import { Errors }         from '../error-handling/errors';

@Injectable()
export class UserService {
  hasValidationErrors: boolean;
  validationErrors: Error[];

  constructor(
    private cookieService: CookieService,
    private http: Http,
    private occService: OCCService
  ) { }

  getEmailAddressFromCookie(): string {
    return decodeURIComponent(this.cookieService.get("userId"));
  }

  getTitles(): Promise<Titles> {
    const url = this.getTitlesUrl();
    const headers = this.occService.getOCCHeadersWithAccessToken();

    return this.http.get(url, {headers: headers})
      .toPromise()
      .then(response => response.json() as Titles)
      .catch(this.handleError);
  }

  getTitlesUrl(): string {
    return this.occService.getBaseOCCUrlWithSite() + '/titles?' + this.occService.getLocaleParam();
  }

  getUser(userId: string): Promise<User> {
    const url = this.getUsersUrl() + '/' + userId;
    const headers = this.occService.getOCCHeadersWithAccessToken();

    return this.http.get(url, {headers: headers})
      .toPromise()
      .then(response => response.json() as User)
      .catch(this.handleError);
  }

  getUsersUrl(): string {
    return this.occService.getBaseOCCUrlWithSite() + '/users';
  }

  update(user: User): Promise<any> {
    const url = this.getUsersUrl() + '/' + user.uid;
    const headers = this.occService.getOCCHeadersWithAccessToken();
    return this.http
      .put(url, JSON.stringify(user), {headers: headers})
      .toPromise()
      .then(() => user)
      .catch(this.handleError);
  }

  changeLogin(newLogin: string, password: string): Promise<any>
  {
    const url = this.getUsersUrl() + '/' + this.getEmailAddressFromCookie() + '/login';
    const params = new URLSearchParams();
    params.set('newLogin', newLogin);
    params.set('password', password);
    return this.sendPutRequest(url, params);
  }

  changePassword(currentPassword: string, newPassword: string): Promise<any>
  {
    const url = this.getUsersUrl() + '/' + this.getEmailAddressFromCookie() + '/' + 'password';
    const params = new URLSearchParams();
    params.set('old', currentPassword);
    params.set('new', newPassword);
    return this.sendPutRequest(url, params);
  }

  private sendPutRequest(url:string, params:URLSearchParams)
  {
    const headers = this.occService.getOCCHeadersWithAccessToken();
    const requestOptions = new RequestOptions({headers: headers, params: params});
    return this.http
        .put(url, null, requestOptions)
        .toPromise()
        .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    const errors = JSON.parse(error._body) as Errors;

    if (errors.errors.length > 0) {
      return Promise.reject(errors.errors);
    } else {
      return Promise.reject(error.message || error);
    }
  }
}
