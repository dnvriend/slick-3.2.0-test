package com.github.dnvriend.query

import com.github.dnvriend.TestSpec

class OracleQueryQueriesTest extends TestSpec {
  import akkaPersistenceRepository._
  import slick.jdbc.OracleProfile.api._

  def statement(query: Query[_, _, Seq]): String =
    query.result.statements.head

  "DML" should "events By Tag And offset And maxNumberOfRecords" in {
    statement(JournalTable
      .filter(_.tags like "%foo%")
      .sortBy(_.ordering.asc)
      .filter(_.ordering >= 1L)
      .take(1L)) shouldBe
      """select x2.x3, x2.x4, x2.x5, x2.x6, x2.x7, x2.x8 from (select "persistence_id" as x5, "tags" as x8, "message" as x7, "deleted" as x4, "ordering" as x3, "sequence_nr" as x6 from "JDBC_JOURNAL" where ("tags" like '%foo%') and ("ordering" >= 1) order by "ordering") x2 where rownum <= 1"""
  }
}
