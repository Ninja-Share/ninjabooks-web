import { Component } from '@angular/core';

@Component({
  selector: 'my-app',
  template: `
  <nav class="navbar navbar-default navbar-static-top">
      <div class="container">
        <div class="navbar-header">
     <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-1" aria-expanded="false">
       <span class="sr-only">Toggle navigation</span>
       <span class="icon-bar"></span>
       <span class="icon-bar"></span>
       <span class="icon-bar"></span>
     </button>
     <a class="navbar-brand" href="#">BOOKS SHARING</a>
   </div>
   <div class="collapse navbar-collapse" id="navbar-collapse-1">
        <ul class="nav navbar-nav navbar-right">
          <li><a href="#">Szukaj</a></li>
          <li><a href="#">Lista książek</a></li>
          <li><a href="#">Dodaj książkę</a></li>
          <li><a href="#">Powiadomienia</a></li>
          <li><a href="#">Konto</a></li>
        </ul>
    </div>
  </div>
    </nav>
    <div class="container main-content logging-section">

    </div>`,
})
export class AppComponent  { name = 'Angular'; }
