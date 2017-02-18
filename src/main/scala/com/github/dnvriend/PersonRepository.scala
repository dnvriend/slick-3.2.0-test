package com.github.dnvriend

import java.sql.{ Date, Timestamp }
import java.text.SimpleDateFormat
import java.util.UUID
import javax.inject.{ Inject, Singleton }

import com.github.dnvriend.PersonRepository.PersonTableRow
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.{ JdbcBackend, JdbcProfile }
import slick.lifted.ProvenShape

import scala.concurrent.{ ExecutionContext, Future }

object PersonRepository {
  final case class PersonTableRow(name: String, dateOfBirth: Date, created: Timestamp = new Timestamp(System.currentTimeMillis()), id: String = UUID.randomUUID.toString)
}

@Singleton
class PersonRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def getProfile: JdbcProfile = profile
  def database: JdbcBackend#DatabaseDef = db

  implicit class DateString(dateString: String) {
    def date: java.sql.Date = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      new java.sql.Date(sdf.parse(dateString).getTime)
    }
  }

  class PersonTable(tag: Tag) extends Table[PersonTableRow](tag, "PERSONS") {
    def * : ProvenShape[PersonTableRow] = (name, dateOfBirth, created, id) <> (PersonTableRow.tupled, PersonTableRow.unapply)
    def id: Rep[String] = column[String]("ID", O.PrimaryKey)
    def name: Rep[String] = column[String]("NAME")
    def dateOfBirth: Rep[Date] = column[Date]("DATE_OF_BIRTH")
    def created: Rep[Timestamp] = column[Timestamp]("CREATED")
  }

  lazy val PersonTable = new TableQuery(tag => new PersonTable(tag))

  def dropCreateSchema: Future[Unit] = {
    val schema = PersonTable.schema
    db.run(schema.create)
      .recoverWith {
        case t: Throwable =>
          db.run(DBIO.seq(schema.drop, schema.create))
      }
  }

  def createEntries: Future[Unit] = {
    val setup: DBIOAction[Unit, NoStream, Effect.Write with Effect.Transactional] = DBIO.seq(
      // Insert some persons
      PersonTable ++= Seq(
        PersonTableRow("Arnold Schwarzenegger", "1947-07-30".date),
        PersonTableRow("Bruce Willis", "1955-03-19".date),
        PersonTableRow("Jackie Chan", "1954-04-07".date),
        PersonTableRow("Bruce Lee", "1940-11-27".date),
        PersonTableRow("Sigourney Weaver", "1949-10-08".date),
        PersonTableRow("Harrison Ford", "1942-07-13".date),
        PersonTableRow("Patrick Stewart", "1940-07-13".date),
        PersonTableRow("Kate Mulgrew", "1955-04-29".date)
      )
    ).transactionally
    db.run(setup)
  }

  /**
   * Initializes the database; creates the schema and inserts persons
   */
  def initialize: Future[Unit] = for {
    _ <- dropCreateSchema
    _ <- createEntries
  } yield ()
}
