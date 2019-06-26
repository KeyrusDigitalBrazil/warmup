import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'global-alerts',
  templateUrl: 'global-alerts.component.html'
})
export class GlobalAlertsComponent {
  private _confMsgs: string[];
  private _infoMsgs: string[];
  private _errorMsgs: string[];

  @Output() onCleared = new EventEmitter<string>();

  @Input()
  set confMsgs(confMsgs: string[]) {
    this._confMsgs = confMsgs;
  }

  get confMsgs(): string[] {
    return this._confMsgs;
  }

  @Input()
  set infoMsgs(infoMsgs: string[]) {
    this._infoMsgs = infoMsgs;
  }

  get infoMsgs(): string[] {
    return this._infoMsgs;
  }

  @Input()
  set errorMsgs(errorMsgs: string[]) {
    this._errorMsgs = errorMsgs;
  }

  get errorMsgs(): string[] {
    return this._errorMsgs;
  }

  hasConfMsgs(): boolean {
    return this._confMsgs && this._confMsgs.length > 0;
  }

  hasInfoMsgs(): boolean {
    return this._infoMsgs && this._infoMsgs.length > 0;
  }

  hasErrorMsgs(): boolean {
    return this._errorMsgs && this._errorMsgs.length > 0;
  }

  clear(type: string) {
    switch(type) {
      case 'conf':
        this._confMsgs = null;
        break;
      case 'info':
        this._infoMsgs = null;
        break;
      case 'error':
        this._errorMsgs = null;
        break;
    }
    this.onCleared.emit(type);
  }
}
