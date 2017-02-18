package tables

import slick.dbio.Effect.{ Read, Schema, Write }

import scala.concurrent._
import scala.concurrent.duration._
import slick.jdbc.H2Profile.api._
import slick.lifted.{ ProvenShape, TableQuery }
import slick.sql.{ FixedSqlAction, FixedSqlStreamingAction }

// runMain tables.Example01
object Example01 extends App {

  // Tables -- mappings between scala types and database tables

  case class Album(artist: String, title: String, year: Int, id: Long = 0)

  // A standard Slick table type representing an SQL table type to store instances
  // of type Album.
  class AlbumTable(tag: Tag) extends Table[Album](tag, "albums") {

    // definitions of each of the columns
    def artist: Rep[String] = column[String]("artist")
    def title: Rep[String] = column[String]("title")
    def year: Rep[Int] = column[Int]("year")

    // the 'id' column has a couple of extra 'flags' to say
    // 'make this a primary key' and 'make this an auto incrementing primary key'
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    // this is the default projection for the table. It tells us how to convert between a
    // tuple of these columns of the database and the Album datatype that we want to map
    // using this table.
    def * : ProvenShape[Album] = (artist, title, year, id) <> (Album.tupled, Album.unapply)
  }

  lazy val AlbumTable = TableQuery[AlbumTable]

  // Actions -- represent commands issued to the database

  // create table "albums" ("artist" VARCHAR NOT NULL,"title" VARCHAR NOT NULL,"year" VARCHAR NOT NULL,"id" BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT)
  val createTableAction: FixedSqlAction[Unit, NoStream, Schema] = AlbumTable.schema.create

  // insert into "albums" ("artist","title","year")  values (?,?,?)
  val insertAlbumsAction: FixedSqlAction[Option[Int], NoStream, Write] = AlbumTable ++= Seq(
    Album("Mark Knopfler", "Altamira", 2015),
    Album("Mark Knopfler", "Privateering", 2012),
    Album("Mark Knopfler", "Kill To Get Crimson", 2007),
    Album("Mark Knopfler", "All The Roadrunning", 2006),
    Album("Mark Knopfler", "Shangri-La", 2004),
    Album("Mark Knopfler", "The Ragpicker's Dream", 2002),
    Album("Mark Knopfler", "Sailing To Philadelphia", 2000),
    Album("Mark Knopfler", "Wag The Dog", 1998),
    Album("Mark Knopfler", "Golden Heart", 1996),
    Album("Dire Straits", "Live At The BBC", 1995),
    Album("Dire Straits", "On The Night", 1993),
    Album("Dire Straits", "On Every Street", 1991),
    Album("Dire Straits", "Brothers In Arms", 1985),
    Album("Dire Straits", "Alchemy: Dire Straits Live", 1984),
    Album("Dire Straits", "Love Over Gold", 1982),
    Album("Dire Straits", "Making Movies", 1980),
    Album("Dire Straits", "Communique", 1979),
    Album("Dire Straits", "Dire Straits", 1978)
  )

  // select "artist", "title", "year", "id" from "albums"
  val selectAlbumsActions: FixedSqlStreamingAction[Seq[Album], Album, Read] = AlbumTable.result

  // Database --

  private val db = Database.forConfig("scalaxdb")

  // Application --

  // db.run takes an Action, runs it against the database and gives us a Future[T]
  // and is great when dealing with async code. Here we don't have async code but
  // sync code so we will await here.
  private def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 2.seconds)

  exec(createTableAction)
  exec(insertAlbumsAction)
  exec(selectAlbumsActions).foreach(println)
}
