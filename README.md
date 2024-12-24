# Book Library

## Prequisities:

#### Using `docker`:

* Docker

#### In native environment:

* Java 17
* PostgreSQL 14
* Node.js & npm

## Configuration:

* Files can be found in `src/main/resources` directory
    * Edit `application.docker.properties` when running app using `docker`
    * Edit `application.properties` when running app natively
* If editing server port then also edit `apiURL` in `environment.ts` in `webapp/environments`
* Book reservation & borrow times can be configured using `book_hours_reserved` and `book_days_borrowed` values

## Running:

#### Using `docker`:

* Navigate to project root directory
* Run `docker compose up`
* Wait until everything has started up
* Open `http://localhost:4200` in your browser

#### Natively:

* Create database called `booklibrary`
* Run `./gradlew bootRun` in project root directory
* Navigate to `src/main/webapp`
* Run `npm install -g @angular/cli`
* Run `npm install`
* Run `ng serve`
* Open `http://localhost:4200` in your browser

## Swagger:

* Can be accessed from `http://localhost:8080/swagger-ui/index.html`
