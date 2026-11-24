package com.tuxunxiangqi.android.data

import android.content.Context
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.OpeningBook
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** A built-in endgame puzzle. All puzzles are "Red to move and
 *  win" positions with a decisive theoretical verdict (material or
 *  textbook wins). There is no single forced line baked in: during
 *  play the engine judges every move, so any genuinely winning
 *  continuation counts — the engine is the referee. */
data class Puzzle(
    val id: Int,
    val title: String,
    val fen: String,
    val hint: String
)

object Puzzles {
    val all: List<Puzzle> = listOf(
        Puzzle(
            1, "双车炮对士象全",
            "2bakab2/9/3R1R3/9/4C4/9/9/9/9/4K4 w - - 0 1",
            "双车加一炮打士象全，子力压倒。先占中路，再逐个敲掉守子。"
        ),
        Puzzle(
            2, "马炮胜士象全",
            "2bakab2/9/4N4/9/9/4C4/9/9/9/4K4 w - - 0 1",
            "马炮对士象全是经典例胜：马踩中士、炮借马腿，慢慢收网，急不得。"
        ),
        Puzzle(
            3, "车马胜士象全",
            "2bakab2/9/3R1N3/9/9/9/9/9/9/3K5 w - - 0 1",
            "车马配合：车守肋道，马走卧槽，逼黑将无处可躲。"
        ),
        Puzzle(
            4, "车兵对单马",
            "4k4/9/2n6/4P4/9/9/R8/9/9/4K4 w - - 0 1",
            "高兵加车打单马：兵借车力过宫，马再能跳也拦不住两条线。"
        ),
        Puzzle(
            5, "车兵对双士",
            "3aka3/9/9/4P4/9/9/8R/9/9/4K4 w - - 0 1",
            "车兵对双士：先用兵换掉一士，车再收拾残局。"
        ),
        Puzzle(
            6, "双炮兵对士象全",
            "2bakab2/9/9/4P4/2C3C2/9/9/9/9/4K4 w - - 0 1",
            "双炮加过河兵：兵是炮的炮架，也是压宫的钉子，一步步往里挤。"
        ),
        Puzzle(
            7, "马兵胜士象全",
            "2bakab2/9/9/3NP4/9/9/9/9/9/3K5 w - - 0 1",
            "马兵对士象全是例胜，但步数不短：马走八个点，兵随将转，耐心局。"
        ),
        Puzzle(
            8, "单车对马炮",
            "4k4/9/2c6/6n2/4R4/9/9/9/9/4K4 w - - 0 1",
            "单车对马炮：车先捉双、再兑炮，剩下车对马就稳了。"
        ),
        Puzzle(
            9, "重炮加车",
            "3ak4/9/4C4/4C4/9/9/9/9/R8/4K4 w - - 0 1",
            "中路重炮已经成形，车再助一臂之力，黑将的活动空间只剩一条缝。"
        ),
        Puzzle(
            11, "车马对马士象",
            "2bakab2/9/7R1/3n5/4N4/9/9/9/9/4K4 w - - 0 1",
            "车马打带马的防线：先兑掉那匹马，剩下的就是熟悉的车马收官。"
        ),
        Puzzle(
            12, "马炮兵对士象全",
            "2bakab2/9/9/3NP4/9/4C4/9/9/9/3K5 w - - 0 1",
            "马炮兵三子归边：兵占花心、马走卧槽、炮借兵架，三条线轮流施压。"
        ),
        Puzzle(
            13, "单车对炮双士",
            "3aka3/9/9/9/4c4/9/R8/9/9/4K4 w - - 0 1",
            "车打炮双士：先捉炮再破士。对方炮能远程捣乱，车要先站住中路。"
        ),
        Puzzle(
            14, "双马炮对士象全",
            "2bakab2/9/2N3N2/9/9/9/4C4/9/9/4K4 w - - 0 1",
            "双马加一炮，子力富余：双马轮番卧槽，炮在中路封口，慢慢挤死。"
        ),
        Puzzle(
            15, "车兵对单炮",
            "4k4/9/7c1/4P4/9/2R6/9/9/9/4K4 w - - 0 1",
            "车兵对单炮：兵过宫、车护兵，炮只能远远看着——别让炮换掉兵。"
        ),
        Puzzle(
            16, "双车带兵",
            "2bakab2/9/R7R/4P4/9/9/9/9/9/4K4 w - - 0 1",
            "双车加过河兵打士象全，纯粹的子力碾压：双车占肋，兵往花心一坐就收官。"
        ),
        Puzzle(
            10, "卧槽马加炮",
            "2bakab2/9/2N6/9/9/9/4C4/9/9/4K4 w - - 0 1",
            "马已卧槽，炮在中路：马后炮的架子搭好了，就看你怎么收口。"
        ),
        Puzzle(
            17, "车炮胜士象全",
            "2bakab2/9/9/9/4C4/9/9/3R5/9/4K4 w - - 0 1",
            "车炮配合打士象全：炮占中路，车扫边路，先破一士再收官。"
        ),
        Puzzle(
            18, "双马胜士象全",
            "2bakab2/9/2N3N2/9/9/9/9/9/9/3K5 w - - 0 1",
            "双马打士象全是出了名的耐心局：双马轮流占位，别给黑将透气的口。"
        ),
        Puzzle(
            19, "车马炮联攻",
            "2bakab2/9/3R5/4N4/4C4/9/9/9/9/4K4 w - - 0 1",
            "车马炮三件套都在：车占肋、马卧槽、炮镇中，轮着施压别贪吃。"
        ),
        Puzzle(
            20, "单车胜士象全",
            "2bakab2/9/9/9/9/9/9/3R5/9/3K5 w - - 0 1",
            "单车打士象全靠的是耐心和位置：车先占中，再逐个敲掉士象。"
        ),
        Puzzle(
            21, "单车对马双士",
            "3aka3/9/9/2n6/9/9/9/3R5/9/3K5 w - - 0 1",
            "车打马双士：先捉马，再破士。马跳得欢，车要卡住它的落点。"
        ),
        Puzzle(
            22, "双炮胜士象全",
            "2bakab2/9/9/9/2C3C2/9/9/9/9/3K5 w - - 0 1",
            "双炮打士象全：两门炮轮流借将作架，一门封口，一门打子。"
        ),
        Puzzle(
            23, "马炮兵对单马",
            "4k4/9/9/2n1N4/4P4/9/4C4/9/9/3K5 w - - 0 1",
            "马炮兵打单马：兵过河压宫，马炮在后面跟上，把黑马兑掉就稳了。"
        ),
        Puzzle(
            24, "车炮兵攻士象全",
            "2bakab2/9/9/4P4/9/4C4/9/3R5/9/4K4 w - - 0 1",
            "车炮兵三线合围：兵坐花心当炮架，车炮轮番轰，先破防线再收官。"
        ),
        Puzzle(
            25, "双车马对士象全",
            "2bakab2/9/9/3N5/R8/8R/9/9/9/3K5 w - - 0 1",
            "双车加马打士象全，子力碾压：车占要道、马踏中士，破防只是时间问题。"
        ),
        Puzzle(
            26, "车炮双兵压宫",
            "2bakab2/9/9/3P1P3/9/3R5/7C1/9/9/3K5 w - - 0 1",
            "车炮带着双兵压境：兵往宫里拱，炮借兵架，车随时准备收官。"
        ),
        Puzzle(
            27, "马双炮带兵",
            "2bakab2/9/9/4P4/4N4/7C1/1C7/9/9/4K4 w - - 0 1",
            "马、双炮加过河兵：炮靠兵和马做架子，轮番轰宫心，守方顾此失彼。"
        ),
        Puzzle(
            28, "双车对马炮",
            "4k4/9/2n3c2/9/8R/9/R8/9/9/3K5 w - - 0 1",
            "双车对马炮：先捉炮、再困马，双车成形后一路平推。"
        ),
        Puzzle(
            29, "车马对双炮双士",
            "3aka3/9/1c5c1/9/5N3/2R6/9/9/9/3K5 w - - 0 1",
            "车马打双炮：车捉炮、马踏士，兑掉一炮后局面就透了。"
        ),
        Puzzle(
            30, "车带三兵胜士象全",
            "2bakab2/9/9/4P4/1RP3P2/9/9/9/9/4K4 w - - 0 1",
            "一车带三个过河兵：兵兵相护往前拱，车在后头撑腰，士象全也挡不住。"
        ),
        Puzzle(
            31, "双车炮兵齐攻",
            "2bakab2/9/9/3P5/9/R7R/4C4/9/9/4K4 w - - 0 1",
            "双车一炮一兵全压上：先手在握，逐个敲掉守子再收官。"
        ),
        Puzzle(
            32, "车马双兵对士象全",
            "2bakab2/9/9/6N2/3P1P3/2R6/9/9/9/3K5 w - - 0 1",
            "车马双兵：兵拱宫、马跳卧槽、车占肋道，三路一起施压。"
        ),
        Puzzle(
            33, "双车胜士象全",
            "2bakab2/9/9/9/9/9/9/9/3R3R1/3K5 w - - 0 1",
            "双车打士象全是最干脆的子力差：两车轮番压上抽子，别贪快走成和棋。"
        ),
        Puzzle(
            34, "车马炮胜士象全",
            "2bakab2/9/9/4N4/4C4/9/9/9/5R3/4K4 w - - 0 1",
            "车马炮三件齐全对士象全：马踩中士、炮打底象、车收残局，分工明确。"
        ),
        Puzzle(
            35, "双马炮胜士象全",
            "2bakab2/9/2N3N2/9/4C4/9/9/9/9/4K4 w - - 0 1",
            "双马加一炮是经典例胜：双马轮转踩士象，炮在中路借马腿发炮。"
        ),
        Puzzle(
            36, "车双兵胜士象全",
            "2bakab2/9/9/3P1P3/9/9/9/9/4R4/4K4 w - - 0 1",
            "车带双兵打士象全：双兵过河互相撑腰，车护着兵一步步拱进九宫。"
        ),
        Puzzle(
            37, "马炮双兵胜士象全",
            "2bakab2/9/9/3NP4/4C4/3P5/9/9/9/4K4 w - - 0 1",
            "马炮带双兵：兵是炮架也是钉子，马炮兵三路轮着施压，守方顾此失彼。"
        ),
        Puzzle(
            38, "双车胜马炮双士",
            "3ak4/4a4/2n3c2/9/9/9/9/9/9/R3K3R w - - 0 1",
            "双车对马炮双士，子力差太悬殊：先捉马炮、再破双士，稳扎稳打即可。"
        ),
        Puzzle(
            39, "车炮胜双马",
            "4k4/9/2n3n2/9/9/4C4/9/9/9/R3K4 w - - 0 1",
            "车炮对双马是例胜：车炮配合先兑掉一马，剩单马挡不住车炮合攻。"
        ),
        Puzzle(
            40, "马兵胜单炮",
            "4k4/9/9/4c4/3NP4/9/9/9/9/4K4 w - - 0 1",
            "马兵对单炮是耐心局：高兵借马力往底线拱，炮隔子打不着，慢慢磨。"
        ),
        Puzzle(
            41, "车马双炮胜士象全",
            "2bakab2/9/3R5/3N5/4C4/9/5C3/9/9/4K4 w - - 0 1",
            "车马双炮一齐压上，子力豪华：车占肋、马踏士、双炮轮番轰，分工收网别贪快。"
        ),
        Puzzle(
            42, "双车双炮胜士象全",
            "2bakab2/9/R7R/4C4/9/3C5/9/9/9/4K4 w - - 0 1",
            "双车双炮打士象全是纯粹的火力差：炮先轰掉守子，双车再平推收官。"
        ),
        Puzzle(
            43, "车炮双马胜双士",
            "3aka3/9/9/2N3N2/R8/4C4/9/9/9/4K4 w - - 0 1",
            "对方只剩双士：双马轮流卧槽，车炮在后面封口，破士只是先后问题。"
        ),
        Puzzle(
            44, "双马双炮胜士象全",
            "2bakab2/9/2N3N2/4C4/9/2C6/9/9/9/3K5 w - - 0 1",
            "双马双炮的子力富余局：马踩士象、炮借马腿，双管齐下慢慢收网。"
        ),
        Puzzle(
            45, "车双马胜士象全",
            "2bakab2/9/2N3N2/9/9/3R5/9/9/9/3K5 w - - 0 1",
            "一车带双马：车封肋道，双马轮转踩子，士象全守得了一时守不了全场。"
        ),
        Puzzle(
            46, "马炮带兵胜单马",
            "4k4/9/1n7/3P5/5N3/9/6C2/9/9/3K5 w - - 0 1",
            "马炮带一兵打单马：兵已过河，马炮跟上。这局有杀棋藏在里面，先把杀路算准，再动手收官。"
        ),
        Puzzle(
            47, "双车兵胜双士",
            "3aka3/9/9/4P4/9/9/R8/8R/9/4K4 w - - 0 1",
            "双车带高兵打双士：兵先换掉一士，双车再收拾残局，稳扎稳打。"
        ),
        Puzzle(
            48, "车马兵胜单炮",
            "4k4/9/9/7c1/3NP4/9/9/2R6/9/3K5 w - - 0 1",
            "车马兵打单炮：先把对方的炮兑掉或赶远，剩下车马兵对空宫就透了。"
        ),
        Puzzle(
            49, "马炮兵胜士象",
            "2baka3/3P5/9/6N2/9/9/4C4/9/9/3K5 w - - 0 1",
            "兵已贴宫、马炮都在，这局不靠磨：两步里藏着杀，先把杀路算出来再动手。"
        ),
        Puzzle(
            50, "车炮双兵胜双象",
            "2b1k1b2/9/9/3P1P3/4C4/9/R8/9/9/4K4 w - - 0 1",
            "双象守不了中路：双兵压宫、车炮跟进，几步之内就有杀，关键是先手别断。"
        ),
        Puzzle(
            51, "车马双兵胜双象",
            "2b1k1b2/9/9/3P5/2N3P2/9/9/8R/9/3K5 w - - 0 1",
            "车马带双兵打双象：马跳、兵拱、车封路，三件套轮着上，杀路不长，算准再走。"
        ),
        Puzzle(
            52, "双马车兵胜双象",
            "2b1k1b2/9/2N1P1N2/9/9/9/9/4R4/9/3K5 w - - 0 1",
            "车占中、双马锁宫、兵临城下——铁门栓的架子已经摆好，只差最后一步，自己找出来。"
        ),
        Puzzle(
            53, "车马兵胜双士单象",
            "3akab2/1N7/4P4/9/9/9/9/3R5/9/3K5 w - - 0 1",
            "车马兵对双士单象，子力不多但杀已经成形：一步定局，先把那一步找出来。"
        ),
        Puzzle(
            54, "卧槽马胜双士",
            "3aka3/9/4P4/9/4N4/9/9/9/9/4K4 w - - 0 1",
            "兵压中路、马已临宫：卧槽一跳，将军无处可逃。一步定局，先把那一跳找出来。"
        ),
        Puzzle(
            55, "双炮马胜士象全",
            "2bakab2/9/4N4/9/2C3C2/9/9/9/9/3K5 w - - 0 1",
            "双炮带一马打士象全：马当炮架、炮借马威，几步之内有杀，先把杀路算清再动手。"
        ),
        Puzzle(
            56, "双马炮胜士象全（二）",
            "2bakab2/9/2N3N2/9/4C4/9/9/9/9/3K5 w - - 0 1",
            "双马加一炮：双马轮流踩点、炮在后面封口，杀路不长，别让先手断了。"
        ),
        Puzzle(
            57, "车双炮胜双士",
            "3aka3/9/9/9/4C4/9/3C5/8R/9/4K4 w - - 0 1",
            "车炮齐备、对方只剩双士：杀已经成形，一步定局，先把那一步找出来。"
        ),
        Puzzle(
            58, "双马兵胜士象全",
            "2bakab2/9/9/3NP4/9/5N3/9/9/9/3K5 w - - 0 1",
            "双马带一兵打士象全：子力不占优，赢在配合——几步之内藏着杀，马腿别被别住，先把杀路算出来。"
        ),
        Puzzle(
            59, "双车兵胜士象全",
            "2bakab2/9/9/4P4/9/9/R8/8R/9/4K4 w - - 0 1",
            "双车带高兵打士象全，子力压倒：先敲掉守子，再平推收官，稳扎稳打别贪快。"
        ),
        Puzzle(
            60, "车马炮胜士象全（二）",
            "2bakab2/9/2N6/9/4C4/9/9/3R5/9/4K4 w - - 0 1",
            "车马炮三件套齐攻，杀已经成形：一步定局，先把那一步找出来。"
        ),
        Puzzle(
            61, "双车马胜双士",
            "3aka3/9/9/5N3/9/9/R8/8R/9/3K5 w - - 0 1",
            "双车带一马打双士：车封路、马锁宫，两步之内见杀，先把杀路算清再走。"
        ),
        Puzzle(
            62, "马双兵胜士象全",
            "2bakab2/9/9/3P1P3/4N4/9/9/9/9/3K5 w - - 0 1",
            "一匹马带双兵打士象全：子力单薄，全靠兵马配合——这局藏着一串连环杀，算不全别动手。"
        ),
        Puzzle(
            63, "车炮双兵胜双士",
            "3aka3/9/9/3P1P3/4C4/9/9/8R/9/4K4 w - - 0 1",
            "车炮带双兵打双士，杀已经成形：一步定局，先把那一步找出来。"
        ),
        Puzzle(
            64, "车双兵胜双士单象",
            "3akab2/9/9/3P1P3/9/9/9/8R/9/3K5 w - - 0 1",
            "车带双兵打双士单象：兵借车力压宫，先敲掉守子再收官，子力差摆在那里，稳着走。"
        ),
        Puzzle(
            65, "车马双炮胜双士单象",
            "3akab2/9/2N6/4C4/9/5C3/9/3R5/9/4K4 w - - 0 1",
            "车马双炮齐攻双士单象，火力全开：一步定局，先把那一步找出来。"
        ),
        Puzzle(
            66, "马炮双兵胜士象全（二）",
            "2bakab2/9/9/3P1P3/4C4/5N3/9/9/9/3K5 w - - 0 1",
            "马炮带双兵打士象全：兵马炮三样配合，藏着一串连环杀，算不全别动手。"
        ),
        Puzzle(
            67, "双马双兵胜双士单象",
            "3akab2/9/2N3N2/3P1P3/9/9/9/9/9/3K5 w - - 0 1",
            "双马带双兵打双士单象：马踩点、兵压宫，几步之内见杀，先把杀路算清。"
        ),
        Puzzle(
            68, "双车炮胜双士单象",
            "3akab2/9/9/9/4C4/9/9/R8/8R/3K5 w - - 0 1",
            "双车带一炮打双士单象：车封路、炮轰士，两步之内见杀。"
        ),
        Puzzle(
            69, "车炮马兵胜双士单象",
            "3akab2/9/9/4P4/5N3/6C2/9/3R5/9/3K5 w - - 0 1",
            "车炮马兵四件套打双士单象：轮着上、别乱序，三步之内见杀。"
        ),
        Puzzle(
            70, "车双马双兵胜双士",
            "3aka3/9/2N3N2/3P1P3/9/9/9/9/8R/3K5 w - - 0 1",
            "一车带双马双兵打双士：子力豪华，马踩点、车封路，两步之内见杀。"
        ),
        Puzzle(
            71, "双炮马兵胜双士",
            "3aka3/9/4P4/9/3N5/2C3C2/9/9/9/4K4 w - - 0 1",
            "双炮带马兵打双士：炮借马腿、兵压中路，两步之内见杀，先算清再走。"
        ),
        Puzzle(
            72, "炮马双兵胜双士",
            "3aka3/9/9/3P1P3/4C4/5N3/9/9/9/3K5 w - - 0 1",
            "炮马带双兵打双士：子力不多，赢在配合，两步之内藏着杀。"
        ),
        Puzzle(
            73, "车马双兵胜双士单象",
            "3akab2/9/9/3P1P3/5N3/9/9/8R/9/3K5 w - - 0 1",
            "车马带双兵打双士单象：子力占优，先敲掉守子再收官，稳扎稳打。"
        ),
        Puzzle(
            74, "双车双兵胜双士单象",
            "3akab2/9/9/3P1P3/9/9/R8/8R/9/3K5 w - - 0 1",
            "双车带双兵打双士单象：子力压倒，车封路、兵压宫，稳扎稳打收官。"
        ),
        Puzzle(
            75, "双炮双兵胜双士",
            "3aka3/9/9/3P1P3/2C3C2/9/9/9/9/3K5 w - - 0 1",
            "双炮带双兵打双士：兵当炮架、层层推进，三步之内见杀。"
        ),
        Puzzle(
            76, "马炮三兵胜士象全",
            "2bakab2/9/9/2P1P1P2/6C2/5N3/9/9/9/3K5 w - - 0 1",
            "马炮带三兵打士象全：兵多势众，步步推进，先破士象再收官。"
        ),
        Puzzle(
            77, "车双炮双兵胜双士",
            "3aka3/9/9/3P1P3/2C3C2/9/9/8R/9/3K5 w - - 0 1",
            "车双炮带双兵打双士：火力全开，两步之内见杀。"
        ),
        Puzzle(
            78, "双车双炮双兵胜双士单象",
            "3akab2/9/9/3P1P3/2C3C2/9/R8/8R/9/3K5 w - - 0 1",
            "双车双炮再带双兵：火力全开，三步之内见杀。"
        ),
        Puzzle(
            79, "车马三兵胜双士单象",
            "3akab2/9/9/2P1P1P2/5N3/9/9/8R/9/3K5 w - - 0 1",
            "车马带三兵打双士单象：兵临城下、车马齐攻，四步之内见杀。"
        ),
        Puzzle(
            80, "双炮三兵胜士象全",
            "2bakab2/9/9/2P1P1P2/2C3C2/9/9/9/9/3K5 w - - 0 1",
            "双炮带三兵打士象全：兵多炮利，先破士象再收官。"
        ),
        Puzzle(
            81, "车双马三兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/9/9/9/8R/9/3K5 w - - 0 1",
            "车双马再带三兵：子力压倒，两步之内见杀。"
        ),
        Puzzle(
            82, "双马炮双兵胜双士单象",
            "3akab2/9/2N3N2/3P1P3/4C4/9/9/9/9/3K5 w - - 0 1",
            "双马一炮再带双兵打双士单象：两步之内见杀。"
        ),
        Puzzle(
            83, "双马双炮双兵胜双士",
            "3aka3/9/2N3N2/3P1P3/2C3C2/9/9/9/9/3K5 w - - 0 1",
            "双马双炮再带双兵：一步见杀，找对那一步。"
        ),
        Puzzle(
            84, "车双炮三兵胜士象全",
            "2bakab2/9/9/2P1P1P2/2C3C2/9/9/8R/9/3K5 w - - 0 1",
            "车双炮带三兵打士象全：火力全开，两步之内见杀。"
        ),
        Puzzle(
            85, "车炮四兵胜双士单象",
            "3akab2/9/9/2P2P1P1/4C4/2P6/9/8R/9/3K5 w - - 0 1",
            "车炮带四兵打双士单象：兵多势众，两步之内见杀。"
        ),
        Puzzle(
            86, "车马胜单士单象",
            "2bak4/9/9/9/5N3/9/9/8R/9/3K5 w - - 0 1",
            "车马打单士单象：盘面空空荡荡，车马配合三步成杀。"
        ),
        Puzzle(
            87, "双车双炮三兵胜双士单象",
            "3akab2/9/9/2P1P1P2/2C3C2/9/R8/8R/9/3K5 w - - 0 1",
            "双车双炮再带三兵：火力全开，三步之内见杀。"
        ),
        Puzzle(
            88, "车马炮三兵胜士象全",
            "2bakab2/9/9/2P1P1P2/6C2/5N3/9/8R/9/3K5 w - - 0 1",
            "车马炮带三兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            89, "双车马炮三兵胜双士",
            "3aka3/9/9/2P1P1P2/6C2/5N3/R8/8R/9/3K5 w - - 0 1",
            "双车马炮再带三兵：两步之内见杀。"
        ),
        Puzzle(
            90, "车双马炮双兵胜双士单象",
            "3akab2/9/2N3N2/3P1P3/4C4/9/9/8R/9/3K5 w - - 0 1",
            "车双马一炮再带双兵打双士单象：两步之内见杀。"
        ),
        Puzzle(
            91, "双车双马双炮双兵胜双士",
            "3aka3/9/2N3N2/3P1P3/2C3C2/9/R8/8R/9/3K5 w - - 0 1",
            "双车双马双炮再带双兵：全军压上，一步见杀。"
        ),
        Puzzle(
            92, "车炮双马三兵胜双士单象",
            "3akab2/9/2N3N2/2P1P1P2/6C2/9/9/8R/9/3K5 w - - 0 1",
            "车炮双马带三兵打双士单象：三步之内见杀。"
        ),
        Puzzle(
            93, "双炮双马三兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/9/9/9/9/3K5 w - - 0 1",
            "双炮双马再带三兵：两步之内见杀。"
        ),
        Puzzle(
            94, "车双炮四兵胜双士",
            "3aka3/9/9/2P1P1P2/2C3C2/2P6/9/8R/9/3K5 w - - 0 1",
            "车双炮带四兵：四兵压境，两步之内见杀。"
        ),
        Puzzle(
            95, "双车双炮四兵胜双士",
            "3aka3/9/9/2P1P1P2/2C3C2/2P6/R8/8R/9/3K5 w - - 0 1",
            "双车双炮再带四兵：火力全开，两步之内见杀。"
        ),
        Puzzle(
            96, "车马双炮三兵胜双士单象",
            "3akab2/9/9/2P1P1P2/2C3C2/2N6/9/8R/9/3K5 w - - 0 1",
            "车马双炮带三兵打双士单象：四步之内见杀。"
        ),
        Puzzle(
            97, "双马双炮四兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/2P6/9/9/9/3K5 w - - 0 1",
            "双马双炮再带四兵：两步之内见杀。"
        ),
        Puzzle(
            98, "车双马双炮三兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/9/9/8R/9/3K5 w - - 0 1",
            "车双马双炮再带三兵：两步之内见杀。"
        ),
        Puzzle(
            99, "双车双马双炮三兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/9/R8/8R/9/3K5 w - - 0 1",
            "双车双马双炮再带三兵：全军压上，两步之内见杀。"
        ),
        Puzzle(
            100, "车双炮双马四兵胜双士单象",
            "3akab2/9/2N3N2/2P1P1P2/2C3C2/2P6/9/8R/9/3K5 w - - 0 1",
            "车双炮双马带四兵打双士单象：三步之内见杀。"
        ),
        Puzzle(
            101, "双车马炮四兵胜士象全",
            "2bakab2/9/2P1P1P2/6C2/5N3/2P6/R8/8R/9/3K5 w - - 0 1",
            "双车马炮带四兵打士象全：四步之内见杀。"
        ),
        Puzzle(
            102, "马双炮四兵胜双士单象",
            "3akab2/9/9/2P1P1P2/2C3C2/2P6/5N3/9/9/3K5 w - - 0 1",
            "马炮带四兵打双士单象：兵多势众，稳步收官。"
        ),
        Puzzle(
            103, "双车双马双炮四兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/2P6/R8/8R/9/3K5 w - - 0 1",
            "双车双马双炮再带四兵：全军压上，两步之内见杀。"
        ),
        Puzzle(
            104, "车马双炮四兵胜士象全",
            "2bakab2/9/2P1P1P2/2C3C2/2P6/5N3/9/8R/9/3K5 w - - 0 1",
            "车马双炮带四兵打士象全：两步之内见杀。"
        ),
        Puzzle(
            105, "双马双炮五兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/1P3P3/9/9/9/3K5 w - - 0 1",
            "双马双炮带满五兵：兵海压境，两步之内见杀。"
        ),
        Puzzle(
            106, "车双马四兵胜双士单象",
            "3akab2/9/2N3N2/2P1P1P2/9/1P3P3/9/8R/9/3K5 w - - 0 1",
            "车双马带四兵打双士单象：三步之内见杀。"
        ),
        Puzzle(
            107, "双车双马双炮五兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/1P3P3/R8/8R/9/3K5 w - - 0 1",
            "双车双马双炮带满五兵：全军压上，两步之内见杀。"
        ),
        Puzzle(
            108, "车马双炮四兵胜士象全（二）",
            "2bakab2/9/2N6/2P1P1P2/2C3C2/2P6/9/9/8R/3K5 w - - 0 1",
            "车马双炮带四兵打士象全：四步之内见杀。"
        ),
        Puzzle(
            109, "马双炮五兵胜双士",
            "3aka3/9/9/2P1P1P2/2C3C2/1P3P3/5N3/9/9/3K5 w - - 0 1",
            "马炮带满五兵：兵海压境，四步之内见杀。"
        ),
        Puzzle(
            110, "双车双马双炮五兵胜双士单象",
            "3akab2/9/2N3N2/2P1P1P2/2C3C2/1P3P3/R8/8R/9/3K5 w - - 0 1",
            "全军带满五兵打双士单象：两步之内见杀。"
        ),
        Puzzle(
            111, "车双炮五兵胜双士",
            "3aka3/9/9/2P1P1P2/2C3C2/1P3P3/9/8R/9/3K5 w - - 0 1",
            "车双炮带满五兵：兵海压境，两步之内见杀。"
        ),
        Puzzle(
            112, "双马炮五兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/6C2/1P3P3/9/9/9/3K5 w - - 0 1",
            "双马一炮带满五兵打士象全：稳步收官。"
        ),
        Puzzle(
            113, "车马炮五兵胜双士单象",
            "3akab2/9/9/2P1P1P2/6C2/1P3P3/5N3/9/8R/3K5 w - - 0 1",
            "车马炮带满五兵打双士单象：四步之内见杀。"
        ),
        Puzzle(
            114, "双车双马双炮五兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/2C3C2/1P3P3/R8/8R/9/3K5 w - - 0 1",
            "全军带满五兵打士象全：三步之内见杀。"
        ),
        Puzzle(
            115, "车双马双炮五兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/1P3P3/9/8R/9/3K5 w - - 0 1",
            "车双马双炮带满五兵：两步之内见杀。"
        ),
        Puzzle(
            116, "双车炮五兵胜双士单象",
            "3akab2/9/9/2P1P1P2/6C2/1P3P3/R8/8R/9/3K5 w - - 0 1",
            "双车一炮带满五兵打双士单象：四步之内见杀。"
        ),
        Puzzle(
            117, "马双炮四兵胜双士",
            "3aka3/9/9/2P1P1P2/2C3C2/2P6/9/5N3/9/3K5 w - - 0 1",
            "马炮带四兵：马伏后阵、炮兵压上，四步之内见杀。"
        ),
        Puzzle(
            118, "双车双马双炮五兵胜双士单象（二）",
            "2baka3/9/2N3N2/2P1P1P2/2C3C2/1P3P3/R8/8R/9/3K5 w - - 0 1",
            "全军带满五兵打双士单象：两步之内见杀。"
        ),
        Puzzle(
            119, "车马双炮五兵胜双士",
            "3aka3/9/9/2P1P1P2/2C3C2/1P3P3/5N3/9/8R/3K5 w - - 0 1",
            "车马双炮带满五兵：两步之内见杀。"
        ),
        Puzzle(
            120, "双炮五兵胜双士单象",
            "3akab2/9/9/2P1P1P2/2C3C2/1P3P3/9/9/9/3K5 w - - 0 1",
            "双炮带满五兵打双士单象：兵海压境，稳步收官。"
        ),
        Puzzle(
            121, "双车双炮双兵胜士象全",
            "2bakab2/9/9/3P1P3/2C3C2/9/9/R8/8R/3K5 w - - 0 1",
            "双车双炮带双兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            122, "双车双炮四兵胜士象全",
            "2bakab2/9/9/2P1P1P2/2C3C2/2P6/9/R8/8R/3K5 w - - 0 1",
            "双车双炮带四兵打士象全：两步之内见杀。"
        ),
        Puzzle(
            123, "马双炮五兵胜双士（二）",
            "3aka3/9/9/2P1P1P2/2C3C2/1P3P3/9/3N5/9/3K5 w - - 0 1",
            "马双炮带满五兵打双士：四步之内见杀。"
        ),
        Puzzle(
            124, "双马双炮四兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/2C3C2/2P6/9/9/9/3K5 w - - 0 1",
            "双马双炮带四兵打士象全：四步之内见杀。"
        ),
        Puzzle(
            125, "车双炮五兵胜士象全",
            "2bakab2/9/9/2P1P1P2/2C3C2/1P3P3/9/8R/9/3K5 w - - 0 1",
            "车双炮带满五兵打士象全：两步之内见杀。"
        ),
        Puzzle(
            126, "双车双马五兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/9/1P3P3/9/R8/8R/3K5 w - - 0 1",
            "双车双马带满五兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            127, "车双马双炮四兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/2C3C2/2P6/9/9/8R/3K5 w - - 0 1",
            "车双马双炮带四兵打双士：两步之内见杀。"
        ),
        Puzzle(
            128, "双炮四兵胜双士",
            "3aka3/9/9/2P1P1P2/2C3C2/2P6/9/9/9/3K5 w - - 0 1",
            "双炮带四兵打双士：四步之内见杀。"
        ),
        Puzzle(
            129, "车炮五兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/1P3P3/C8/9/8R/3K5 w - - 0 1",
            "车炮带满五兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            130, "车双炮四兵胜双士（二）",
            "3aka3/9/9/2P1P1P2/9/1P3P3/C6C1/9/8R/3K5 w - - 0 1",
            "车双炮带四兵打双士：两步之内见杀。"
        ),
        Puzzle(
            131, "双车双马四兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/9/2P6/9/R8/8R/3K5 w - - 0 1",
            "双车双马带四兵打双士：两步之内见杀。"
        ),
        Puzzle(
            132, "马双炮四兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/2P6/C6C1/9/5N3/3K5 w - - 0 1",
            "马双炮带四兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            133, "车马炮五兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/1P3P3/C8/5N3/8R/3K5 w - - 0 1",
            "车马炮带满五兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            134, "双马五兵胜士象全",
            "2bakab2/9/2N3N2/9/2P1P1P2/9/1P3P3/9/9/3K5 w - - 0 1",
            "双马带满五兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            135, "车双马四兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/9/2P6/9/9/8R/3K5 w - - 0 1",
            "车双马带四兵打双士：两步之内见杀。"
        ),
        Puzzle(
            136, "双炮五兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/1P3P3/9/C6C1/9/3K5 w - - 0 1",
            "双炮带满五兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            137, "马炮五兵胜双士",
            "3aka3/9/9/2P1P1P2/3N5/1P3P3/9/2C6/9/3K5 w - - 0 1",
            "马炮带满五兵打双士：子力压倒，稳步收官。"
        ),
        Puzzle(
            138, "双车炮四兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/2P6/C8/R7R/9/3K5 w - - 0 1",
            "双车炮带四兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            139, "车炮三兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/9/9/C8/8R/3K5 w - - 0 1",
            "车炮带三兵打士象全：子力占优，稳步收官。"
        ),
        Puzzle(
            140, "马炮三兵胜士象全（二）",
            "2bakab2/9/9/2P1P1P2/3N5/9/9/2C6/9/3K5 w - - 0 1",
            "马炮带三兵打士象全：五步之内见杀，手筋要算准。"
        ),
        Puzzle(
            141, "双马三兵胜双士",
            "3aka3/9/2N3N2/2P1P1P2/9/9/9/9/9/3K5 w - - 0 1",
            "双马带三兵打双士：两步之内见杀。"
        ),
        Puzzle(
            142, "车马四兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/1P7/9/5N3/8R/3K5 w - - 0 1",
            "车马带四兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            143, "车双炮三兵胜双士",
            "3aka3/9/9/2P1P1P2/9/9/9/C6C1/8R/3K5 w - - 0 1",
            "车双炮带三兵打双士：两步之内见杀。"
        ),
        Puzzle(
            144, "双马炮三兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/9/9/9/2C6/9/3K5 w - - 0 1",
            "双马炮带三兵打士象全：四步之内见杀。"
        ),
        Puzzle(
            145, "车马双炮三兵胜双士",
            "3aka3/9/9/2P1P1P2/3N5/9/9/C6C1/8R/3K5 w - - 0 1",
            "车马双炮带三兵打双士：两步之内见杀。"
        ),
        Puzzle(
            146, "双车二兵胜双士",
            "3aka3/9/9/2P3P2/9/9/9/9/R7R/3K5 w - - 0 1",
            "双车带二兵打双士：两步之内见杀。"
        ),
        Puzzle(
            147, "车炮二兵胜士象全",
            "2bakab2/9/9/2P3P2/9/9/9/C8/8R/3K5 w - - 0 1",
            "车炮带二兵打士象全：子力占优，稳步收官。"
        ),
        Puzzle(
            148, "车双马二兵胜双士",
            "3aka3/9/2N3N2/2P3P2/9/9/9/9/8R/3K5 w - - 0 1",
            "车双马带二兵打双士：两步之内见杀。"
        ),
        Puzzle(
            149, "车马三兵胜双士",
            "3aka3/9/9/2P1P1P2/9/9/9/5N3/8R/3K5 w - - 0 1",
            "车马带三兵打双士：五步之内见杀，手筋要算准。"
        ),
        Puzzle(
            150, "双炮马三兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/9/9/C6C1/5N3/3K5 w - - 0 1",
            "双炮马带三兵打士象全：子力占优，稳步收官。"
        ),
        Puzzle(
            151, "车炮四兵胜双士",
            "3aka3/9/2P3P2/9/2P3P2/9/9/C8/8R/3K5 w - - 0 1",
            "车炮带四兵打双士：两步之内见杀。"
        ),
        Puzzle(
            152, "双车三兵胜士象全",
            "2bakab2/9/9/2P1P1P2/9/9/9/9/R7R/3K5 w - - 0 1",
            "双车带三兵打士象全：子力压倒，稳步收官。"
        ),
        Puzzle(
            153, "车双马四兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/1P7/9/9/9/8R/3K5 w - - 0 1",
            "车双马带四兵打士象全：四步之内见杀。"
        ),
        Puzzle(
            154, "车炮三兵胜双士",
            "3aka3/9/9/2P1P1P2/9/9/2C6/9/8R/3K5 w - - 0 1",
            "车炮带三兵打双士：两步之内见杀。"
        ),
        Puzzle(
            155, "双马四兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/1P7/9/9/9/9/3K5 w - - 0 1",
            "双马带四兵打士象全：子力占优，稳步收官。"
        ),
        Puzzle(
            156, "车马四兵胜双士",
            "3aka3/9/9/2P1P1P2/1P7/9/5N3/9/8R/3K5 w - - 0 1",
            "车马带四兵打双士：五步之内见杀，手筋要算准。"
        ),
        Puzzle(
            157, "双车四兵胜双士",
            "3aka3/9/2P3P2/9/2P3P2/9/9/9/R7R/3K5 w - - 0 1",
            "双车带四兵打双士：两步之内见杀。"
        ),
        Puzzle(
            158, "马炮四兵胜士象全",
            "2bakab2/9/9/2P1P1P2/1P7/9/9/2C6/5N3/3K5 w - - 0 1",
            "马炮带四兵打士象全：子力占优，稳步收官。"
        ),
        Puzzle(
            159, "双车双炮三兵胜双士",
            "3aka3/9/9/2P1P1P2/9/9/C6C1/9/R7R/3K5 w - - 0 1",
            "双车双炮带三兵打双士：两步之内见杀。"
        ),
        Puzzle(
            160, "单车马双炮四兵胜士象全",
            "2bakab2/9/2N3N2/2P1P1P2/1P7/9/C6C1/9/8R/3K5 w - - 0 1",
            "车马双炮带四兵打士象全：三步之内见杀。"
        )
    )

    /** The daily puzzle: stable within a day, rotates over the set. */
    fun daily(dayOfYear: Int): Puzzle = all.sortedBy { it.id }[dayOfYear % all.size]

    /** The daily puzzle of a specific calendar date, given as
     *  an epoch day (1.32.0). Uses that date's own day-of-year,
     *  so it always names the puzzle the app showed (or will
     *  show) on that date — the basis for catching up on missed
     *  dailies. */
    fun dailyForEpochDay(epochDay: Long): Puzzle =
        daily(java.time.LocalDate.ofEpochDay(epochDay).dayOfYear)

    /** The epoch days of the last [count] daily puzzles,
     *  today first (1.32.0). */
    fun recentDailyDays(todayEpochDay: Long, count: Int = 7): List<Long> =
        (0 until count).map { todayEpochDay - it }

    /** Display grouping by the lead attacking material, in the
     *  order the list page shows them. */
    val categoryOrder: List<String> =
        listOf("双车类", "车马类", "车炮类", "车兵类", "马炮类", "双炮双马类")

    private val categoryById: Map<Int, String> = mapOf(
        1 to "双车类", 16 to "双车类", 25 to "双车类", 28 to "双车类", 31 to "双车类",
        33 to "双车类", 38 to "双车类",
        3 to "车马类", 11 to "车马类", 19 to "车马类", 29 to "车马类", 32 to "车马类",
        34 to "车马类",
        9 to "车炮类", 17 to "车炮类", 24 to "车炮类", 26 to "车炮类",
        39 to "车炮类",
        4 to "车兵类", 5 to "车兵类", 8 to "车兵类", 13 to "车兵类",
        15 to "车兵类", 20 to "车兵类", 21 to "车兵类", 30 to "车兵类",
        36 to "车兵类",
        2 to "马炮类", 7 to "马炮类", 10 to "马炮类", 12 to "马炮类",
        14 to "马炮类", 23 to "马炮类", 37 to "马炮类", 40 to "马炮类",
        6 to "双炮双马类", 18 to "双炮双马类", 22 to "双炮双马类", 27 to "双炮双马类",
        35 to "双炮双马类", 44 to "双炮双马类",
        41 to "车马类", 45 to "车马类", 48 to "车马类",
        42 to "双车类", 47 to "双车类",
        43 to "车炮类",
        46 to "马炮类",
        49 to "马炮类", 50 to "车炮类", 51 to "车马类",
        52 to "双炮双马类", 53 to "车马类",
        54 to "马炮类", 55 to "双炮双马类", 56 to "双炮双马类", 57 to "车炮类",
        58 to "双炮双马类", 59 to "双车类", 60 to "车马类", 61 to "双车类",
        62 to "马炮类", 63 to "车炮类", 64 to "车兵类", 65 to "车马类",
        66 to "马炮类", 67 to "双炮双马类", 68 to "双车类", 69 to "车炮类",
        70 to "车马类", 71 to "双炮双马类", 72 to "马炮类", 73 to "车马类",
        74 to "双车类", 75 to "双炮双马类", 76 to "马炮类", 77 to "车炮类",
        78 to "双车类", 79 to "车马类", 80 to "双炮双马类", 81 to "车马类",
        82 to "双炮双马类", 83 to "双炮双马类", 84 to "车炮类", 85 to "车炮类",
        86 to "车马类", 87 to "双车类", 88 to "车马类", 89 to "双车类",
        90 to "车马类", 91 to "双车类", 92 to "车马类", 93 to "双炮双马类",
        94 to "车炮类", 95 to "双车类", 96 to "车马类", 97 to "双炮双马类",
        98 to "车马类", 99 to "双车类", 100 to "车马类", 101 to "双车类",
        102 to "马炮类", 103 to "双车类", 104 to "车马类", 105 to "双炮双马类",
        106 to "车马类", 107 to "双车类", 108 to "车马类", 109 to "马炮类",
        110 to "双车类", 111 to "车炮类", 112 to "双炮双马类", 113 to "车马类",
        114 to "双车类", 115 to "车马类", 116 to "双车类", 117 to "马炮类",
        118 to "双车类", 119 to "车马类", 120 to "双炮双马类", 121 to "双车类",
        122 to "双车类", 123 to "马炮类", 124 to "双炮双马类", 125 to "车炮类",
        126 to "双车类", 127 to "车马类", 128 to "双炮双马类", 129 to "车炮类",
        130 to "车炮类", 131 to "双车类", 132 to "马炮类", 133 to "车马类",
        134 to "双炮双马类", 135 to "车马类", 136 to "双炮双马类", 137 to "马炮类",
        138 to "双车类", 139 to "车炮类", 140 to "马炮类", 141 to "双炮双马类",
        142 to "车马类", 143 to "车炮类", 144 to "双炮双马类", 145 to "车马类",
        146 to "双车类", 147 to "车炮类", 148 to "车马类",
        149 to "车马类", 150 to "双炮双马类", 151 to "车炮类", 152 to "双车类",
        153 to "车马类", 154 to "车炮类", 155 to "双炮双马类", 156 to "车马类",
        157 to "双车类", 158 to "马炮类", 159 to "双车类", 160 to "车马类"
    )

    /** 1 = material crush, 2 = standard textbook win,
     *  3 = sparse or patience finish that is easy to throw away. */
    private val difficultyById: Map<Int, Int> = mapOf(
        1 to 1, 16 to 1, 19 to 1, 25 to 1, 28 to 1, 31 to 1,
        33 to 1, 34 to 1, 38 to 1, 41 to 1, 42 to 1, 47 to 1, 52 to 1, 53 to 1,
        54 to 1, 57 to 1, 59 to 1, 60 to 1, 61 to 1, 63 to 1, 64 to 1, 65 to 1,
        68 to 1, 70 to 1, 71 to 1, 73 to 1, 74 to 1, 77 to 1, 78 to 1, 81 to 1, 82 to 1, 83 to 1, 84 to 1, 85 to 1, 87 to 1, 88 to 1, 89 to 1, 90 to 1, 91 to 1, 93 to 1, 94 to 1, 95 to 1, 97 to 1, 98 to 1, 99 to 1, 102 to 1, 103 to 1, 104 to 1, 105 to 1, 107 to 1, 110 to 1, 111 to 1, 112 to 1, 115 to 1, 118 to 1, 119 to 1, 120 to 1, 121 to 1, 122 to 1, 125 to 1, 126 to 1, 127 to 1, 129 to 1, 130 to 1, 131 to 1, 132 to 1, 133 to 1, 134 to 1, 135 to 1, 136 to 1, 137 to 1, 138 to 1, 139 to 1, 141 to 1, 142 to 1, 143 to 1, 145 to 1, 146 to 1, 147 to 1, 148 to 1, 150 to 1, 151 to 1, 152 to 1, 154 to 1, 155 to 1, 157 to 1, 158 to 1, 159 to 1,
        62 to 3, 66 to 3,
        7 to 3, 8 to 3, 18 to 3, 20 to 3, 22 to 3, 40 to 3, 46 to 3
    )

    fun categoryOf(id: Int): String = categoryById[id] ?: "综合类"

    fun difficultyOf(id: Int): Int = difficultyById[id] ?: 2

    /** The next puzzle to practise after [currentId] (1.38.0):
     *  unsolved ones in the same category first (list order,
     *  wrapping around), then unsolved ones anywhere else.
     *  Null when every other puzzle is already solved. Pure,
     *  pinned by unit tests. */
    fun nextUnsolved(solved: Set<Int>, currentId: Int): Puzzle? {
        val idx = all.indexOfFirst { it.id == currentId }
        if (idx < 0) return null
        val cat = categoryOf(currentId)
        val ordered = all.drop(idx + 1) + all.take(idx)
        return ordered.firstOrNull {
            it.id !in solved && categoryOf(it.id) == cat
        } ?: ordered.firstOrNull { it.id !in solved }
    }

    /** The list page's filter (1.19.0): keep puzzles of one
     *  difficulty tier (null = all tiers) and optionally only
     *  the ones not solved yet. Pure, so the tier distribution
     *  and the unsolved logic are pinned by unit tests. */
    fun filterPuzzles(
        list: List<Puzzle>,
        difficulty: Int?,
        unsolvedOnly: Boolean,
        solvedIds: Set<Int>
    ): List<Puzzle> = list.filter { p ->
        (difficulty == null || difficultyOf(p.id) == difficulty) &&
            (!unsolvedOnly || p.id !in solvedIds)
    }

    /** Ids lacking an explicit category entry — the unit test
     *  pins this to empty so a new puzzle can never silently land
     *  in the fallback bucket. (Difficulty defaults to 2 by
     *  design; only the extremes are listed.) */
    fun idsWithoutCategory(): Set<Int> =
        all.map { it.id }.filter { it !in categoryById }.toSet()
}

/** Curated positions for the guess-the-best-move trainer, grouped
 *  by difficulty (12 per tier since 1.28.0). The engine's top move
 *  is the answer; the position only needs to be interesting, not
 *  forced. The endgame entries reuse Puzzles faces verbatim, so
 *  they inherit the same FEN sanity coverage in PuzzleFenTest. */
object GuessPositions {
    val easy: List<Pair<String, String>> = listOf(
        "车兵对单马局面" to "4k4/9/2n6/4P4/9/9/R8/9/9/4K4 w - - 0 1",
        "车兵对双士局面" to "3aka3/9/9/4P4/9/9/8R/9/9/4K4 w - - 0 1",
        "马兵对士象全" to "2bakab2/9/9/3NP4/9/9/9/9/9/3K5 w - - 0 1",
        "马炮对士象全" to "2bakab2/9/4N4/9/9/4C4/9/9/9/4K4 w - - 0 1",
        "双车带兵局面" to "2bakab2/9/R7R/4P4/9/9/9/9/9/4K4 w - - 0 1",
        "双车马对士象全" to "2bakab2/9/9/3N5/R8/8R/9/9/9/3K5 w - - 0 1",
        "双车炮兵齐攻" to "2bakab2/9/9/3P5/9/R7R/4C4/9/9/4K4 w - - 0 1",
        "车马炮联攻局面" to "2bakab2/9/3R5/4N4/4C4/9/9/9/9/4K4 w - - 0 1",
        "单车对马双士局面" to "3aka3/9/9/2n6/9/9/9/3R5/9/3K5 w - - 0 1",
        "马炮兵对单马局面" to "4k4/9/9/2n1N4/4P4/9/4C4/9/9/3K5 w - - 0 1",
        "双车胜士象全局面" to "2bakab2/9/9/9/9/9/9/9/3R3R1/3K5 w - - 0 1",
        "车兵对单炮局面" to "4k4/9/7c1/4P4/9/2R6/9/9/9/4K4 w - - 0 1"
    )
    val normal: List<Pair<String, String>> = listOf(
        "标准开局" to
            "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
        "中炮对进马" to
            "rnbakab1r/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR w - - 0 2",
        "双车炮对士象全" to "2bakab2/9/3R1R3/9/4C4/9/9/9/9/4K4 w - - 0 1",
        "车马对士象全" to "2bakab2/9/3R1N3/9/9/9/9/9/9/3K5 w - - 0 1",
        "车炮胜士象全" to "2bakab2/9/9/9/4C4/9/9/3R5/9/4K4 w - - 0 1",
        "车炮兵攻士象全" to "2bakab2/9/9/4P4/9/4C4/9/3R5/9/4K4 w - - 0 1",
        "马炮兵对士象全" to "2bakab2/9/9/3NP4/9/4C4/9/9/9/3K5 w - - 0 1",
        "双车对马炮" to "4k4/9/2n3c2/9/8R/9/R8/9/9/3K5 w - - 0 1",
        "车马双兵对士象全" to "2bakab2/9/9/6N2/3P1P3/2R6/9/9/9/3K5 w - - 0 1",
        "车马炮胜士象全" to "2bakab2/9/9/4N4/4C4/9/9/9/5R3/4K4 w - - 0 1",
        "车炮双兵压宫" to "2bakab2/9/9/3P1P3/9/3R5/7C1/9/9/3K5 w - - 0 1",
        "马双炮带兵" to "2bakab2/9/9/4P4/4N4/7C1/1C7/9/9/4K4 w - - 0 1"
    )
    val hard: List<Pair<String, String>> = listOf(
        "单车对马炮" to "4k4/9/2c6/6n2/4R4/9/9/9/9/4K4 w - - 0 1",
        "重炮加车" to "3ak4/9/4C4/4C4/9/9/9/9/R8/4K4 w - - 0 1",
        "双炮兵对士象全" to "2bakab2/9/9/4P4/2C3C2/9/9/9/9/4K4 w - - 0 1",
        "卧槽马加炮" to "2bakab2/9/2N6/9/9/9/4C4/9/9/4K4 w - - 0 1",
        "双马胜士象全" to "2bakab2/9/2N3N2/9/9/9/9/9/9/3K5 w - - 0 1",
        "单车胜士象全" to "2bakab2/9/9/9/9/9/9/3R5/9/3K5 w - - 0 1",
        "双炮胜士象全" to "2bakab2/9/9/9/2C3C2/9/9/9/9/3K5 w - - 0 1",
        "单车对炮双士" to "3aka3/9/9/9/4c4/9/R8/9/9/4K4 w - - 0 1",
        "车马对双炮双士" to "3aka3/9/1c5c1/9/5N3/2R6/9/9/9/3K5 w - - 0 1",
        "车带三兵胜士象全" to "2bakab2/9/9/4P4/1RP3P2/9/9/9/9/4K4 w - - 0 1",
        "双马炮对士象全" to "2bakab2/9/2N3N2/9/9/9/4C4/9/9/4K4 w - - 0 1",
        "车马对马士象" to "2bakab2/9/7R1/3n5/4N4/9/9/9/9/4K4 w - - 0 1"
    )

    /** Opening-book positions (1.30.0): book lines replayed
     *  4–6 plies from the standard start, so the side to move
     *  faces a real 定式 position instead of an endgame face.
     *  Derived from OpeningBook itself, so the pool can never
     *  drift from the book the app ships; legality is replayed
     *  through the real state machine and duplicates (the same
     *  position reached by two lines) are dropped. Pinned by
     *  TrainingPoolsTest. */
    val opening: List<Pair<String, String>> by lazy {
        val seen = HashSet<String>()
        val out = ArrayList<Pair<String, String>>()
        for (node in OpeningBook.allNodes()) {
            val plies = node.historyUci.size
            if (plies !in 4..6) continue
            val gs = GameState()
            var ok = true
            for (uci in node.historyUci) {
                val ft = Fen.parseUciMove(uci)
                if (ft == null || !gs.tryMove(ft.first, ft.second)) {
                    ok = false
                    break
                }
            }
            if (!ok) continue
            val fen = Fen.toFen(gs)
            if (!seen.add(fen)) continue
            val notes = OpeningBook.historyNotation(node.historyUci)
            // The label names the 定式 line the position comes
            // from (1.33.0) so a question reads as a position
            // from a real line, not an anonymous book node.
            val line = OpeningBook.lineName(node.historyUci)
            val label = (if (line != null) "$line · " else "") +
                "定式走了 " + plies + " 手 · " +
                notes.takeLast(2).joinToString(" ")
            out.add(label to fen)
        }
        out
    }
}

/** One wrong-book entry: a position the user guessed wrong on,
 *  with the engine's best move at that moment. */
data class WrongEntry(
    val fen: String,
    val correctUci: String,
    val title: String,
    val dateMs: Long
)

class WrongBookStore(context: Context) {
    private val file = File(context.applicationContext.filesDir, "wrongbook.json")

    fun loadAll(): List<WrongEntry> {
        if (!file.exists()) return emptyList()
        return try {
            val arr = JSONArray(file.readText())
            val out = ArrayList<WrongEntry>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    WrongEntry(
                        fen = o.getString("fen"),
                        correctUci = o.optString("uci", ""),
                        title = o.optString("title", "错题"),
                        dateMs = o.optLong("dateMs", 0L)
                    )
                )
            }
            out.sortedByDescending { it.dateMs }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun add(entry: WrongEntry) {
        val all = loadAll().filter { it.fen != entry.fen } + entry
        writeAll(all)
    }

    fun remove(fen: String) {
        writeAll(loadAll().filter { it.fen != fen })
    }

    fun replaceAll(entries: List<WrongEntry>) {
        writeAll(entries)
    }

    private fun writeAll(entries: List<WrongEntry>) {
        val arr = JSONArray()
        for (e in entries) {
            val o = JSONObject()
            o.put("fen", e.fen)
            o.put("uci", e.correctUci)
            o.put("title", e.title)
            o.put("dateMs", e.dateMs)
            arr.put(o)
        }
        file.writeText(arr.toString())
    }
}
