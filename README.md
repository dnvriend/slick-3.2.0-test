# slick-3.2.0-test
A small test project to test Slick 3.2.0 Milestone and Release Candidate releases

## Concepts

### Tables
A way of defining a relationship between the Scala datatypes and the database.

### Queries
A DSL for building SQL

### Actions
Actions allow us to sequence queries together and send them to the database in a big scripts.

### Joins
Joins allow us to build queries that pull data from multiple sources

### Profiles/Drivers
Profiles are slick's way how to represent different database backends with their different
capabilities like eg:

- slick.jdbc.DB2Profile
- slick.jdbc.DerbyProfile
- slick.jdbc.H2Profile
- slick.jdbc.HsqldbProfile
- slick.jdbc.MySQLProfile
- slick.jdbc.OracleProfile
- slick.jdbc.PostgresProfile
- slick.jdbc.SQLiteProfile
- slick.jdbc.SQLServerProfile

The idea is that we build generic code that can work with multiple different database backends.

## Documentation
- [2017-02-10 - Slick 3.2.0-RC1](http://slick.lightbend.com/doc/3.2.0-RC1/)
- [2016-12-05 - Slick 3.2.0-M2](http://slick.lightbend.com/doc/3.2.0-M2/)
- [2016-17-04 - Slick 3.2.0-M1](http://slick.lightbend.com/doc/3.2.0-M1/)

## Video
- [(1'42 hr) Essential Slick Workshop - Dave Gurnell](https://vimeo.com/148074461)

## Resources
- [Essential Slick - Underscore.io](http://underscore.io/books/essential-slick/)