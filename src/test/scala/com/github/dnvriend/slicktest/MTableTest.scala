package com.github.dnvriend.slicktest

import com.github.dnvriend.TestSpec
import slick.jdbc.meta.{ MIndexInfo, MQName, MTable }

class MTableTest extends TestSpec {

  import profile.api._

  it should "have some 'string' methods" in {
    MTable.getTables.getDumpInfo.mainInfo shouldBe "[]"
    MTable.getTables.getDumpInfo.attrInfo shouldBe ""
    MTable.getTables.getDumpInfo.name shouldBe "StreamingResultAction"
    MTable.getTables.getDumpInfo.getNamePlusMainInfo shouldBe "StreamingResultAction []"
  }

  it should "return table meta data" in {
    val tableMetaData: Vector[MTable] = db.run(MTable.getTables).futureValue
    tableMetaData shouldBe Vector(
      MTable(MQName(Option("PLAY"), Option("PUBLIC"), "COFFEES"), "TABLE", "", None, None, None),
      MTable(MQName(Option("PLAY"), Option("PUBLIC"), "PERSONS"), "TABLE", "", None, None, None),
      MTable(MQName(Option("PLAY"), Option("PUBLIC"), "SUPPLIERS"), "TABLE", "", None, None, None),
      MTable(MQName(Option("PLAY"), Option("PUBLIC"), "users"), "TABLE", "", None, None, None)
    )
    val coffeesTableMetaData = tableMetaData.find(_.name.name == "COFFEES")
    val coffeesTableIndexInfo: Vector[MIndexInfo] = db.run(coffeesTableMetaData.value.getIndexInfo(unique = false, approximate = false)).futureValue
    coffeesTableIndexInfo shouldBe
      Vector(
        MIndexInfo(
          MQName(Option("PLAY"), Option("PUBLIC"), "COFFEES"),
          nonUnique = false,
          Option("PLAY"),
          Option("PRIMARY_KEY_63"),
          3,
          1,
          Option("COF_NAME"),
          Option(true),
          0,
          0,
          Option("")
        ),
        MIndexInfo(
          MQName(Option("PLAY"), Option("PUBLIC"), "COFFEES"),
          nonUnique = true,
          Option("PLAY"),
          Option("SUP_FK_INDEX_6"),
          3,
          1,
          Option("SUP_ID"),
          Option(true),
          0,
          0,
          Option("")
        )
      )
  }
}
