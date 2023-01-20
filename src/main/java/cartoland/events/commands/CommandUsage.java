package cartoland.events.commands;

import cartoland.Cartoland;
import cartoland.mini_games.MiniGameInterface;
import cartoland.mini_games.OneATwoB;
import cartoland.utility.FileHandle;
import cartoland.utility.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommandUsage extends ListenerAdapter
{
    private final Map<String, CommandInterface> commands = new HashMap<>();
    private final Map<String, MiniGameInterface> games = new HashMap<>();
    private String userID;
    private String argument;
    private final Random random = new Random();

    //350張惠惠圖片
    private final String[] megumin = { "_mexif/status/1375088851446755332","_Mizugasi/status/1444998923739824131","_Rivia_/status/1370370105696161794","_ukoyr/status/1544612178556121088","024NISHI024/status/1396492351346860038","1G9Xw69kePSLI6p/status/1420731295156477954","3CDtvmmpuqKhl7J/status/1471803818048634884","6gcYq8nXYHQwui5/status/1332594611156500487","7egTXS0GKWh3wGP/status/1334542548497387521","7egTXS0GKWh3wGP/status/1342110371662888960","7egTXS0GKWh3wGP/status/1346091304124497920","7egTXS0GKWh3wGP/status/1416720114066415617","7egTXS0GKWh3wGP/status/1455918876990996489","7egTXS0GKWh3wGP/status/1485297876947566594","7egTXS0GKWh3wGP/status/1527310236247560193","7egTXS0GKWh3wGP/status/1533389156755603456","7egTXS0GKWh3wGP/status/1557015010668609536","7egTXS0GKWh3wGP/status/1595465406420221952","7egTXS0GKWh3wGP/status/1606956350596317185","9aBGoLUanvcjd7O/status/1599205425127501824","ahmadjendro/status/1488530273373683713","Aloe_04maho/status/1342795265724436481","Aloe_04maho/status/1360906615008886784","Aloe_04maho/status/1403669632108564485","Aloe_04maho/status/1418163778487652354","Aloe_04maho/status/1423601843368730625","Aloe_04maho/status/1445719564185075722","Aloe_04maho/status/1573801227376214017","AONAGINEMURI/status/1390290873951285248","askr_otr/status/1468201641723965447","atasom/status/1343169561680519169","az210309/status/1383254021201297414","az210309/status/1391411800420151310","bhive003/status/1382662692729618434","bhive003/status/1421734667326132225","bhive003/status/1506027874825895938","bhive003/status/1576769547117293572","bhive003/status/1588833560546676737","BNC_0116/status/1120180446392807424","BNC_0116/status/1123935283995713543","BNC_0116/status/1128289624126738432","BNC_0116/status/1129402831587274752","BNC_0116/status/1211311585571917825","BNC_0116/status/1228335403221966849","BNC_0116/status/1353732864177623040","cachi_lo/status/1423468322893357056","catharpoon/status/1395598782675357698","catharpoon/status/1428436939015741440","chakichaki33/status/1529055947767050240","chakichaki33/status/1533765708513148928","CHILLY_karon/status/1378395239845482499","Coconatnuts09/status/1449003566610325504","creates_blue/status/1389692031606267904","creates_blue/status/1393257386287144963","creates_blue/status/1394877336516907010","creates_blue/status/1400192682480136194","D2gg1cPdO8bfjFS/status/1333391702132867073","D2gg1cPdO8bfjFS/status/1342283498783002626","D2gg1cPdO8bfjFS/status/1256453504299757569","dai_gazacy/status/1333072335717289986","DlrfMimikyu/status/1352830077696962560","DlrfMimikyu/status/1362948691376619524","DlrfMimikyu/status/1400129807925628933","E_GA_KU/status/1596505160175747072","Edacchi73/status/1487748613908615182","EJAMIARTSTUDIO/status/1370264344903045120","Fagi_Kakikaki/status/1416334656371269634","Fagi_Kakikaki/status/1558410423837523970","FevJF0EICCKnLlD/status/1347026153098211331","gakkari_orz/status/1388805801859452928","gakkari_orz/status/1538592731962179587","Gasolineillust/status/1565325467850518529","gazercide/status/1373256680763396106","GReeeeNmodoki2/status/1599057455702765568","GuRa_fgo/status/1348538564900917248","GuRa_fgo/status/1352926702398185473","HagGy0327_/status/1338114471143739392","hagi_neco/status/1393868573106327563","hagi_neco/status/1429373932159660032","hapycloud/status/1343423407442948101","harehareota/status/1598270723747098624","Harimax8/status/1383359465366323209","Harimax8/status/1422891821772906500","Harimax8/status/1538112903555215360","Harimax8/status/1596347482916950018","hatuganookome/status/1344206596310761472","he_shan123/status/1557726596202606595","HereticxxA/status/1398405163677671425","Hi_me_520So/status/1399677759698337797","hwa_n01/status/1353356379889700864","icetea_art/status/1528012740656345088","icetea_art/status/1545769743151353856","icetea_art/status/1553379890128306177","icetea_art/status/1555916614138150912","icetea_art/status/1573673618172624896","icetea_art/status/1593967339036450820","isonnoha/status/1504494884765396994","Ixy/status/1334516599760490498","Ixy/status/1353673446719250433","Ixy/status/1391319694859137024","Ixy/status/1417105811650727936","Ixy/status/1599059007498772480","J02GGOgpQ6dueGu/status/1336638090828341250","J02GGOgpQ6dueGu/status/1336868237141704705","J02GGOgpQ6dueGu/status/1345199258220126216","J02GGOgpQ6dueGu/status/1345279854040649728","J02GGOgpQ6dueGu/status/1352167601951002624","J02GGOgpQ6dueGu/status/1355069629840855042","J02GGOgpQ6dueGu/status/1358677301731368964","J02GGOgpQ6dueGu/status/1367304489468796932","J02GGOgpQ6dueGu/status/1378316365128331264","J02GGOgpQ6dueGu/status/1380295158676090880","J02GGOgpQ6dueGu/status/1467024737448886274","JackRockhardt/status/1421495091001135106","JAM_pentail/status/1374333181684375556","JAM_pentail/status/1379418416046477312","JAM_pentail/status/1496121346824232966","jirafuru1/status/1385345798372741120","kadokawasneaker/status/1463416144765751300","kadokawasneaker/status/1463709023455399936","kaiiruuu/status/1435533706161102852","kamindani/status/1364578421406199822","karst_bunny/status/1487062233343426561","karst_bunny/status/1502885083614973952","karst_bunny/status/1577615125720485888","kasumiM224/status/1408089488358723585","kasumiM224/status/1530582872184483840","kgs_jf/status/1459384335799562241","kisasageDrawing/status/1571040020219428865","KMTM_kmk0819/status/1342130873785745411","KMTM_kmk0819/status/1356814844792504321","KMTM_kmk0819/status/1388147012881707009","KMTM_kmk0819/status/1400465949963145226","KMTM_kmk0819/status/1446105335723298817","KMTM_kmk0819/status/1573212323644575744","KMTM_kmk0819/status/1606986710520565761","KMTM_kmk0819/status/1614200785335623682","kosame_H2O/status/1367079770870046720","kuragen000/status/1464202279897104391","kuro_gbp02/status/1458399016023060481","kuro_gbp02/status/1606632160756396032","kyo_k/status/1357357057213538309","LiveTM_08/status/1484269503563264000","lkpk_g/status/1333706453182341120","lkpk_g/status/1334414009840226305","lkpk_g/status/1394177004967108608","lkpk_g/status/1429742364428431367","ma2_Matsu/status/1334957384381288448","Mahdi_011/status/1401149547724820482","maika_82/status/1401554662130556928","MegaMouseArts/status/1370182918258184192","mikazuki_akira/status/1379289441450139654","mishima_kurone/status/1451479334636572676","mishima_kurone/status/1456979191736377344","mm_pentab/status/1403371578327404544","mnk_nk1414/status/1381215456741138434","mokuka_noe/status/1363396372309901313","MP26player/status/1335131654415818753","Mr_Tangsuyuk/status/1576950800261095424","MyungYi_/status/1346389393750204416","MyungYi_/status/1367020232334663683","namahyou/status/1441056569236463637","Namakura_noelle/status/1408435172224114691","nasubisamurai15/status/1524090497249075206","natu_7273/status/1569990932568891392","nemuhosi/status/1355473834326061062","NNPS_KM_SONYA/status/1346410525853245445","NohikariAi/status/1523465539678785536","noir2more/status/1334513715660644355","nomoregrass417/status/1526173684355870720","noneru_pix/status/1349470312648290307","nut_megu/status/1336584872471584768","nut_megu/status/1338774453711355904","nut_megu/status/1340946345096450049","nut_megu/status/1345658481076887553","nut_megu/status/1348193043229278208","nut_megu/status/1363775871711014919","nut_megu/status/1380808129676333064","nut_megu/status/1401107716651638786","nut_megu/status/1412336326108610563","nut_megu/status/1417047602130993154","nut_megu/status/1418859623973589000","nut_megu/status/1436616211828129798","nut_megu/status/1504421763479920640","nut_megu/status/1507644448079253508","nut_megu/status/1540621318374162433","ogipote/status/712966825663795202","omurice4684/status/1334513374726643715","OnsenSyndicate/status/1401606597584068617","Osuzu_botan/status/1458708798562918400","pensukeo/status/1038481866993500163","pensukeo/status/1068471996369915906","pensukeo/status/1099923406571528193","pensukeo/status/1105799480396279809","pensukeo/status/1113333584654266368","pensukeo/status/1123238336359886849","pensukeo/status/1123913549934473216","pensukeo/status/1127271844384395264","pensukeo/status/1129119914638233600","pensukeo/status/1129334190695100416","pensukeo/status/1130947782209024000","pensukeo/status/1132349932705214465","pensukeo/status/1172969592458600448","pensukeo/status/1184557949714219009","pensukeo/status/1271368885682319360","pensukeo/status/1334513603865686018","pensukeo/status/1345392705837309952","pensukeo/status/1400214535080878081","Pictolita/status/1455005915992584196","ptrtear/status/1413824587935010820","ranfptn/status/1355848490383855617","ronndomizukami/status/1468855768367898625","rouka_2/status/1331916310100336645","rouka0101/status/1377561442694488064","Ruroi31/status/1416330421525041152","Sabcoo/status/1354433463043022849","sannshi_34/status/1334743269582938112","sannshi_34/status/1416979268928634880","senrihinZK/status/1526404539121037312","shisui0178/status/1380913346589822978","shONe_Banana/status/1487825453427863553","shouu_kyun/status/1452032402742669313","shoyu_Sara/status/1333748893239046144","shoyu_Sara/status/1334837150714527745","shoyu_Sara/status/1350413371363848196","shoyu_Sara/status/1358023126894825472","shoyu_Sara/status/1363817144018866178","shoyu_Sara/status/1365634168839790601","shoyu_Sara/status/1365995755194880003","shoyu_Sara/status/1369256816794771456","shoyu_Sara/status/1382302645726027782","shoyu_Sara/status/1383375535967903749","shoyu_Sara/status/1388463621164859395","shoyu_Sara/status/1426514140554698753","shoyu_Sara/status/1434109889056428034","shoyu_Sara/status/1449698306620227589","shoyu_Sara/status/1454041990329352192","shoyu_Sara/status/1459477324437458946","shoyu_Sara/status/1466361693324210183","shoyu_Sara/status/1475441506437988352","shoyu_Sara/status/1482306755447050241","shoyu_Sara/status/1487746415602577409","shoyu_Sara/status/1497902031813890058","shoyu_Sara/status/1523274342465175553","shoyu_Sara/status/1535957094251507712","shoyu_Sara/status/1546102725196550144","shoyu_Sara/status/1586696036126711809","shoyu_Sara/status/1593937896784166912","shoyu_Sara/status/1600838962406625280","shoyu_Sara/status/1606620722864214017","shoyu_Sara/status/1606981310349967366","sozoshu_kyo/status/1385928402499108866","suke_yuno/status/1334837425667969024","suke_yuno/status/1342076712587206658","suke_yuno/status/1344635807965974528","suke_yuno/status/1348240561505853442","suke_yuno/status/1356574457360310273","suke_yuno/status/1359838927914373130","suke_yuno/status/1260533099361787905","suke_yuno/status/1267778380994236416","suke_yuno/status/1277202674916589570","suke_yuno/status/1360925354773241857","suke_yuno/status/1363822716260782080","suke_yuno/status/1378677005764292611","suke_yuno/status/1380491541450424320","suke_yuno/status/1384431678454521860","suke_yuno/status/1390275707347951621","suke_yuno/status/1401870084285493252","suke_yuno/status/1406218302515384322","suke_yuno/status/1416757349046382592","suke_yuno/status/1418673457491943428","suke_yuno/status/1422169100533460995","suke_yuno/status/1424335734085853193","suke_yuno/status/1432310655512506386","suke_yuno/status/1443910414455107588","suke_yuno/status/1454782046337781770","suke_yuno/status/1455867948883529732","suke_yuno/status/1460578197477552133","suke_yuno/status/1474349388307005448","suke_yuno/status/1476897920071114759","suke_yuno/status/1480872741784125441","suke_yuno/status/1493194450688159746","suke_yuno/status/1496087004211453960","suke_yuno/status/1521823893857931264","suke_yuno/status/1547189145818525696","suke_yuno/status/1558054431153082368","suke_yuno/status/1559510936616321024","suke_yuno/status/1565673703215886337","suke_yuno/status/1577266429568901124","suke_yuno/status/1583789431949230081","suke_yuno/status/1594666279511945216","suke_yuno/status/1602273539247476738","suke_yuno/status/1605171507097145344","suke_yuno/status/1609197162726055936","suke_yuno/status/1613869444387983362","sugarwhite1046/status/1437002711405268993","syu_an_n/status/1478490774585376768","Takumi_ha_DX/status/1378299101071085569","Takumi_ha_DX/status/1380122050883293189","Takumi_ha_DX/status/1382673881903689730","tkd14059560/status/1380758914401099777","tmzr_ovo_/status/1373556726469775363","toukan_drawing/status/1373572772983574537","toukan_drawing/status/1424305682589708293","toukan_drawing/status/1507656088103297026","toketa15/status/1360559485664677889","tQg_07/status/1460934239822696456","TsutaKaede/status/1528329828012785664","TsutaKaede/status/1541753284070965249","tUWU284MlWaU7VA/status/1367024008223354880","ud864/status/1427601524688003072","Usa4gi/status/1438652486806425603","Vi3q1ahbJM31goT/status/1345234835741188105","wa_ki_ya_ku/status/1360906994673016833","wa_ki_ya_ku/status/1372881405705744384","wa_ki_ya_ku/status/1387723681833447427","wa_ki_ya_ku/status/1394971615864270850","wa_ki_ya_ku/status/1406206671571984384","wa_ki_ya_ku/status/1422886552280072195","wa_ki_ya_ku/status/1424383682442268683","wa_ki_ya_ku/status/1435205520265121793","wa_ki_ya_ku/status/1448264639385509892","wa_ki_ya_ku/status/1495134351272529926","wa_ki_ya_ku/status/1599242577865478145","wa_ki_ya_ku/status/1610423761794379776","wumalutsufuri/status/1461324636524728323","wumalutsufuri/status/1461695108055658496","wumalutsufuri/status/1463503094977687554","wumalutsufuri/status/1464586977114607616","wumalutsufuri/status/1465279326392176641","wumalutsufuri/status/1474425440760897536","wumalutsufuri/status/1474737540821561345","wumalutsufuri/status/1493204800414322689","wumalutsufuri/status/1505947094192095235","wumalutsufuri/status/1514219512388026373","wumalutsufuri/status/1532330229880205312","wumalutsufuri/status/1559492213809496064","wumalutsufuri/status/1607322393818849281","XaJgt7S9FkzWCiy/status/1553715061713039360","yachiyo_naga/status/1422834901066207237","yano_t/status/1378351221283594241","Yansae81/status/1334864422934757376","Yansae81/status/1345655525795512321","Yansae81/status/1348915439527620608","Yansae81/status/1467394004971323393","yukimaru_sgk/status/1334992143715201025","yuzufu_1/status/1544305092333129728","zyu90gg/status/1334575930908397568","zyu90gg/status/1418913957067194368","zyu90gg/status/1423605503146426376" };

    public CommandUsage()
    {
        //初始化map 放入所有指令
        commands.put("invite", event->
                event.reply("https://discord.gg/UMYxwHyRNE").queue());

        commands.put("datapack", event ->
                event.reply(minecraftCommandRelated("dtp", event)).queue());
        commands.put("language", event ->
                event.reply(minecraftCommandRelated("lang", event)).queue());
        String[] askCommands = { "help", "cmd", "faq", "dtp", "lang" };
        for (String s : askCommands)
            commands.put(s, event ->
                    event.reply(minecraftCommandRelated(s, event)).queue());

        commands.put("megumin", event ->
                event.reply("https://twitter.com/" + megumin[random.nextInt(megumin.length)]).queue()); //隨機一張惠惠

        commands.put("shutdown", event ->
        {
            if (!userID.equals(Cartoland.AC_ID_STRING)) //不是我
                return;
            event.reply("Shutting down...").queue();
            TextChannel channel = event.getJDA().getChannelById(TextChannel.class, Cartoland.BOT_CHANNEL_ID);
            if (channel != null)
                channel.sendMessage("Cartoland bot is now offline.").queue();
            event.getJDA().shutdown();
        });

        commands.put("whosyourdaddy", event -> event.reply(userID.equals(Cartoland.AC_ID_STRING) ? "You." : "Not you.").queue());

        commands.put("oneatwob", event ->
        {
            argument = event.getOption("answer", OptionMapping::getAsString);
            if (argument == null) //不帶參數
            {
                if (games.containsKey(userID)) //已經有在玩遊戲
                    event.reply("You are already in " + games.get(userID).gameName() + " game.").queue();
                else
                {
                    event.reply("Start 1A2B game! type `/oneatwob <answer>` to make a guess.").queue();
                    games.put(userID, new OneATwoB());
                }
            }
            else //帶參數
            {
                if (games.containsKey(userID)) //已經有在玩遊戲
                {
                    MiniGameInterface playing = games.get(userID);
                    if (playing.gameName().equals("1A2B")) //是1A2B沒錯
                    {
                        OneATwoB oneATwoB = (OneATwoB) playing;
                        int ab = oneATwoB.calculateAAndB(argument);
                        String shouldReply = switch (ab)
                        {
                            case OneATwoB.ErrorCode.INVALID ->
                                    "Not a valid answer, please enter " + OneATwoB.ANSWER_LENGTH + " integers.";
                            case OneATwoB.ErrorCode.NOT_UNIQUE ->
                                    "Please enter " + OneATwoB.ANSWER_LENGTH + " unique integers.";
                            default -> argument + " = " + ab / 10 + " A " + ab % 10 + " B";
                        };

                        //猜出ANSWER_LENGTH個A 遊戲結束
                        if (ab / 10 == OneATwoB.ANSWER_LENGTH)
                        {
                            long second = oneATwoB.getTimePassed();
                            event.reply(shouldReply + "\nGame Over, the answer is **" + argument + "**.\n" +
                                                "Used Time: " + second / 60 + " minutes " + second % 60 + " seconds.\n" +
                                                "Guesses: " + oneATwoB.getGuesses() + " times.").queue();
                            games.remove(userID);
                        }
                        else
                            event.reply(shouldReply).queue();
                    }
                }
                else
                    event.reply("Please run `/oneatwob` without arguments to start the game.").queue();
            }
        });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        super.onSlashCommandInteraction(event);
        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) //不是機器人
            return;

        userID = member.getId();
        commands.get(event.getName()).commandProcess(event);

        String logString = member.getUser().getName() + "(" + userID + ") used /" + event.getName() + (argument != null ? " " + argument + "." : ".");
        System.out.println(logString);
        FileHandle.logIntoFile(logString);
    }

    private String minecraftCommandRelated(String jsonKey, @NotNull SlashCommandInteractionEvent event)
    {
        argument = event.getOption(jsonKey + "_name", OptionMapping::getAsString); //獲得參數
        if (argument == null) //沒有參數
            return JsonHandle.command(userID, jsonKey);
        return JsonHandle.command(userID, jsonKey, argument);
    }
}
