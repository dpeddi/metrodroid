/*
 * CompassTest.kt
 *
 * Copyright 2018 Michael Farrell <micolous+git@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package au.id.micolous.metrodroid.test

import au.id.micolous.metrodroid.time.MetroTimeZone
import au.id.micolous.metrodroid.time.Month
import au.id.micolous.metrodroid.time.TimestampFull

import au.id.micolous.metrodroid.card.Card
import au.id.micolous.metrodroid.card.ultralight.UltralightCard
import au.id.micolous.metrodroid.card.ultralight.UltralightPage
import au.id.micolous.metrodroid.transit.yvr_compass.CompassUltralightTransitData
import au.id.micolous.metrodroid.util.ImmutableByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test cases for Vancouver's Compass Card.
 *
 * Adapted from information on http://www.lenrek.net/experiments/compass-tickets/
 */
class CompassTest : BaseInstrumentedTest() {

    private fun createUltralightFromString(cardData: Array<String>): Card {
        val d = TimestampFull(MetroTimeZone.UTC, 2010, Month.FEBRUARY, 1, 0, 0, 0)
        val serial = ImmutableByteArray.fromHex(cardData[1].substring(0, 18))

        val pages = ArrayList<UltralightPage>()
        for (block in 1 until cardData.size) {
            for (p in 0..3) {
                pages.add(UltralightPage(
                        ImmutableByteArray.fromHex(cardData[block].substring(
                                p * 8, (p + 1) * 8)), false)
                )
            }
        }

        return Card(tagId=serial, scannedAt=d,
                    mifareUltralight=UltralightCard("MF0ICU2", pages))
    }

    @Test
    fun testLenrekCards() {
        for (cardData in LENREK_TEST_DATA) {
            val card = createUltralightFromString(cardData)
            val d = card.parseTransitData()
            assertNotNull(d)
            assertTrue(d is CompassUltralightTransitData)

            val cd = d as CompassUltralightTransitData?
            assertEquals(cardData[0], cd!!.serialNumber)

            val ti = card.parseTransitIdentity()
            assertEquals(cardData[0], ti?.serialNumber)
        }
    }

    companion object {
        // Based on data from http://www.lenrek.net/experiments/compass-tickets/tickets-1.0.0.csv
        private val LENREK_TEST_DATA = arrayOf(
                // "Compass Number","Manufacturer's Data","Product Record","Transaction Record","Transaction Record","Ultralight EV1 Configuration"
                arrayOf("0001 0084 2851 9244 6735", "0407AA216AE543814D48000000000000", "0A04002F20018200000000D00000FADC", "46A6020603000012010E0003D979C64E", "C6A602060400001601931705039F14A3"),
                arrayOf("0001 0084 9509 0975 6177", "0407B932EAE14381C948000000000000", "0A08006D200183000000005000004F9A", "465F02010300001B0141000921FF4637", "466102010400002B01411605A2A721EE"),
                arrayOf("0001 0117 0705 0509 1852", "040AA523C29643819648000000000000", "0A04009C1F018200000000500000FAE6", "C06D02FF0100040001000000C946AFE9", "F66D02FF0200000001120003427869CE"),
                arrayOf("0001 0138 6661 2047 7445", "040C9C1C927C3F805148000000000000", "0A0400561F0183000000006000009FA8", "808302FF0100040001000000B9EE8333", "968302FF02000000013D0009D5784BDC"),
                arrayOf("0001 0139 6526 1751 1689", "040CB3338A7743803E48000000000000", "0A080055200183000000006400002FB9", "668502010300001A0133000972C89BFD", "368202FF0200000001410009F574A441"),
                arrayOf("0001 0148 8129 7674 1124", "040D8809D2762F800B48000000000000", "0A0400642101A600000000D000004007", "C66FDCFF0300001CDB070003E57AA9A6", "564CDCFF02000000DB050003C0143A68"),
                arrayOf("0001 0173 7546 5784 1922", "040FCD4E8A7743803E48000000000000", "0A080067200182000000008C00001C32", "003D02FF010004000100000018DF79CA", "00000000000000000000000000008D33"),
                arrayOf("0001 0182 8560 3144 5772", "0410A13D72E143815148000000000000", "0A04007A200183000000009200009802", "4664020403000015010E000342821C3B", "A6640204040000180193170559A6D54A"),
                arrayOf("0001 0194 9556 9667 4571", "0411BB262A8135811F48000000000000", "0A0400551F019F00000000E60000F014", "806B02FF0100040001000000E911CACE", "B66B02FF0200000001460009B2127ABE"),
                arrayOf("0001 0195 3906 2178 6887", "0411C5584ADC43805548000000000000", "0A0400981F0183000000003400005D1B", "C08702FF01000400010000000360C253", "D68702FF02000000015A0003A4789C1A"),
                arrayOf("0001 0204 0448 9371 7760", "04128E10CA5740805D48000000000000", "0A08005A1F0182000000008A0000D779", "409702FF010004000100000070FA5EE5", "00000000000000000000000000007223"),
                arrayOf("0001 0216 9217 4254 2083", "0413BA259A5740800D48000000000000", "0A04002420018200000000B8000003B9", "E08C02FF0100040001000000F5428D17", "868D02060200000001122305E8A63FDE"),
                arrayOf("0001 0226 7706 3942 2729", "04149F07EA5740807D48000000000000", "0A04003120018200000000C00000F817", "607E02FF010004000100000057FEAE04", "767E02FF0200000001410009F57417F2"),
                arrayOf("0001 0237 8396 6655 3610", "0415A138A2E243818248000000000000", "0A040070200182000000007200006BC1", "66A5020603000000010E0003787AF60A", "C6A602060400000B0193170509AD9393"),
                arrayOf("0001 0282 6052 1427 0732", "0419B326EA5740817C48000000000000", "0A04003020018200000000D800005279", "406F02FF0100040001000000B01F35D1", "566F02FF02000000010800034E75577D"),
                arrayOf("0001 0306 5268 3796 6096", "041BE077E25440817748000000000000", "0A0400941F01830000000072000099D6", "C08802FF01000400010000003C0C822D", "000000000000000000000000000016CC"),
                arrayOf("0001 0306 5510 9219 2014", "041BE17672E543815548000000000000", "0A0400931F018200000000860000016F", "864602030500001D013520056BAB4D60", "C64302030400000701A00705BCA78D86"),
                arrayOf("0001 0322 5873 4048 1288", "041D56C7D26243807348000000000000", "0A0800961F0182000000000C00007BA5", "268D02060700003501410009787C3907", "368D02060600003501410009787C795A"),
                arrayOf("0001 0337 0184 2141 3134", "041EA634D25440814748000000000000", "0A04003620018200000000F20000A87D", "266F02000300001601340009AB7AC2E7", "366F02000400001601340009AB7A67C2"),
                arrayOf("0001 0348 0457 7615 7450", "041FA734927C3F815048000000000000", "0A08005E1F0182000000007E00000E33", "E68B02060300005001911C0559A68A9F", "E681021A0200000001BC16054DA22A5D"),
                arrayOf("0001 0388 5580 3814 7847", "042356F9D26243807348000000000000", "0A0800961F01820000000006000097C3", "268D02060500003501410009787C456D", "368D02060400003501410009787C3D7B"),
                arrayOf("0001 0390 8720 3566 4647", "04238C23B2E243809348000000000000", "0A04008D1F018F00000000400000FB4E", "068A020C030000410156000AF551B074", "F68102FF020000000150000A873A663F"),
                arrayOf("0001 0403 1179 5893 1218", "0424A901D24643815648000000000000", "0A04005B2101A600000000540000A19C", "6668F1050500002FF013000371752CA7", "A668F10506000031F0C315057EA3F40B"),
                arrayOf("0001 0404 4816 4316 0339", "0424C961927743812748000000000000", "0A04005A1F018200000000720000AEC7", "666302060500002A0115170551A876E4", "266202060400002001911C05BCA72FC6"),
                arrayOf("0001 0444 1335 6359 8087", "042864C0CA5440805E48000000000000", "0A0400921F018200000000A800008336", "C09202FF010004000100000017DFE3ED", "00000000000000000000000000003BC9"),
                arrayOf("0001 0448 4029 7902 9771", "0428C86C320733818748000000000000", "0A04004C200196000000001000008EDD", "E65C0202030000000102000325090C70", "F65C020202000000017300032509DC73"),
                arrayOf("0001 0460 3040 8795 7773", "0429DD784A2A4681A748000000000000", "0A0800672001820000000076000070C3", "C68B02060700003C0106160562A7DE38", "268B0206060000370101160519AFDD92"),
                arrayOf("0001 0502 9076 0041 3445", "042DBD1C3AE343801A48000000000000", "0A040047200182000000006800006247", "464A02000300001101030003F30C845C", "364802FF02000000010900035F24D943"),
                arrayOf("0001 0512 3243 1752 0645", "042E983A7AE543805C48000000000000", "0A0800881F0182000000005600003CCA", "868302060300001A01FF150520B1A81A", "868302060400001A01FF150520B189F1"),
                arrayOf("0001 0557 8133 3812 0969", "0432BB059A964380CF48000000000000", "0A0800681F0182000000002C000004E2", "E08902FF010004000100000057FEF0C9", "868A021A0200000001BC16058FA3D712"),
                arrayOf("0001 0563 5310 1333 3762", "043340FFBA964380EF48000000000000", "0A0800541F0182000000001C0000C2DF", "A06802FF010004000100000057FED5F0", "866902060200000001911C0503A495CD"),
                arrayOf("0001 0574 5261 2961 1520", "043440F8BA964380EF48000000000000", "0A0800541F018200000000160000E0AF", "A06802FF010004000100000057FEA031", "A66902060200000001911C0503A4C7A3"),
                arrayOf("0001 0587 2029 5075 9680", "043567DEE25440807648000000000000", "0A040047200182000000009A00003282", "A62F02000300000A0133000974C8583C", "762E02FF02000000013700090FD8C2D4"),
                arrayOf("0001 0587 6311 1449 4729", "043571C8DA6243807B48000000000000", "0A08006B20018200000000C400008CC7", "E650021A0300002E0148160517A1742D", "E650021A0400002E0148160517A12F6E"),
                arrayOf("0001 0600 8610 3524 2252", "0436A51FE2DB4381FB48000000000000", "0A08005A1F018200000000DC00007B94", "867F02060300001401FF1505769F0CBF", "867F02060400001401FF1505769FA0AB"),
                arrayOf("0001 0621 2016 8670 0800", "04387FCB7A9643802F48000000000000", "0A080048200182000000000C0000E578", "069002060300005B01911C0539A0200E", "B68402FF0200000001140003634F51C6"),
                arrayOf("0001 0676 5678 1285 5047", "043D8839926A48803048000000000000", "0A04007E200182000000003A0000A504", "069B02060500002901410009217CE4E0", "069E02060600004101911C059AA716CE"),
                arrayOf("0001 0707 4164 1020 2884", "0440569AD26243807348000000000000", "0A0400961F018200000000120000251C", "268D02060500003501410009787CBD66", "368D02060400003501410009787C425B"),
                arrayOf("0001 0787 1256 2729 0888", "0447965DB25740802548000000000000", "0A04009D1F01820000000092000009C2", "404902FF01000400010000007B7D0568", "46830206020000000176010515A93A3F"),
                arrayOf("0001 0803 6129 4547 0763", "044916D3926A48843448000000000000", "0A040081200182000000001A00003741", "26A802060300005501911C05F8A8BCE0", "26A802060400005501911C05F8A89A27"),
                arrayOf("0001 0857 7336 3856 2602", "044E02C0AAE243848F48000000000000", "0A08006920018200000000F00000ED4E", "E08002FF010004000100000087F6FABA", "E683021A0200000001911C05E1B0239B"),
                arrayOf("0001 0873 0787 1665 2804", "044F67A4F2AD3C80E348000000000000", "0A04004E1F0182000000003E0000ADD0", "C0B302FF0100040001000000A7C717A0", "0000000000000000000000000000E5B6"),
                arrayOf("0001 0878 3628 1947 0081", "044FE221FA6243805B48000000000000", "0A04002820018200000000680000563B", "267E021A0300001B01BC160517A143A9", "267E021A0400001B01BC160517A1FA90"),
                arrayOf("0001 0893 2006 2337 9208", "04513CE1729643802748000000000000", "0A04003E20018200000000A80000849A", "808302FF010004000100000059FE31B9", "968302FF0200000001410009F574420A"),
                arrayOf("0001 0906 1835 5401 6004", "04526AB4BAE243809B48000000000000", "0A04002620018200000000980000CE61", "A65402000300000E010100036A758015", "F65202FF02000000010600035E7C6620"),
                arrayOf("0001 0925 9859 4650 2401", "045437EFCA5740805D48000000000000", "0A0800861F0182000000000A0000C7BD", "069902060300001F01911C0519A80C84", "069902060400001F01911C0519A8CEDA"),
                arrayOf("0001 0939 7983 4925 4411", "045579A062AD41810F48000000000000", "0A04003420018200000000F000006E63", "86A102060300001101390009387C2337", "86A102060400001101E7000500B19C12", "000000FF000500000000000000000000"),
                arrayOf("0001 0951 4819 6709 2487", "045689536A774380DE48000000000000", "0A0800701F0182000000006800003AC0", "E07B02FF0100040001000000CFEA8FB0", "867E02060200000001411605E0ABBEE7"),
                arrayOf("0001 0953 2738 4973 9544", "0456B36922EB32827948000000000000", "0A080085200182000000006000008BFB", "468702060300000C01410009917C73D7", "868702060400000E01FF15058FA3AFB9"),
                arrayOf("0001 0991 1567 9888 7709", "045A25F32AE435827948000000000000", "0A08007720018200000000D600009A1A", "06A402060300001B01410009217CC4E0", "86A402060400001F01911C0517A153F0"),
                arrayOf("0001 1013 1912 5841 2807", "045C26F6328135800648000000000000", "0A0400731F018200000000280000897E", "3674021A0300001C0139000949764558", "E681021A0400008A01BC160530AB40CC"),
                arrayOf("0001 1044 7391 0657 1563", "045F04D7BA5540842B48000000000000", "0A08006A20018200000000840000BC8E", "407C02FF01000400010000006C2B51F9", "2683021A0200000001BC160519AF6949"),
                arrayOf("0001 1055 2162 3483 2643", "045FF82BAAE243808B48000000000000", "0A08006C20018200000000860000A7E9", "60A302FF010004000100000087F676B4", "0000000000000000000000000000580E"),
                arrayOf("0001 1096 4867 7715 8406", "0463B956927C3F805148000000000000", "0A04005B20019100000000000000A10B", "6640020303000005014700099116E4E1", "D63F02FF0200000001490009611EBD62"),
                arrayOf("0001 1096 8880 6391 9369", "0463C22DEA5740807D48000000000000", "0A04003620018300000000100000C65D", "064102010300001201200003197EE6A9", "D63E02FF02000000010C00033D7604B4"),
                arrayOf("0001 1123 4337 0251 3922", "04662CC6FAAD3C80EB48000000000000", "0A08006C20018200000000DC0000C755", "46A5020603000012014208059BA76FD9", "06A30206020000000193170548AB3F38"),
                arrayOf("0001 1161 1932 1068 4168", "04699C7922E243800348000000000000", "0A08004220018200000000200000475C", "E6A102060300002D01411605DFA5E383", "E6A102060400002D01411605DFA5D3EF"),
                arrayOf("0001 1189 5573 9333 5046", "046C30D08A964380DF48000000000000", "0A0800571F018200000000D00000D613", "A60502060300000E0141000921FF3A2E", "A60502060400000E01BC16057EAE048F"),
                arrayOf("0001 1199 9512 1419 1407", "046D22C38A964384DB48000000000000", "0A04003C20018200000000B2000031B0", "808302FF0100040001000000270CB28B", "00000000000000000000000000003906"),
                arrayOf("0001 1201 0396 7735 9368", "046D3BDAE25540807748000000000000", "0A040079200182000000003E00000696", "202B02FF01000400010000000B149D8D", "E631021A0200000001BC1605759E9A63"),
                arrayOf("0001 1204 5186 0086 9129", "046D8C6DE25540807748000000000000", "0A0400941F018200000000960000748F", "A06302FF0100040001000000E2174F2B", "B66302FF02000000010600035E7C6FBF"),
                arrayOf("0001 1336 0668 3067 2641", "04798376BAE243809B48000000000000", "0A0800941F018200000000B000005B2E", "669602060500003501D815054FA57D90", "C69102060400001001BC160517A446B4"),
                arrayOf("0001 1345 0204 0486 0164", "047A54A232584080AA48000000000000", "0A08003520018200000000200000ABB6", "C68402030300000E01410009217CC471", "068502030400001001FF150595B1C4EA"),
                arrayOf("0001 1351 4910 4808 8325", "047AEA1CDA6243807B48000000000000", "0A040066200183000000009C0000E42C", "C66C020107000042010F1705B5A2786B", "E66B02010600003B01411605F69E5CDC"),
                arrayOf("0001 1360 1333 8457 2160", "047BB44312B934801F48000000000000", "0A08004D1F018200000000AC0000E065", "C68502060300000101911C0599A300D5", "A68502060200000001911C0599A3A2B7"),
                arrayOf("0001 1423 2035 9392 1287", "0481707D8A7743803E48000000000000", "0A04004F200182000000009A0000F8F1", "A60C020605000032014100093C822423", "B60C020604000032014100093C823EC9"),
                arrayOf("0001 1447 1292 6643 0722", "04839D929A964380CF48000000000000", "0A08005E1F018200000000BC00008553", "A681021A0300001501FF15058FA6DA1D", "A681021A0400001501FF15058FA6AADE"),
                arrayOf("0001 1461 9254 7857 2800", "0484F6FE1AE243803B48000000000000", "0A08006C1F018200000000C200004BEC", "608702FF01000400010000005BFEBB9C", "C687021A0200000001911C05D49D7415"),
                arrayOf("0001 1468 6497 8674 5600", "0485929BAAE243808B48000000000000", "0A04008B1F018200000000E20000B2E6", "807D02FF0100040001000000A7C7FDCF", "B67D02FF02000000013300093C78EAE2"),
                arrayOf("0001 1470 4200 2550 9124", "0485BBB2E25440807648000000000000", "0A0800831F0182000000009C00004C93", "007202FF010004000100000070FAB896", "8672021A0200000001541F0583A9697A"),
                arrayOf("0001 1477 6462 5748 8644", "0486646E220733809648000000000000", "0A040048200182000000001200008B1C", "006E02FF0100040001000000DF94B4CF", "366E02FF0200000001260003A895F8B8"),
                arrayOf("0001 1496 1804 5698 9480", "04881317AA7743841A48000000000000", "0A04006520018200000000620000A74D", "9646021A0300002B013F0009717C5C59", "864602030400002B013F0009717C3BBB"),
                arrayOf("0001 1513 6649 7747 0723", "0489AAAFC25440805648000000000000", "0A08005D20018200000000660000D62B", "468C020605000038014100093C826E78", "E68C02060600003D0141160500A7B17D"),
                arrayOf("0001 1536 5021 4683 5207", "048BBEB97A774380CE48000000000000", "0A08006720018200000000EA0000233F", "669002060300000701410009787C866D", "669502060400002F01EC150559A6578D"),
                arrayOf("0001 1565 5401 7260 4166", "048E626092964380C748000000000000", "0A04004620018200000000A00000FFC8", "E670021A0300004601F11F05A79F7D9F", "2668021A0200000001940005D7A6C9AF"),
                arrayOf("0001 1580 4879 8065 5363", "048FBEBD9A7743802E48000000000000", "0A0400571F0182000000002200004BC7", "A09102FF0100040001000000A9C70CB1", "0000000000000000000000000000A7AC"),
                arrayOf("0001 1581 4678 4215 9365", "048FD5D66AE543804C48000000000000", "0A04003920018200000000EA0000FC3A", "C68702060300000E01020003737A8F26", "168602FF0200000001050003B176096B"),
                arrayOf("0001 1598 8408 9901 9529", "04916974EA6243804B48000000000000", "0A0800761F018200000000820000A20C", "C07D02FF01000400010000006FFA2B5E", "00000000000000000000000000000F10"),
                arrayOf("0001 1616 5697 6392 3247", "04930619B2A74084D148000000000000", "0A08006A20018200000000A2000015DA", "E67402030300003B010B000351745F92", "F67402030400003B010B00035174DEB8"),
                arrayOf("0001 1649 5551 1275 6520", "0496061CB2A74084D148000000000000", "0A08006A20018200000000A8000018DC", "606D02FF010004000100000059FECF2F", "966D02FF0200000001410009F5741AA6"),
                arrayOf("0001 1656 7357 1599 2322", "0496ADB7E26243804348000000000000", "0A0800901F0183000000003C00002131", "408402FF0100040001000000A8C78465", "468D02060200000001911C050DA48767"),
                arrayOf("0001 1668 9965 8867 5842", "0497CBD05AE143807848000000000000", "0A04003620018200000000280000D108", "468D020603000033012F1B0520AF9D26", "E686021A0200000001C2230553AA1F0A"),
                arrayOf("0001 1678 7259 4854 7849", "0498ADB9E26243804348000000000000", "0A0800901F0182000000004200002940", "608402FF0100040001000000A8C7EFF8", "468D02060200000001911C050DA427BA"),
                arrayOf("0001 1735 8839 9408 0008", "049DE0F1B2E243809348000000000000", "0A04004620019E00000000740000FB03", "66AC020603000000013600092A7621DC", "76AC020602000000017300092A760739"),
                arrayOf("0001 1753 8382 2943 1043", "049F8291BA7743800E48000000000000", "0A080043200183000000006000005A24", "208402FF0100040001000000707D234E", "0685021A0200000001040105B79EE6B9"),
                arrayOf("0001 1798 1703 2401 0242", "04A38AA5EA5740807D48000000000000", "0A0800302001820000000074000046EB", "A05B02FF0100040001000000AAC7A257", "00000000000000000000000000008BF1"),
                arrayOf("0001 1828 5733 2179 0720", "04A64E64CA5440805E48000000000000", "0A0800991F018200000000640000F26D", "0686021A0300000D01BC160541A88F81", "0686021A0400000D01BC160541A8F74E"),
                arrayOf("0001 1882 9476 0775 8085", "04AB4067CA5440805E48000000000000", "0A08002220018200000000920000F335", "E05A02FF01000400010000003B0C5E27", "0000000000000000000000000000D913"),
                arrayOf("0001 1893 8757 0430 8487", "04AC3F1F3ADC43802548000000000000", "0A08007720018300000000560000CB84", "E67D021A0300002401BC16054EAA2D76", "E67D021A0400002401BC16054EAA1AC1"),
                arrayOf("0001 1901 4442 4788 8644", "04ACEFCF72E543805448000000000000", "0A040026200182000000000A000064CE", "008102FF0100040001000000270C5ACB", "000000000000000000000000000067B5"),
                arrayOf("0001 1963 0380 3662 2084", "04B289B78A7C3F804948000000000000", "0A04002D20018400000000AE000075DA", "E68402020500003401110003487D3482", "E68502020600003C012823053CAABCB4"),
                arrayOf("0001 1974 1337 9195 0080", "04B38BB4E25740807548000000000000", "0A08005C1F018200000000E400008614", "A683020303000003013F000950F7678C", "268702030400001F01551B0530AFCF01"),
                arrayOf("0001 1974 4949 2480 8961", "04B394AB4ADC43805548000000000000", "0A040072200182000000003600008743", "C6AC020605000059011517052AA10846", "66A602060400002601931705689D0277"),
                arrayOf("0001 2041 7272 3437 6964", "04B9B184AAA74080CD48000000000000", "0A08003C200182000000000E0000F8EF", "C00202FF01000400010000005AFE1A2D", "E60202060200000001FF15053DA3E782"),
                arrayOf("0001 2042 5459 3106 8166", "04B9C4F1BA7743800E48000000000000", "0A04003E20018200000000C200008262", "E6A6020603000011012000037D764E31", "C6A70206040000180173030507AC8E4E"),
                arrayOf("0001 2074 5743 2781 6963", "04BCAE9E729643802748000000000000", "0A04003620018200000000BE00001E02", "869A02060500002801521705C7A3A079", "C69802060400001A01BC160548A90BC8"),
                arrayOf("0001 2082 3240 3448 9625", "04BD6253E27034822448000000000000", "0A08008B20018200000000D00000C428", "606502FF01000400010000005AFE80EC", "C66602060200000001BC1605FE9F27EE"),
                arrayOf("0001 2085 7855 1432 0646", "04BDB3827A774380CE48000000000000", "0A0400441F0183000000000000005C55", "E63E0200030000090109070539A87436", "D63D02FF0200000001010705D19EEBF5"),
                arrayOf("0001 2085 8378 5923 4567", "04BDB485B27743800648000000000000", "0A04003D200183000000005600003852", "606402FF010004000100000061D32827", "E669021A0200000001911C05BCA2F677"),
                arrayOf("0001 2101 7386 1970 8165", "04BF2615EA6243804B48000000000000", "0A04009C1F018200000000F000002661", "E09102FF0100040001000000FF5F05F6", "A695020602000000013F030575AE67AC"),
                arrayOf("0001 2164 2210 8251 0081", "04C4D59DB2E243809348000000000000", "0A08004720018200000000E20000DC97", "008202FF010004000100000079CF89BA", "000000000000000000000000000060DB"),
                arrayOf("0001 2164 5203 1792 0001", "04C4DC94AA7743801E48000000000000", "0A08006D20018200000000B60000E56D", "46870203030000050141000921FFE3FD", "B68602FF020000000140000939FB887B"),
                arrayOf("0001 2218 5886 5759 1043", "04C9C7828AE24380AB48000000000000", "0A04003020018200000000FE00004BF2", "464902000300000401050003287664F4", "D64802FF02000000010400030A793C28"),
                arrayOf("0001 2223 1023 4997 6324", "04CA3076A2964380F748000000000000", "0A04005C1F018300000000D00000F39C", "804202FF010004000100000059FE7FC6", "0000000000000000000000000000E2EB"),
                arrayOf("0001 2225 0028 3039 8723", "04CA5C1AE25540807748000000000000", "0A0400951F018200000000EC0000046C", "E05A02FF010004000100000060D3E96B", "00000000000000000000000000002778"),
                arrayOf("0001 2233 6343 7248 6407", "04CB2562DA5540804F48000000000000", "0A08006E200182000000005600002385", "C06902FF010004000100000062D3464D", "666D02060200000001FF1505AAA283BC"),
                arrayOf("0001 2238 7709 4261 1200", "04CB9DDA72AD41801E48000000000000", "0A04003C200183000000001C0000CCBF", "8684020005000034010500037976269E", "9684020004000034010500037976F432", "000000FF000500000000000000000000"),
                arrayOf("0001 2246 2361 0443 1365", "04CC4B0B428235807548000000000000", "0A0400871F018200000000BA0000D58C", "800402FF01000400010000003F85C67E", "860602060200000001730305EF9E868A"),
                arrayOf("0001 2263 5153 0787 7120", "04CDDD9C92964380C748000000000000", "0A04005A20019100000000CE00006453", "6631020303000004014700099116A789", "F63002FF02000000014900096C1EC14E"),
                arrayOf("0001 2263 5662 6735 2323", "04CDDE9FC25440805648000000000000", "0A0800901F018200000000F20000D1BC", "004F02FF01000400010000007A7DD98C", "164F02FF0200000001200003447E17BB"),
                arrayOf("0001 2354 5511 2560 7688", "04D6257F2A584080B248000000000000", "0A080041200182000000009A00009C64", "267E02060700005C01410009217C2053", "B67D020606000058014000095F7C72D5"),
                arrayOf("0001 2359 5091 2782 4649", "04D698C29A5740800D48000000000000", "0A04004A200182000000000400008B19", "667A02060300001F01010003210588BC", "C67A0206040000220181030573AAD6EE"),
                arrayOf("0001 2387 7768 1462 1440", "04D92A7FC2E24380E348000000000000", "0A0400961F018200000000200000155D", "608402FF01000400010000007FA4520D", "0000000000000000000000000000E64A"),
                arrayOf("0001 2434 4804 8268 4185", "04DD6A3B2A703482EC48000000000000", "0A04007B20018200000000BA00007F6E", "A60C02060300000001410009217C11C1", "B60C02060200000001730009217CF063"),
                arrayOf("0001 2437 1299 2231 5523", "04DDA7F6DA5440804E48000000000000", "0A04004C2001820000000084000047EA", "869602060500002C0141000922FF5D5C", "A69802060600003D0141160567A4DB2B"),
                arrayOf("0001 2479 6850 8949 8906", "04E186EBAAE43582F948000000000000", "0A08008520018200000000B20000632C", "96A10206050000230134000959CC661E", "86A10206060000230134000959CC9A3A"),
                arrayOf("0001 2573 4884 6633 8604", "04EA0E68B2964384E348000000000000", "0A04004B20018200000000D400006B57", "068E021A0300001F018121050CB19749", "068E02060400001F018121050CB14077"),
                arrayOf("0001 2581 8623 6019 5841", "04EAD1B7AAE243808B48000000000000", "0A04007820018200000000EA00004590", "664F0200030000130133000973C83A38", "164D02FF02000000013C000998EB179D"),
                arrayOf("0001 2591 0212 8685 1841", "04EBA6C1EA5740807D48000000000000", "0A08009D1F018200000000EE000023CC", "008302FF010004000100000048D72891", "000000000000000000000000000096E7"),
                arrayOf("0001 2596 0719 9302 5325", "04EC1C7C82E54384A048000000000000", "0A080033200182000000004E000070CC", "A66C02060300000A01070003288145F0", "466D02060400000F01AC020548A036F9"),
                arrayOf("0001 2598 0584 1343 3609", "04EC4A2AC2E24380E348000000000000", "0A04006C20018200000000940000961F", "269F02060300003901911C05B7A8A0DD", "269F02060400003901911C05B7A88BA7"),
                arrayOf("0001 2636 0674 6170 2405", "04EFBFDCBA7743800E48000000000000", "0A0400951F018200000000D600006322", "46AF02060300002801050003937B35FF", "56AA02FF0200000001120003FD7E4F3B"),
                arrayOf("0001 2652 6836 3674 4962", "04F1423F9A964380CF48000000000000", "0A040063200182000000000E0000BB5E", "86AC02060500001C013F0009667CFB55", "76AB020604000013013C0009B5768C40"),
                arrayOf("0001 2681 4625 8166 6560", "04F3E09FAA7743801E48000000000000", "0A08005E1F018200000000E000001883", "608B02FF010004000100000059FED5BF", "E68B02060200000001911C0559A63B09"),
                arrayOf("0001 2707 8270 4206 7204", "04F6463C82E54380A448000000000000", "0A04008C20018200000000A80000AE75", "C67702060700003B010E0003757ABE66", "467802060800003F0193170520B1EEBD"),
                arrayOf("0001 2718 2194 3489 4089", "04F738437A624380DB48000000000000", "0A04003D200182000000002E0000875E", "A04502FF0100040001000000CA46C62C", "2648021A020000000193170577ABBBA6"),
                arrayOf("0001 2720 1293 5316 3521", "04F7641FF26243805348000000000000", "0A08002220018200000000E00000DE27", "006802FF0100040001000000797D228F", "166802FF0200000001200003EC78643E"),
                arrayOf("0001 2763 4508 4330 8809", "04FB55229A964380CF48000000000000", "0A08006C20018200000000180000C016", "C676021A0300003201031705769F0E16", "8670021A0200000001911C0508A4570F"),
                arrayOf("0001 2809 4028 7308 6728", "04FF83F0820733803648000000000000", "0A08002B200182000000008E0000228C", "405602FF0100040001000000902547CA", "0000000000000000000000000000708F"),
                arrayOf("0001 2810 6914 3998 3363", "04FFA1D2827C3F804148000000000000", "0A04006C1F0183000000005200004E8F", "46870201030000190136000929D46CB7", "368402FF0200000001410009F5740201"))
    }
}
