/*
 * Copyright 2015 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend

import javax.inject.{ Inject, Singleton }

import com.github.dnvriend.UserRepository.UserTableRow
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.{ JdbcBackend, JdbcProfile }
import slick.lifted.ProvenShape

import scala.concurrent.{ ExecutionContext, Future }

object UserRepository {
  final case class UserTableRow(id: Option[Int], first: String, last: String)
}

@Singleton
class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def getProfile: JdbcProfile = profile
  def database: JdbcBackend#DatabaseDef = db

  class UserTable(tag: Tag) extends Table[UserTableRow](tag, "users") {
    def * : ProvenShape[UserTableRow] = (id.?, first, last) <> (UserTableRow.tupled, UserTableRow.unapply)
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def first: Rep[String] = column[String]("first")
    def last: Rep[String] = column[String]("last")
  }

  lazy val UserTable = new TableQuery(tag => new UserTable(tag))

  def dropCreateSchema: Future[Unit] = {
    val schema = UserTable.schema
    db.run(schema.create)
      .recoverWith {
        case t: Throwable =>
          db.run(DBIO.seq(schema.drop, schema.create))
      }
  }

  def createEntries: Future[Unit] = {
    val setup = DBIO.seq(
      // insert some users
      UserTable ++= Seq(
        UserTableRow(None, "Bill", "Gates"),
        UserTableRow(None, "Steve", "Balmer"),
        UserTableRow(None, "Steve", "Jobs"),
        UserTableRow(None, "Steve", "Wozniak")
      )
    ).transactionally
    db.run(setup)
  }

  /**
   * Initializes the database; creates the schema and inserts users
   */
  def initialize: Future[Unit] = for {
    _ <- dropCreateSchema
    _ <- createEntries
  } yield ()
}
