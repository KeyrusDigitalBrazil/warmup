export class Error {
  message: string;
  reason: string;
  subject: string;
  subjectType: string;
  type: string;

  constructor(subject:string, message:string)
  {
    this.subject = subject;
    this.message = message;
  }
}