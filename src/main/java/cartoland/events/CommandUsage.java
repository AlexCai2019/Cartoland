package cartoland.events;

import cartoland.Cartoland;
import cartoland.commands.*;
import cartoland.commands.TicTacToeCommand;
import cartoland.mini_games.IMiniGame;
import cartoland.utilities.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

import static cartoland.commands.ICommand.*;

/**
 * {@code CommandUsage} is a listener that triggers when a user uses slash command. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class CommandUsage extends ListenerAdapter
{
	/**
	 * The key of this map is the n of a command, and the value is the execution.
	 */
	private final Map<String, ICommand> commands = new HashMap<>();
	/**
	 * The key of this map is the n of a game, and the value is the actual game.
	 */
	private final Map<Long, IMiniGame> games = new HashMap<>();
	public Map<Long, IMiniGame> getGames()
	{
		return games;
	}

	/**
	 * 396 images about Megumin.
	 */
	private static final I[] MEGUMIN_IMAGES = {new I("_mexif", 1375088851446755332L),new I("_Mizugasi", 1444998923739824131L),new I("_Rivia_", 1370370105696161794L),new I("_ukoyr", 1544612178556121088L),new I("024NISHI024", 1396492351346860038L),new I("1G9Xw69kePSLI6p", 1420731295156477954L),new I("3CDtvmmpuqKhl7J", 1471803818048634884L),new I("6gcYq8nXYHQwui5", 1332594611156500487L),new I("7egTXS0GKWh3wGP", 1334542548497387521L),new I("7egTXS0GKWh3wGP", 1342110371662888960L),new I("7egTXS0GKWh3wGP", 1346091304124497920L),new I("7egTXS0GKWh3wGP", 1416720114066415617L),new I("7egTXS0GKWh3wGP", 1455918876990996489L),new I("7egTXS0GKWh3wGP", 1485297876947566594L),new I("7egTXS0GKWh3wGP", 1527310236247560193L),new I("7egTXS0GKWh3wGP", 1533389156755603456L),new I("7egTXS0GKWh3wGP", 1557015010668609536L),new I("7egTXS0GKWh3wGP", 1595465406420221952L),new I("7egTXS0GKWh3wGP", 1606956350596317185L),new I("7egTXS0GKWh3wGP", 1639303735951384576L),new I("7egTXS0GKWh3wGP", 1644717774214959106L),new I("7egTXS0GKWh3wGP", 1668961956404002816L),new I("9aBGoLUanvcjd7O", 1599205425127501824L),new I("ahmadjendro", 1488530273373683713L),new I("Aloe_04maho", 1342795265724436481L),new I("Aloe_04maho", 1360906615008886784L),new I("Aloe_04maho", 1403669632108564485L),new I("Aloe_04maho", 1418163778487652354L),new I("Aloe_04maho", 1423601843368730625L),new I("Aloe_04maho", 1445719564185075722L),new I("Aloe_04maho", 1573801227376214017L),new I("Aloe_04maho", 1633030821388099585L),new I("AONAGINEMURI", 1390290873951285248L),new I("askr_otr", 1468201641723965447L),new I("atasom", 1343169561680519169L),new I("az210309", 1383254021201297414L),new I("az210309", 1391411800420151310L),new I("bhive003", 1382662692729618434L),new I("bhive003", 1421734667326132225L),new I("bhive003", 1506027874825895938L),new I("bhive003", 1576769547117293572L),new I("bhive003", 1588833560546676737L),new I("BNC_0116", 1120180446392807424L),new I("BNC_0116", 1123935283995713543L),new I("BNC_0116", 1128289624126738432L),new I("BNC_0116", 1129402831587274752L),new I("BNC_0116", 1211311585571917825L),new I("BNC_0116", 1228335403221966849L),new I("BNC_0116", 1353732864177623040L),new I("cachi_lo", 1423468322893357056L),new I("catharpoon", 1642908738276458503L),new I("catharpoon", 1395598782675357698L),new I("catharpoon", 1428436939015741440L),new I("chakichaki33", 1529055947767050240L),new I("chakichaki33", 1533765708513148928L),new I("CHILLY_karon", 1378395239845482499L),new I("Coconatnuts09", 1449003566610325504L),new I("creates_blue", 1389692031606267904L),new I("creates_blue", 1393257386287144963L),new I("creates_blue", 1394877336516907010L),new I("creates_blue", 1400192682480136194L),new I("creates_blue", 1630805213530685441L),new I("cyken0718", 1654300719732891654L),new I("D2gg1cPdO8bfjFS", 1333391702132867073L),new I("D2gg1cPdO8bfjFS", 1342283498783002626L),new I("D2gg1cPdO8bfjFS", 1256453504299757569L),new I("dai_gazacy", 1333072335717289986L),new I("DlrfMimikyu", 1352830077696962560L),new I("DlrfMimikyu", 1362948691376619524L),new I("DlrfMimikyu", 1400129807925628933L),new I("E_GA_KU", 1596505160175747072L),new I("Edacchi73", 1487748613908615182L),new I("EJAMIARTSTUDIO", 1370264344903045120L),new I("ekakibegaxa", 1652202562052059136L),new I("Fagi_Kakikaki", 1416334656371269634L),new I("Fagi_Kakikaki", 1558410423837523970L),new I("FevJF0EICCKnLlD", 1347026153098211331L),new I("gakkari_orz", 1388805801859452928L),new I("gakkari_orz", 1538592731962179587L),new I("Gasolineillust", 1565325467850518529L),new I("gazercide", 1373256680763396106L),new I("GReeeeNmodoki2", 1599057455702765568L),new I("GuRa_fgo", 1348538564900917248L),new I("GuRa_fgo", 1352926702398185473L),new I("HagGy0327_", 1338114471143739392L),new I("hagi_neco", 1393868573106327563L),new I("hagi_neco", 1429373932159660032L),new I("hapycloud", 1343423407442948101L),new I("harehareota", 1598270723747098624L),new I("harehareota", 1638859837034610692L),new I("Harimax8", 1383359465366323209L),new I("Harimax8", 1422891821772906500L),new I("Harimax8", 1538112903555215360L),new I("Harimax8", 1596347482916950018L),new I("Harimax8", 1665927714610761729L),new I("Harururu0526", 1660971335517106176L),new I("hatuganookome", 1344206596310761472L),new I("he_shan123", 1557726596202606595L),new I("HereticxxA", 1398405163677671425L),new I("Hi_me_520So", 1399677759698337797L),new I("hwa_n01", 1353356379889700864L),new I("icetea_art", 1528012740656345088L),new I("icetea_art", 1545769743151353856L),new I("icetea_art", 1553379890128306177L),new I("icetea_art", 1555916614138150912L),new I("icetea_art", 1573673618172624896L),new I("icetea_art", 1593967339036450820L),new I("icetea_art", 1626944636525903873L),new I("icetea_art", 1639598009716707329L),new I("isonnoha", 1504494884765396994L),new I("Ixy", 1334516599760490498L),new I("Ixy", 1353673446719250433L),new I("Ixy", 1391319694859137024L),new I("Ixy", 1417105811650727936L),new I("Ixy", 1599059007498772480L),new I("J02GGOgpQ6dueGu", 1336638090828341250L),new I("J02GGOgpQ6dueGu", 1336868237141704705L),new I("J02GGOgpQ6dueGu", 1345199258220126216L),new I("J02GGOgpQ6dueGu", 1345279854040649728L),new I("J02GGOgpQ6dueGu", 1352167601951002624L),new I("J02GGOgpQ6dueGu", 1355069629840855042L),new I("J02GGOgpQ6dueGu", 1358677301731368964L),new I("J02GGOgpQ6dueGu", 1367304489468796932L),new I("J02GGOgpQ6dueGu", 1378316365128331264L),new I("J02GGOgpQ6dueGu", 1380295158676090880L),new I("J02GGOgpQ6dueGu", 1467024737448886274L),new I("JackRockhardt", 1421495091001135106L),new I("JAM_pentail", 1374333181684375556L),new I("JAM_pentail", 1379418416046477312L),new I("JAM_pentail", 1496121346824232966L),new I("jirafuru1", 1385345798372741120L),new I("kadokawasneaker", 1463416144765751300L),new I("kadokawasneaker", 1463709023455399936L),new I("kaiiruuu", 1435533706161102852L),new I("kamindani", 1364578421406199822L),new I("karst_bunny", 1487062233343426561L),new I("karst_bunny", 1502885083614973952L),new I("karst_bunny", 1577615125720485888L),new I("kasumiM224", 1408089488358723585L),new I("kasumiM224", 1530582872184483840L),new I("kgs_jf", 1459384335799562241L),new I("kisasageDrawing", 1571040020219428865L),new I("KMTM_kmk0819", 1342130873785745411L),new I("KMTM_kmk0819", 1356814844792504321L),new I("KMTM_kmk0819", 1388147012881707009L),new I("KMTM_kmk0819", 1400465949963145226L),new I("KMTM_kmk0819", 1446105335723298817L),new I("KMTM_kmk0819", 1573212323644575744L),new I("KMTM_kmk0819", 1606986710520565761L),new I("KMTM_kmk0819", 1614200785335623682L),new I("KMTM_kmk0819", 1643237268717699078L),new I("KMTM_kmk0819", 1663893703096950786L),new I("kosame_H2O", 1367079770870046720L),new I("kuragen000", 1464202279897104391L),new I("kuro_gbp02", 1458399016023060481L),new I("kuro_gbp02", 1606632160756396032L),new I("kuro_gbp02", 1631265798554808322L),new I("kuro_gbp02", 1643578446768132097L),new I("kyo_k", 1357357057213538309L),new I("LiveTM_08", 1484269503563264000L),new I("lkpk_g", 1333706453182341120L),new I("lkpk_g", 1334414009840226305L),new I("lkpk_g", 1394177004967108608L),new I("lkpk_g", 1429742364428431367L),new I("Lyourika", 1644037256431423488L),new I("ma2_Matsu", 1334957384381288448L),new I("Mahdi_011", 1401149547724820482L),new I("maika_82", 1401554662130556928L),new I("MegaMouseArts", 1370182918258184192L),new I("mikazuki_akira", 1379289441450139654L),new I("mishima_kurone", 1451479334636572676L),new I("mishima_kurone", 1456979191736377344L),new I("mm_pentab", 1403371578327404544L),new I("mnk_nk1414", 1381215456741138434L),new I("mokuka_noe", 1363396372309901313L),new I("MP26player", 1335131654415818753L),new I("MP26player", 1639615200885366787L),new I("Mr_Tangsuyuk", 1576950800261095424L),new I("MyungYi_", 1346389393750204416L),new I("MyungYi_", 1367020232334663683L),new I("namahyou", 1441056569236463637L),new I("Namakura_noelle", 1408435172224114691L),new I("nasubisamurai15", 1524090497249075206L),new I("natu_7273", 1569990932568891392L),new I("nemuhosi", 1355473834326061062L),new I("NNPS_KM_SONYA", 1346410525853245445L),new I("NohikariAi", 1523465539678785536L),new I("noir2more", 1334513715660644355L),new I("nomoregrass417", 1526173684355870720L),new I("noneru_pix", 1349470312648290307L),new I("nut_megu", 1336584872471584768L),new I("nut_megu", 1338774453711355904L),new I("nut_megu", 1340946345096450049L),new I("nut_megu", 1345658481076887553L),new I("nut_megu", 1348193043229278208L),new I("nut_megu", 1363775871711014919L),new I("nut_megu", 1380808129676333064L),new I("nut_megu", 1401107716651638786L),new I("nut_megu", 1412336326108610563L),new I("nut_megu", 1417047602130993154L),new I("nut_megu", 1418859623973589000L),new I("nut_megu", 1436616211828129798L),new I("nut_megu", 1504421763479920640L),new I("nut_megu", 1507644448079253508L),new I("nut_megu", 1540621318374162433L),new I("oekakidaichi", 1657309710658461696L),new I("oekakidaichi", 1661658367314771968L),new I("ogipote", 712966825663795202L),new I("omurice4684", 1334513374726643715L),new I("OnsenSyndicate", 1401606597584068617L),new I("Osuzu_botan", 1458708798562918400L),new I("Pecino_", 1652145692498972673L),new I("pensukeo", 888014816266211329L),new I("pensukeo", 895656010974502912L),new I("pensukeo", 914382762978680832L),new I("pensukeo", 925014913285734402L),new I("pensukeo", 942065366179627008L),new I("pensukeo", 1038481866993500163L),new I("pensukeo", 1068471996369915906L),new I("pensukeo", 1099923406571528193L),new I("pensukeo", 1105799480396279809L),new I("pensukeo", 1113333584654266368L),new I("pensukeo", 1123238336359886849L),new I("pensukeo", 1123913549934473216L),new I("pensukeo", 1127271844384395264L),new I("pensukeo", 1129119914638233600L),new I("pensukeo", 1129334190695100416L),new I("pensukeo", 1130947782209024000L),new I("pensukeo", 1132349932705214465L),new I("pensukeo", 1172969592458600448L),new I("pensukeo", 1184557949714219009L),new I("pensukeo", 1271368885682319360L),new I("pensukeo", 1334513603865686018L),new I("pensukeo", 1345392705837309952L),new I("pensukeo", 1400214535080878081L),new I("pensukeo", 1641738701482110976L),new I("Pictolita", 1455005915992584196L),new I("Pinkyringring", 1659121877288366080L),new I("ptrtear", 1413824587935010820L),new I("ranfptn", 1355848490383855617L),new I("ronndomizukami", 1468855768367898625L),new I("rouka_2", 1331916310100336645L),new I("rouka0101", 1377561442694488064L),new I("Ruroi31", 1416330421525041152L),new I("Sabcoo", 1354433463043022849L),new I("sannshi_34", 1334743269582938112L),new I("sannshi_34", 1416979268928634880L),new I("senrihinZK", 1526404539121037312L),new I("shisui0178", 1380913346589822978L),new I("shONe_Banana", 1487825453427863553L),new I("shouu_kyun", 1452032402742669313L),new I("shoyu_maru", 1333748893239046144L),new I("shoyu_maru", 1334837150714527745L),new I("shoyu_maru", 1350413371363848196L),new I("shoyu_maru", 1358023126894825472L),new I("shoyu_maru", 1363817144018866178L),new I("shoyu_maru", 1365634168839790601L),new I("shoyu_maru", 1365995755194880003L),new I("shoyu_maru", 1369256816794771456L),new I("shoyu_maru", 1382302645726027782L),new I("shoyu_maru", 1383375535967903749L),new I("shoyu_maru", 1388463621164859395L),new I("shoyu_maru", 1426514140554698753L),new I("shoyu_maru", 1434109889056428034L),new I("shoyu_maru", 1449698306620227589L),new I("shoyu_maru", 1454041990329352192L),new I("shoyu_maru", 1459477324437458946L),new I("shoyu_maru", 1466361693324210183L),new I("shoyu_maru", 1475441506437988352L),new I("shoyu_maru", 1482306755447050241L),new I("shoyu_maru", 1487746415602577409L),new I("shoyu_maru", 1497902031813890058L),new I("shoyu_maru", 1523274342465175553L),new I("shoyu_maru", 1535957094251507712L),new I("shoyu_maru", 1546102725196550144L),new I("shoyu_maru", 1586696036126711809L),new I("shoyu_maru", 1593937896784166912L),new I("shoyu_maru", 1600838962406625280L),new I("shoyu_maru", 1606620722864214017L),new I("shoyu_maru", 1606981310349967366L),new I("shoyu_maru", 1622554337330499585L),new I("shoyu_maru", 1625462523523919872L),new I("shoyu_maru", 1631987566663843840L),new I("shoyu_maru", 1658807591819153408L),new I("skyrail_illust", 1660083826889207811L),new I("sozoshu_kyo", 1385928402499108866L),new I("suke_yuno", 1334837425667969024L),new I("suke_yuno", 1342076712587206658L),new I("suke_yuno", 1344635807965974528L),new I("suke_yuno", 1348240561505853442L),new I("suke_yuno", 1356574457360310273L),new I("suke_yuno", 1359838927914373130L),new I("suke_yuno", 1260533099361787905L),new I("suke_yuno", 1267778380994236416L),new I("suke_yuno", 1277202674916589570L),new I("suke_yuno", 1360925354773241857L),new I("suke_yuno", 1363822716260782080L),new I("suke_yuno", 1378677005764292611L),new I("suke_yuno", 1380491541450424320L),new I("suke_yuno", 1384431678454521860L),new I("suke_yuno", 1390275707347951621L),new I("suke_yuno", 1401870084285493252L),new I("suke_yuno", 1406218302515384322L),new I("suke_yuno", 1416757349046382592L),new I("suke_yuno", 1418673457491943428L),new I("suke_yuno", 1422169100533460995L),new I("suke_yuno", 1424335734085853193L),new I("suke_yuno", 1432310655512506386L),new I("suke_yuno", 1443910414455107588L),new I("suke_yuno", 1454782046337781770L),new I("suke_yuno", 1455867948883529732L),new I("suke_yuno", 1460578197477552133L),new I("suke_yuno", 1474349388307005448L),new I("suke_yuno", 1476897920071114759L),new I("suke_yuno", 1480872741784125441L),new I("suke_yuno", 1493194450688159746L),new I("suke_yuno", 1496087004211453960L),new I("suke_yuno", 1521823893857931264L),new I("suke_yuno", 1547189145818525696L),new I("suke_yuno", 1558054431153082368L),new I("suke_yuno", 1559510936616321024L),new I("suke_yuno", 1565673703215886337L),new I("suke_yuno", 1577266429568901124L),new I("suke_yuno", 1583789431949230081L),new I("suke_yuno", 1594666279511945216L),new I("suke_yuno", 1602273539247476738L),new I("suke_yuno", 1605171507097145344L),new I("suke_yuno", 1609197162726055936L),new I("suke_yuno", 1613869444387983362L),new I("suke_yuno", 1628346593803141120L),new I("suke_yuno", 1643586361029705728L),new I("suke_yuno", 1654095640509702144L),new I("suke_yuno", 1666097657549565952L),new I("suke_yuno", 1671852223087902720L),new I("sugarwhite1046", 1437002711405268993L),new I("syu_an_n", 1478490774585376768L),new I("Takumi_ha_DX", 1378299101071085569L),new I("Takumi_ha_DX", 1380122050883293189L),new I("Takumi_ha_DX", 1382673881903689730L),new I("Tam_U", 1644283492635930624L),new I("tkd14059560", 1380758914401099777L),new I("tmzr_ovo_", 1373556726469775363L),new I("toukan_drawing", 1373572772983574537L),new I("toukan_drawing", 1424305682589708293L),new I("toukan_drawing", 1507656088103297026L),new I("toketa15", 1360559485664677889L),new I("tQg_07", 1460934239822696456L),new I("TsutaKaede", 1528329828012785664L),new I("TsutaKaede", 1541753284070965249L),new I("TsutaKaede", 1618230537969364996L),new I("TsutaKaede", 1643912324561137664L),new I("tUWU284MlWaU7VA", 1367024008223354880L),new I("ud864", 1427601524688003072L),new I("Usa4gi", 1438652486806425603L),new I("Vi3q1ahbJM31goT", 1345234835741188105L),new I("wa_ki_ya_ku", 1360906994673016833L),new I("wa_ki_ya_ku", 1372881405705744384L),new I("wa_ki_ya_ku", 1387723681833447427L),new I("wa_ki_ya_ku", 1394971615864270850L),new I("wa_ki_ya_ku", 1406206671571984384L),new I("wa_ki_ya_ku", 1422886552280072195L),new I("wa_ki_ya_ku", 1424383682442268683L),new I("wa_ki_ya_ku", 1435205520265121793L),new I("wa_ki_ya_ku", 1448264639385509892L),new I("wa_ki_ya_ku", 1495134351272529926L),new I("wa_ki_ya_ku", 1599242577865478145L),new I("wa_ki_ya_ku", 1610423761794379776L),new I("wumalutsufuri", 1461324636524728323L),new I("wumalutsufuri", 1461695108055658496L),new I("wumalutsufuri", 1463503094977687554L),new I("wumalutsufuri", 1464586977114607616L),new I("wumalutsufuri", 1465279326392176641L),new I("wumalutsufuri", 1474425440760897536L),new I("wumalutsufuri", 1474737540821561345L),new I("wumalutsufuri", 1493204800414322689L),new I("wumalutsufuri", 1505947094192095235L),new I("wumalutsufuri", 1514219512388026373L),new I("wumalutsufuri", 1532330229880205312L),new I("wumalutsufuri", 1559492213809496064L),new I("wumalutsufuri", 1607322393818849281L),new I("wumalutsufuri", 1625463570090852352L),new I("XaJgt7S9FkzWCiy", 1553715061713039360L),new I("yachiyo_naga", 1422834901066207237L),new I("yano_t", 1378351221283594241L),new I("Yansae81", 1334864422934757376L),new I("Yansae81", 1345655525795512321L),new I("Yansae81", 1348915439527620608L),new I("Yansae81", 1467394004971323393L),new I("yuguya_1941", 1659114097823105025L),new I("yuki_artman", 1672808564459728899L),new I("yukimaru_sgk", 1334992143715201025L),new I("yuzufu_1", 1544305092333129728L),new I("z42893347", 1652654046753480704L),new I("zyu90gg", 1334575930908397568L),new I("zyu90gg", 1418913957067194368L),new I("zyu90gg", 1423605503146426376L)};

	/**
	 * Put every command and their execution into {@link #commands}.
	 */
	public CommandUsage()
	{
		ICommand alias;

		//初始化map 放入所有指令
		//invite
		commands.put(INVITE, event -> event.reply("https://discord.gg/UMYxwHyRNE").queue());

		//help
		commands.put(HELP, event -> event.reply(minecraftCommandRelated("help", event)).queue());

		//cmd
		alias = event -> event.reply(minecraftCommandRelated("cmd", event)).queue();
		commands.put(CMD, alias);
		commands.put(MCC, alias);
		commands.put(COMMAND, alias);

		//faq
		alias = event -> event.reply(minecraftCommandRelated("faq", event)).queue();
		commands.put(FAQ, alias);
		commands.put(QUESTION, alias);

		//dtp
		alias = event -> event.reply(minecraftCommandRelated("dtp", event)).queue();
		commands.put(DTP, alias);
		commands.put(DATAPACK, alias);

		//jira
		alias = new JiraCommand();
		commands.put(JIRA, alias);
		commands.put(BUG, alias);

		//tool
		commands.put(TOOL, new ToolCommand());

		//lang
		alias = event -> event.reply(minecraftCommandRelated("lang", event)).queue();
		commands.put(LANG, alias);
		commands.put(LANGUAGE, alias);

		//quote
		commands.put(QUOTE, new QuoteCommand());

		//youtuber
		commands.put(YOUTUBER, event -> event.reply("https://www.youtube.com/" + event.getOption("youtuber_name", CommonFunctions.getAsString)).queue());

		//introduce
		commands.put(INTRODUCE, new IntroduceCommand());

		//birthday
		commands.put(BIRTHDAY, new BirthdayCommand());

		//megumin
		commands.put(MEGUMIN, event ->
		{
			I meguminImage = Algorithm.randomElement(MEGUMIN_IMAGES);
			event.reply("https://twitter.com/" + meguminImage.a + "/status/" + meguminImage.s).queue();
		}); //隨機一張惠惠

		//shutdown
		commands.put(SHUTDOWN, event ->
		{
			if (event.getUser().getIdLong() != IDs.AC_ID) //不是我
			{
				event.reply("You can't do that.").queue();
				return;
			}

			event.reply("Shutting down...").complete(); //先送訊息 再下線

			JDA jda = Cartoland.getJDA();
			Guild cartoland = jda.getGuildById(IDs.CARTOLAND_SERVER_ID); //定位創聯
			if (cartoland == null) //如果找不到創聯
			{
				jda.shutdown(); //直接結束 不傳訊息了
				return;
			}

			TextChannel botChannel = cartoland.getTextChannelById(IDs.BOT_CHANNEL_ID); //創聯的機器人頻道
			if (botChannel != null) //找到頻道了
				botChannel.sendMessage("Cartoland Bot 已下線。\nCartoland Bot is now offline.").complete();
			jda.shutdown(); //關機下線
		});

		//reload
		commands.put(RELOAD, event ->
		{
			if (event.getUser().getIdLong() != IDs.AC_ID) //不是我
			{
				event.reply("You can't do that.").queue();
				return;
			}

			event.reply("Reloading...").queue();
			JsonHandle.reloadLanguageFiles();
		});

		//admin
		commands.put(ADMIN, new AdminCommand());

		//one_a_two_b
		commands.put(ONE_A_TWO_B, new OneATwoBCommand(this));

		//lottery
		commands.put(LOTTERY, new LotteryCommand());

		//transfer
		commands.put(TRANSFER, new TransferCommand());

		//tic_tac_toe
		commands.put(TIC_TAC_TOE, new TicTacToeCommand(this));

		//connect_four
		commands.put(CONNECT_FOUR, new ConnectFourCommand(this));
	}

	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when a user uses slash command.
	 *
	 * @param event The event that carries information of the user and the command.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
	{
		String commandName = event.getName();
		ICommand commandExecution = commands.get(commandName);
		if (commandExecution != null)
			commandExecution.commandProcess(event);
		else
			event.reply("You can't use this!").queue();
		User user = event.getUser();
		FileHandle.log(user.getEffectiveName() + "(" + user.getIdLong() + ") used /" + commandName); //IO放最後 避免超過3秒限制
	}

	/**
	 * When it comes to /help, /cmd, /faq, /dtp and /lang that needs to use lang/*.json files, those lambda
	 * expressions will call this method.
	 *
	 * @param commandName the command n, only "help", "cmd", "faq", "dtp" and "lang" are allowed.
	 * @param event The event that carries information of the user and the command.
	 * @return The content that the bot is going to reply the user.
	 * @since 1.0
	 * @author Alex Cai
	 */
	private String minecraftCommandRelated(String commandName, SlashCommandInteractionEvent event)
	{
		String argument = event.getOption(commandName + "_name", CommonFunctions.getAsString); //獲得參數
		if (argument == null) //沒有參數
			return JsonHandle.command(event.getUser().getIdLong(), commandName); //儘管/lang的參數是必須的 但為了方便還是讓他用這個方法處理
		return JsonHandle.command(event.getUser().getIdLong(), commandName, argument);
	}

	/**
	 * {@code I} stands for image, which is an image on Twitter. {@link #a} stands for "author", and {@link #s} stands for "serial number".
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static record I(String a, long s) {}
}