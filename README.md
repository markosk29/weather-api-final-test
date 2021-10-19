## Spring Boot: Weather API

In this challenge, you are part of a team that is building a travel company platform. One requirement is for a REST API service to provide weather information. Your task is to add functionality to add and delete weather information as well as perform some queries. You'll be dealing with typical information for weather data like latitude, longitude, temperature, etc.


The definitions and detailed requirements list are written below. You will be graded on whether your application performs data retrieval and manipulation based on given use cases exactly as described in the requirements.


Each weather data is a JSON object describing hourly temperatures recorded at a given location on a given date. Each such object has the following properties:

```
id: the unique integer ID of the object
date: the date, in YYYY-MM-DD format, denoting the date of the record
lat: the latitude (up to 4 decimal places) of the location of the record
lon: the longitude (up to 4 decimal places) of the location of the record
city: the name of the city of the record
state: the name of the state of the record
temperatures: an array of 24 float values, each up to one decimal place, denoting the hourly temperatures of the record in Celsius
 ```



Your task is to complete the given project so that it passes all the test cases when running the provided unit tests. The project by default supports the use of the SQLite3 database. Implement the POST request to /weather first because testing the other methods require POST to work correctly.


## Environment:
- Java version: 1.8
- Maven version: 3.*
- Spring Boot version: 2.2.1.RELEASE

## Read-Only Files:
- src/test/*

## Data:
Example of a weather data JSON object:
```
{
   "id": 1,
   "date": "1985-01-01",
   "lat": 36.1189,
   "lon": -86.6892,
   "city": "Nashville",
   "state": "Tennessee",
   "temperatures": [17.3, 16.8, 16.4, 16.0, 15.6, 15.3, 15.0, 14.9, 15.8, 18.0, 20.2, 22.3, 23.8, 24.9, 25.5, 25.7, 24.9, 23.0, 21.7, 20.8, 29.9, 29.2, 28.6, 28.1]
}
```

## Requirements:
The `REST` service must expose the `/weather` endpoint, which allows for managing the collection of weather records in the following way:


POST request to `/weather`:

- creates a new weather data record
- expects a valid weather data object as its body payload, except that it does not have an id property; you can assume that the given object is always valid
- adds the given object to the collection and assigns a unique integer id to it
- the response code is 201 and the response body is the created record, including its unique id


GET request to `/weather`:

- the response code is 200
- the response body is an array of matching records, ordered by their ids in increasing order
- accepts an optional query string parameter, date, in the format YYYY-MM-DD, for example /weather/?date=2019-06-11. When this parameter is present, only the records with the matching date are returned.
- accepts an optional query string parameter, city, and when this parameter is present, only the records with the matching city are returned. The value of this parameter is case insensitive, so "London" and "london" are equivalent. Moreover, it might contain several values, separated by commas (e.g. city=london,Moscow), meaning that records with the city matching any of these values must be returned.
- accepts an optional query string parameter, sort, that can take one of two values: either "date" or "-date". If the value is "date", then the ordering is by date in ascending order. If it is "-date", then the ordering is by date in descending order. If there are two records with the same date, the one with the smaller id must come first.


GET request to `/weather/<id>`:

- returns a record with the given id
- if the matching record exists, the response code is 200 and the response body is the matching object
- if there is no record in the collection with the given id, the response code is 404

## Commands
- run: 
```bash
mvn clean package; java -jar target/WeatherApi-1.0-SNAPSHOT.jar
```
- install: 
```bash
mvn clean install
```
- test: 
```bash
mvn clean test
```
