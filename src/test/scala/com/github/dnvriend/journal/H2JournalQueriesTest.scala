package com.github.dnvriend.journal

import com.github.dnvriend.TestSpec
import slick.jdbc.JdbcProfile

abstract class JournalDDLTest(val p: JdbcProfile) extends TestSpec {
  import akkaPersistenceRepository._
  import p.api._

  "DDL" should "generate create table statement" in {

    val ddl = JournalTable.schema.createStatements.toList
    println(ddl)
  }
}

class H2JournalDDLTest extends JournalDDLTest(slick.jdbc.H2Profile)

class PGJournalDDLTest extends JournalDDLTest(slick.jdbc.PostgresProfile)

class MyDDLTest extends JournalDDLTest(slick.jdbc.MySQLProfile)

//class H2JournalQueriesTest extends TestSpec {
//  import akkaPersistenceRepository._
//  import slick.jdbc.H2Profile.api._
//  import pprint.Config.Colors._
//
//  "DDL" should "generate create table statement" in {
//    val ddl = JournalTable.schema.createStatements.toList
//    pprint.pprintln(ddl)
//  }

//  it should "generate primary key statement" in {
//    SnapshotTable.schema.createStatements.toList.drop(1).head shouldBe
//      """alter table "JDBC_SNAPSHOTS" add constraint "snapshot_pk" primary key("persistence_id","sequence_nr")"""
//  }

//  def selectAllJournalForPersistenceId(persistenceId: String) =
//    JournalTable.filter(_.persistenceId === persistenceId).sortBy(_.sequenceNumber.desc)
//
//  def statement(query: Query[_, _, Seq]): String =
//    query.result.statements.head
//
//  "DML" should "mark entries as deleted" in {
//    JournalTable
//      .filter(_.persistenceId === "pid")
//      .filter(_.sequenceNumber <= 1L)
//      .filter(_.deleted === false)
//      .map(_.deleted).update(true).statements.head shouldBe
//      """update "JDBC_JOURNAL" set "deleted" = ? where (("JDBC_JOURNAL"."persistence_id" = 'pid') and ("JDBC_JOURNAL"."sequence_nr" <= 1)) and ("JDBC_JOURNAL"."deleted" = false)"""
//  }
//
//  it should "find highest SequenceNr For PersistenceId" in {
//    statement(selectAllJournalForPersistenceId("pid").map(_.sequenceNumber).take(1)) shouldBe
//      """select "sequence_nr" from "JDBC_JOURNAL" where "persistence_id" = 'pid' order by "sequence_nr" desc limit 1"""
//  }
//
//  it should "select By PersistenceId And Max SequenceNumber" in {
//    statement(selectAllJournalForPersistenceId("pid").filter(_.sequenceNumber <= 1L)) shouldBe
//      """select "ordering", "deleted", "persistence_id", "sequence_nr", "message", "tags" from "JDBC_JOURNAL" where ("persistence_id" = 'pid') and ("sequence_nr" <= 1) order by "sequence_nr" desc"""
//  }
//
//  it should "select PersistenceIds Distinct" in {
//    statement(JournalTable.map(_.persistenceId).distinct) shouldBe
//      """select distinct "persistence_id" from "JDBC_JOURNAL""""
//  }
//
//  it should "select By set of PersistenceIds" in {
//    val query = for {
//      query <- JournalTable.map(_.persistenceId)
//      if query inSetBind Set("pid1", "pid2")
//    } yield query
//
//    statement(query) shouldBe
//      """select "persistence_id" from "JDBC_JOURNAL" where "persistence_id" in (?, ?)"""
//  }
//
//  it should "select by persistenceId And fromSequenceNr And toSequenceNr And limit" in {
//    statement(JournalTable
//      .filter(_.persistenceId === "pid")
//      .filter(_.deleted === false)
//      .filter(_.sequenceNumber >= 1L)
//      .filter(_.sequenceNumber <= 1L)
//      .sortBy(_.sequenceNumber.asc)
//      .take(1L)) shouldBe
//      """select "ordering", "deleted", "persistence_id", "sequence_nr", "message", "tags" from "JDBC_JOURNAL" where ((("persistence_id" = 'pid') and ("deleted" = false)) and ("sequence_nr" >= 1)) and ("sequence_nr" <= 1) order by "sequence_nr" limit 1"""
//  }
//}
