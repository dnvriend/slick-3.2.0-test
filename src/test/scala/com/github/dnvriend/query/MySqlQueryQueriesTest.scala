package com.github.dnvriend.query

import com.github.dnvriend.TestSpec

class MySqlQueryQueriesTest extends TestSpec {
  import akkaPersistenceRepository._
  import slick.jdbc.MySQLProfile.api._

  def statement(query: Query[_, _, Seq]): String =
    query.result.statements.head

  "DML" should "events By Tag And offset And maxNumberOfRecords" in {
    statement(JournalTable
      .filter(_.tags like "%foo%")
      .sortBy(_.ordering.asc)
      .filter(_.ordering >= 1L)
      .take(1L)) shouldBe
      """select `ordering`, `deleted`, `persistence_id`, `sequence_nr`, `message`, `tags` from `JDBC_JOURNAL` where (`tags` like '%foo%') and (`ordering` >= 1) order by `ordering` limit 1"""
  }
}
