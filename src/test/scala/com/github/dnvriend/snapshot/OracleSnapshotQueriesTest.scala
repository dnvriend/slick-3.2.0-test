package com.github.dnvriend.snapshot

import com.github.dnvriend.AkkaPersistenceRepository.SnapshotRow
import com.github.dnvriend.TestSpec

class OracleSnapshotQueriesTest extends TestSpec {
  import akkaPersistenceRepository._
  import slick.jdbc.OracleProfile.api._

  def statement(query: Query[_, _, Seq]): String =
    query.result.statements.head

  def insertSnapshotStatement(snapshotRow: SnapshotRow): String =
    SnapshotTable.insertOrUpdate(snapshotRow).statements.head

  def selectAll(persistenceId: String) =
    SnapshotTable.filter(_.persistenceId === persistenceId).sortBy(_.sequenceNumber.desc)

  "DDL" should "generate create table statement" in {
    SnapshotTable.schema.createStatements.toList.head shouldBe
      """create table "JDBC_SNAPSHOTS" ("persistence_id" VARCHAR(255) NOT NULL,"sequence_nr" BIGINT NOT NULL,"created" BIGINT NOT NULL,"snapshot" BLOB NOT NULL)"""
  }

  it should "generate primary key statement" in {
    SnapshotTable.schema.createStatements.toList.drop(1).head shouldBe
      """alter table "JDBC_SNAPSHOTS" add constraint "snapshot_pk" primary key("persistence_id","sequence_nr")"""
  }

  "DML" should "select all for update statement" in {
    statement(selectAll("pid")) shouldBe
      """"""
  }

  it should "select By PersistenceId And Max SeqNr" in {
    statement(selectAll("pid").take(1)) shouldBe
      """"""
  }

  it should "select By PersistenceId And SeqNr" in {
    statement(selectAll("pid").filter(_.sequenceNumber === 1L)) shouldBe
      """"""
  }

  it should "select By PersistenceId And MaxTimestamp" in {
    statement(selectAll("pid").filter(_.created <= 1L)) shouldBe
      """"""
  }

  it should "select By PersistenceId And Max SequenceNr" in {
    statement(selectAll("pid").filter(_.sequenceNumber <= 1L)) shouldBe
      """"""
  }

  it should "select By PersistenceId And MaxSequenceNr And MaxTimestamp" in {
    statement(selectAll("pid").filter(_.sequenceNumber <= 1L).filter(_.created <= 1L)) shouldBe
      """"""
  }

  it should "insert a snapshot row" in {
    insertSnapshotStatement(SnapshotRow("pid", 1L, 1L, Array.empty[Byte])) shouldBe
      """"""
  }
}
