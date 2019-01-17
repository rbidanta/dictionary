# Look for Definition Of A Word

This is the backend service that makes calls to the [Robot](https://app.swaggerhub.com/apis-docs/InMoment/challenge-robot/1.0.0) API inorder to search the word.

## Request
The backend service exposes an HTTP GET request.

HTTP:http://localhost:{port}/search/{Word}

## Response
If the word exists in the dictionary a JSON string is returned as shown below.

```
{
    "status": "READY",
    "timeUsed": 272601,
    "timeRemaining": 332198,
    "currentPageIndex": 428,
    "currentTermIndex": 95,
    "currentTerm": "Z",
    "currentTermDefinition": "Z, the twenty-sixth and last letter of the English alphabet, isa vocal consonant. It is taken from the Latin letter Z, which camefrom the Greek alphabet, this having it from a Semitic source. Theultimate origin is probably Egyptian. Etymologically, it is mostclosely related to s, y, and j; as in glass, glaze; E. yoke, Gr.yugum; E. zealous, jealous. See Guide to Pronunciation, §§ 273, 274.",
    "hasNextPage": true,
    "hasNextTerm": true,
    "hasPreviousPage": true,
    "hasPreviousTerm": true
}
```

If the word does not exist:

```
{
    "status": "Word Not Found"
}
```

## How to run the project?
### Option 1
- Make sure you have the Java version on your workstation is atleast 1.8
- Clone the project from [github](https://github.com/rbidanta/dictionary.git)
- Run the following command to build the project
```
./gradlew clean build
```
- The above command will create a jar file under /build/libs/dictionary-api-0.1.0.jar
- Use the following command to run the application
```
java -jar /build/libs/dictionary-api-0.1.0.jar
```
- The application will run on port number 8080 and can be accessed using
[https://localhost:8080/search/](http://localhost:8080/search/) as the base context

### Option 2
- Clone the project from [github](https://github.com/rbidanta/musicapi)
- Make sure to install latest version of [gradle](http://gradle.org/install/) using the following command or any other package manager
```
brew install gradle
```
- Run the following command to build the project
```
gradle bootrun
```
- The application will run on port number 8080 and can be accessed using
[https://localhost:8080/search/](https://localhost:8080/search/) as the base context

