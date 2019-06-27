///
/// [y] hybris Platform
///
/// Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
///
/// This software is the confidential and proprietary information of SAP
/// ("Confidential Information"). You shall not disclose such Confidential
/// Information and shall use it only in accordance with the terms of the
/// license agreement you entered into with SAP.
///

import {Injectable} from '@angular/core';
import {
  Http,
  RequestOptionsArgs,
  Response,
  Headers,
} from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

declare var ACC: {config:{CSRFToken:string}};

@Injectable()
export class HttpService {

  constructor (private http: Http) {}

  public get(url: string, options?: RequestOptionsArgs): Observable<Response> {
    this.addXRequestTokenHeader(options);
    return this.http.get(url, options).catch(this.handleError(this));
  }
  public post(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    const updatedOptions = this.updateHeaders(options);
    return this.http.post(url, body, updatedOptions).catch(this.handleError(this));
  }
  public put(url: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    const updatedOptions = this.updateHeaders(options);
    return this.http.put(url, body, updatedOptions).catch(this.handleError(this));
  }
  private handleError (self: HttpService) {
    return (res: Response) => {
      return Observable.throw(res.json());
    };
  }
  private updateHeaders(options?: RequestOptionsArgs): RequestOptionsArgs {
    if (!options) {
      options = { headers: new Headers() };
    }
    this.addCsrfTokenHeader(options);
    this.addXRequestTokenHeader(options);
    return options;
  }

  private addCsrfTokenHeader(options: RequestOptionsArgs){
    const token =  ACC.config.CSRFToken;
    options.headers.set('CSRFToken', `${token}`);
}
  private addXRequestTokenHeader(options: RequestOptionsArgs){
    options.headers.set('X-Requested-With', 'XMLHttpRequest');
  }
}
