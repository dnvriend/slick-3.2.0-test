package com.github.dnvriend.snapshot

import com.github.dnvriend.AkkaPersistenceRepository.SnapshotRow
import com.github.dnvriend.TestSpec

class SnapshotQueriesTest extends TestSpec {
  import profile.api._
  import akkaPersistenceRepository._

  def selectAll(persistenceId: String): Query[Snapshot, SnapshotRow, Seq] =
    SnapshotTable.filter(_.persistenceId === persistenceId).sortBy(_.sequenceNumber.desc)

  "DDL" should "generate create table statement" in {
    SnapshotTable.schema.createStatements.toList.head shouldBe
      """create table "JDBC_SNAPSHOTS" ("persistence_id" VARCHAR(255) NOT NULL,"sequence_nr" BIGINT NOT NULL,"created" BIGINT NOT NULL,"snapshot" BLOB NOT NULL)"""
  }

  it should "generate primary key statement" in {
    SnapshotTable.schema.createStatements.toList.drop(1).head shouldBe
      """alter table "JDBC_SNAPSHOTS" add constraint "snapshot_pk" primary key("persistence_id","sequence_nr")"""
  }

  it should "select all" in {
    val persistenceId = ""
    selectAll("pid").toString
  }

  it should "select By PersistenceId And Max SeqNr" in {

  }
}
