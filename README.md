# slick-3.2.0-test
A small test project to test [Slick 3.2.0 Release Candidate](http://slick.lightbend.com/news/2017/02/10/slick-3.2.0-RC1-released.html)

## Tables, Queries, Actions, Joins and Profiles
Slick isn't that difficult to use but we need to know some concepts so we know how to configure it.

### Tables
Tables is how Slick defines a relationship between the Scala datatypes and the database. We need two things, a Scala datatype that we use in our application and a table where to store the data. For example:

```scala
case class Album(artist: String, title: String, id: Long = 0)

class AlbumTable(tag: Tag) extends Table[Album](tag, "album") {
	def artist = column[String]("artist")
	def title = column[String]("title")
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    // the default projection of the table
	def * (artist, title, id) <> (Album.tupled, Album.unapply)
}

lazy val AlbumTable = TableQuery[AlbumTable]
```

## Custom Column Mapping
ColumnType typeclass



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