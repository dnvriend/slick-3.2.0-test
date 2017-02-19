package tables

import slick.dbio.Effect.{ Read, Schema, Write }
import slick.jdbc.H2Profile.api._
import slick.lifted.{ ProvenShape, TableQuery }
import slick.sql.{ FixedSqlAction, FixedSqlStreamingAction }
import tables.Rating._

import scala.concurrent._
import scala.concurrent.duration._

abstract class Rating(val starts: Int)

object Rating {
  case object Awesome extends Rating(5)
  case object Good extends Rating(4)
  case object NotBad extends Rating(3)
  case object Meh extends Rating(2)
  case object Aargh extends Rating(1)

  implicit val columnType: BaseColumnType[Rating] =
    MappedColumnType.base[Rating, Int](Rating.toInt, Rating.fromInt)

  def fromInt(stars: Int): Rating = stars match {
    case 5 => Awesome
    case 4 => Good
    case 3 => NotBad
    case 2 => Meh
    case 1 => Aargh
    case _ => sys.error("Ratings only apply from 1 to 5")
  }

  def toInt(rating: Rating): Int = rating match {
    case Awesome => 5
    case Good => 4
    case NotBad => 3
    case Meh => 2
    case Aargh => 1
  }
}

/**
 * Custom Column Types
 *
 * runMain tables.Example03
 */
object Example03 extends App {

  // Tables -- mappings between scala types and database tables

  case class Album(artist: String, title: String, year: Int, rating: Rating, id: Long = 0)

  // A standard Slick table type representing an SQL table type to store instances
  // of type Album.
  class AlbumTable(tag: Tag) extends Table[Album](tag, "albums") {

    // definitions of each of the columns
    def artist: Rep[String] = column[String]("artist")
    def title: Rep[String] = column[String]("title")
    def year: Rep[Int] = column[Int]("year")
    def rating = column[Rating]("rating")

    // the 'id' column has a couple of extra 'flags' to say
    // 'make this a primary key' and 'make this an auto incrementing primary key'
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    // this is the default projection for the table. It tells us how to convert between a
    // tuple of these columns of the database and the Album datatype that we want to map
    // using this table.
    def * : ProvenShape[Album] = (artist, title, year, rating, id) <> (Album.tupled, Album.unapply)
  }

  lazy val AlbumTable = TableQuery[AlbumTable]

  // Actions -- represent commands issued to the database

  // create table "albums" ("artist" VARCHAR NOT NULL,"title" VARCHAR NOT NULL,"year" VARCHAR NOT NULL,"id" BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT)
  val createTableAction: FixedSqlAction[Unit, NoStream, Schema] = AlbumTable.schema.create

  // insert into "albums" ("artist","title","year")  values (?,?,?)
  val insertAlbumsAction: FixedSqlAction[Option[Int], NoStream, Write] = AlbumTable ++= Seq(
    Album("Mark Knopfler", "Altamira", 2015, Good),
    Album("Mark Knopfler", "Privateering", 2012, Awesome),
    Album("Mark Knopfler", "Kill To Get Crimson", 2007, Awesome),
    Album("Mark Knopfler", "All The Roadrunning", 2006, Awesome),
    Album("Mark Knopfler", "Shangri-La", 2004, Good),
    Album("Mark Knopfler", "The Ragpicker's Dream", 2002, Awesome),
    Album("Mark Knopfler", "Sailing To Philadelphia", 2000, Awesome),
    Album("Mark Knopfler", "Wag The Dog", 1998, Awesome),
    Album("Mark Knopfler", "Golden Heart", 1996, Awesome),
    Album("Dire Straits", "Live At The BBC", 1995, Awesome),
    Album("Dire Straits", "On The Night", 1993, Awesome),
    Album("Dire Straits", "On Every Street", 1991, Awesome),
    Album("Dire Straits", "Brothers In Arms", 1985, Awesome),
    Album("Dire Straits", "Alchemy: Dire Straits Live", 1984, Awesome),
    Album("Dire Straits", "Love Over Gold", 1982, Awesome),
    Album("Dire Straits", "Making Movies", 1980, Awesome),
    Album("Dire Straits", "Communique", 1979, Awesome),
    Album("Dire Straits", "Dire Straits", 1978, Awesome)
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
