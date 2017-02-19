package com.github.dnvriend

import javax.inject.{ Inject, Singleton }

import com.github.dnvriend.AkkaPersistenceRepository.{ JournalRow, SnapshotRow }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.{ JdbcBackend, JdbcProfile }
import slick.lifted.{ PrimaryKey, ProvenShape }

import scala.concurrent.{ ExecutionContext, Future }

object AkkaPersistenceRepository {
  final case class JournalRow(ordering: Long, deleted: Boolean, persistenceId: String, sequenceNumber: Long, message: Array[Byte], tags: Option[String] = None)
  final case class SnapshotRow(persistenceId: String, sequenceNumber: Long, created: Long, snapshot: Array[Byte])
}

@Singleton
class AkkaPersistenceRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  def getProfile: JdbcProfile = profile
  def database: JdbcBackend#DatabaseDef = db

  class JournalTable(tag: Tag) extends Table[JournalRow](tag, "JDBC_JOURNAL") {
    def * : ProvenShape[JournalRow] = (ordering, deleted, persistenceId, sequenceNumber, message, tags) <> (JournalRow.tupled, JournalRow.unapply)

    val ordering: Rep[Long] = column[Long]("ordering", O.AutoInc)
    val persistenceId: Rep[String] = column[String]("persistence_id", O.Length(255, varying = true))
    val sequenceNumber: Rep[Long] = column[Long]("sequence_nr")
    val deleted: Rep[Boolean] = column[Boolean]("deleted", O.Default(false))
    val tags: Rep[Option[String]] = column[Option[String]]("tags", O.Length(255, varying = true))
    val message = column[Array[Byte]]("message")
    val pk: PrimaryKey = primaryKey("journal_pk", (persistenceId, sequenceNumber))
  }

  lazy val JournalTable = TableQuery[JournalTable]

  class SnapshotTable(tag: Tag) extends Table[SnapshotRow](tag, "JDBC_SNAPSHOTS") {
    def * : ProvenShape[SnapshotRow] = (persistenceId, sequenceNumber, created, snapshot) <> (SnapshotRow.tupled, SnapshotRow.unapply)

    val persistenceId: Rep[String] = column[String]("persistence_id", O.Length(255, varying = true))
    val sequenceNumber: Rep[Long] = column[Long]("sequence_nr")
    val created: Rep[Long] = column[Long]("created")
    val snapshot: Rep[Array[Byte]] = column[Array[Byte]]("snapshot")
    val pk: PrimaryKey = primaryKey("snapshot_pk", (persistenceId, sequenceNumber))
  }

  lazy val SnapshotTable = TableQuery[SnapshotTable]

  def dropCreateSchema: Future[Unit] = {
    val schema = JournalTable.schema ++ SnapshotTable.schema
    db.run(schema.create)
      .recoverWith {
        case t: Throwable =>
          db.run(DBIO.seq(schema.drop, schema.create))
      }
  }

  def createEntries: Future[Unit] =
    Future.successful(())

  /**
   * Initializes the database;
   */
  def initialize: Future[Unit] = for {
    _ <- dropCreateSchema
    _ <- createEntries
  } yield ()
}
