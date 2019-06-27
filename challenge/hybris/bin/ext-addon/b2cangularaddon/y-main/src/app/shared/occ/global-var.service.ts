import { Injectable }     from '@angular/core';

@Injectable()
export class GlobalVarService {
    private _siteUid = '';
    private _locale = 'en';

    public set siteUid(siteUid: string) {
        this._siteUid = siteUid;
    }

    public get siteUid(): string {
        return this._siteUid;
    }

    public set locale(locale: string) {
        this._locale = locale;
    }

    public get locale(): string {
        return this._locale;
    }
}
