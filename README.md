# bookstore-backend

bookstore-backend is a spring boot rest API to solve your challenge

## Installation Maven
```bash
./mvnw spring-boot:run
```
## Installation Docker
```bash
docker build -t bookstore-backend .
docker run -p 8080:8080 bookstore-backend
```
Sorry I've not tested the docker I'm working on a pc without it, I suspect my company pc is under control

## Usage

the swagger is

```bash
http://localhost:8080/swagger-ui/index.html
```

the database is here
```bash
http://localhost:8080/h2-console/
jdbc url jdbc:h2:mem:testdb
username sa
```

## Workflow
U need to /register a user there are two roles ADMIN and CONSUMER, with admin u can update/create/delete books, all the other endpoints are usable by logged user except /login outside auth.

After u register u need to /login take the token and put in the Authorization button top right.

Then u can start play with the endpoints. U can only make orders for the current logged user. I can switch but for the moment I think it's ok like this.

I used media type versioning, I did some unit test, for the moment I left integration test out, I left some comments here and there in the code. It is a bit of code already to review.

I logged at minimum and I could have added MCD UUID to see the flow of the request, for the moment I've not done that. I could have also added actuator to helat/managment endpoints I left it out for the moment.

I did in my opinion the closest thing to open close principle to add new types of books, U need to subclass Book and implement a new field in BookType Enum.

I din't use map struct to map objects only becose there where few of them and then I should have found a trik to take the mapper in the BookType Enum. SO I made the mapping by my self.

If u want me to imporve something please let me know, I can work on it.