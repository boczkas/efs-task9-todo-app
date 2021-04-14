# TODO App

Aplikacja do zarządzania zadaniami

## Zadanie

Przedmiotem zadania jest zaimplementowanie aplikacji internetowej do zarządzania zadaniami. Aplikacja powinna udostępniać zasoby i funkcjonalność jako REST API.

### Wymagania

* Aplikacja powinna obsługiwać następujące żądania HTTP

_metoda_ - metoda HTTP żądania,
_adres_ - ścieżka adresu URL żądanego zasobu,
_nagłówki_ - zmienne wysyłane w nagłówku żądania,
_parametry_ - zmienne wysyłane w ścieżce zasobu,
_treść_ - przykład ewentualnej treści żądania,
_odpowiedzi_ - obsługiwane kody statusu oraz przykład ewentualnej treści odpowiedzi

metoda | adres | naglówki | parametry | ciało | odpowiedzi
------ | ----- | -------- | --------- | ----- | ----------
POST | /todo/user | | | <pre>{<br/>&#9;"username": "janKowalski",<br/>&#9;"password": "am!sK#123"<br/>}</pre> | <ul> <li>201</li><li>400</li><li>409</li> </ul>
POST | /todo/task | auth | | <pre>{<br/>&#9;"description": "Kup mleko",<br/>&#9;"due": "2021-06-30"<br/>}</pre> | <ul><li>201<pre>{<br/>&#9;"id": "237e9877-e79b-12d4-a765-321741963000"<br/>}</li><li>400</li><li>401</li><ul>
GET | /todo/task | auth | | | <ul><li>200<pre>[<br/>&#9;{<br/>&#9;&#9;"id": "237e9877-e79b-12d4-a765-321741963000",<br/>&#9;&#9;"description": "Kup mleko",<br/>&#9;&#9;"due": "2021-06-30"<br/>&#9;}<br/>]</pre></li><li>400</li><li>401</li></ul>
GET | /todo/task/{id} | auth | id | | <ul><li>200<pre>{<br/>&#9;"id": "237e9877-e79b-12d4-a765-321741963000",<br/>&#9;"description": "Kup mleko",<br/>&#9;"due": "2021-06-30"<br/>}</pre></li><li>400</li><li>401</li><li>403</li><li>404</li></ul>
PUT | /todo/task/{id} | auth | id | <pre>{<br/>&#9;"description": "Kup mleko",<br/>&#9;"due": "2021-06-30"<br/>}</pre> | <ul><li>200<pre>{<br/>&#9;"id": "237e9877-e79b-12d4-a765-321741963000",<br/>&#9;"description": "Kup mleko",<br/>&#9;"due": "2021-06-30"<br/>}</pre></li><li>400</li><li>401</li><li>403</li><li>404</li></ul>
DELETE | /todo/task/{id} | auth | id |  | <ul><li>200</li><li>400</li><li>401</li><li>403</li><li>404</li></ul>

**auth** - ciąg znaków 'base64(username):base64(password)', gdzie base64() oznacza funkcję kodującą algorytmem Base64. 
Np., dla użytkownika `{ "username": "janKowalski", "password": "am!sK#123" }`, `auth` będzie równe `amFuS293YWxza2k=:YW0hc0sjMTIz`

**id** - unikalny identyfikator zadania w formacie UUID

* Aplikacja powinna obsługiwać treści żądań w formacie JSON
* Aplikacja powinna zwracać treści odpowiedzi w formacie JSON
* Informacje o tym, które nagłówki, parametry lub pola w dokumentach JSON znajdują się w szczegółowej [dokumenatcji Swagger API](https://epam-online-courses.github.io/efs-task9-todo-app/) aplikacji
* Zadania i użytkownicy stworzeniu przy użyciu żądań, powinni być pamiętani w ramach jednokrotnego uruchomienia aplikacji
* Aplikacja powinna posiadać testy jednostkowe sprawdzające wszystkie obsługiwane żądania
* Testy jednostkowe powinny sprawdzać wszystkie scenariusze użycia (zwracanie wszystkich możliwych odpowiedzi)

### Ograniczenia

* Aplikacja powinna być zaimplementowana wyłącznie z użyciem klas z JDK. Wyjątkiem są biblioteki służące do pisania testów jednostkowych (np. JUnit, AssertJ)

## Sprawdzanie i ocena rozwiązania

## Materiały

### Materiały, z którymi należy się zapoznać przed zajęciami

1. [API](https://pl.wikipedia.org/wiki/Interfejs_programowania_aplikacji)
1. [REST](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm)
1. [HTTP Methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods)
1. [HTTP Response Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
1. [JSON](http://www.json.org/json-pl.html)
1. [UUID](https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html)

### Materiały, które mogą być pomocne podczas rozwiązywania zadania

1. [REST API Tutorial](https://restfulapi.net/)
1. [How to use Swagger UI](https://idratherbewriting.com/learnapidoc/pubapis_swagger.html)
