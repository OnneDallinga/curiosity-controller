# Getting Started

1. Make sure you have Docker and Maven installed
2. If you wish to populate the Database go to the DataLoader class and enable the @Configuration annotation
3. Run mvn package
4. Run docker-compose up --build
5. Test locally using postman/curl. The endpoint is: http://localhost:8080

# Available endpoints:
- GET: localhost:8080/ted-talks/{id}

Get a Ted Talk by id

- GET: localhost:8080/ted-talks?terms={terms}?author={author}

Filters the author or title based on the "terms" query parameter. Additional query parameters can be freely.
Options are: Title, author, date, likes, views and link
When searching on anything other than the generic "terms" the query parameter must be an exact match. Examples:

localhost:8080/ted-talks?author=jan?terms=a

This will not find the author named "Jantje", only the author named "Jan" but it will find every title with the letter "a"

localhost:8080/ted-talks?terms=phy

This will return every Ted Talk with "phy" in the title or in the author field.

- GET: localhost:8080/ted-talks/title/{title}
- GET: localhost:8080/ted-talks/author/{author}
- GET: localhost:8080/ted-talks/views/{views}
- GET: localhost:8080/ted-talks/likes/{likes}

Note: These 4 endpoints only find by exact match

- POST: localhost:8080/ted-talks

Creates a new Ted Talk. Must not be identical to an already existing Ted Talk. The entire TedTalkDto object is required

- PUT: localhost:8080/ted-talks/{id}

Updated an existing Ted Talk. Id and all fields are required. Requires an existing Ted Talk

- DELETE: localhost:8080/ted-talks/{id}

Delete a Ted Talk. Requires an existing Ted Talk.
