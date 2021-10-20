package com.wanakanajava

import java.util.*
import java.util.regex.Pattern

/**
 * A Java version of the Javascript WanaKana romaji-to-kana converter library (https://github.com/WaniKani/WanaKana)
 * Version 1.1.1
 */
object WanaKanaJava {
    var useObsoleteKana :Boolean = false
    var mOptions =
        HashMap<String, Boolean>()
    var mRtoJ =
        HashMap<String, String>()
    var mJtoR =
        HashMap<String, String>()

    // Pass every character of a string through a function and return TRUE if every character passes the function's check
    private fun _allTrue(
        checkStr: String,
        func: (String) -> Boolean
    ): Boolean {
        for (element in checkStr) {
            if (!func(element.toString())) {
                return false
            }
        }
        return true
    }

    // Check if a character is within a Unicode range
    private fun _isCharInRange(chr: Char, start: Int, end: Int): Boolean {
        val code = chr.toInt()
        return code in start..end
    }

    private fun _isCharVowel(chr: Char, includeY: Boolean): Boolean {
        val regexp =
            if (includeY) Pattern.compile("[aeiouy]") else Pattern.compile(
                "[aeiou]"
            )
        val matcher = regexp.matcher(chr.toString())
        return matcher.find()
    }

    private fun _isCharConsonant(chr: Char, includeY: Boolean): Boolean {
        val regexp =
            if (includeY) Pattern.compile("[bcdfghjklmnpqrstvwxyz]") else Pattern.compile(
                "[bcdfghjklmnpqrstvwxz]"
            )
        val matcher = regexp.matcher(chr.toString())
        return matcher.find()
    }

    private fun _isCharKatakana(chr: Char): Boolean {
        return _isCharInRange(
            chr,
            KATAKANA_START,
            KATAKANA_END
        )
    }

    private fun _isCharHiragana(chr: Char): Boolean {
        return _isCharInRange(
            chr,
            HIRAGANA_START,
            HIRAGANA_END
        )
    }

    private fun _isCharKana(chr: Char): Boolean {
        return _isCharHiragana(chr) || _isCharKatakana(chr)
    }

    private fun _katakanaToHiragana(kata: String): String {
        var code: Int
        var hira = ""
        for (_i in 0 until kata.length) {
            val kataChar = kata[_i]
            if (_isCharKatakana(kataChar)) {
                code = kataChar.toInt()
                code += HIRAGANA_START - KATAKANA_START
                hira += String(Character.toChars(code))
            } else {
                hira += kataChar
            }
        }
        return hira
    }

    private fun _hiraganaToKatakana(hira: String?): String {
        var code: Int
        var kata = ""
        for (_i in 0 until hira!!.length) {
            val hiraChar = hira[_i]
            if (_isCharHiragana(hiraChar)) {
                code = hiraChar.toInt()
                code += KATAKANA_START - HIRAGANA_START
                kata += String(Character.toChars(code))
            } else {
                kata += hiraChar
            }
        }
        return kata
    }

    fun _hiraganaToRomaji(hira: String): String? {
        if (isRomaji(hira)) {
            return hira
        }
        var chunk = ""
        var chunkSize: Int
        var cursor = 0
        val len = hira.length
        val maxChunk = 2
        var nextCharIsDoubleConsonant = false
        var roma: String? = ""
        var romaChar: String? = null
        while (cursor < len) {
            chunkSize = Math.min(maxChunk, len - cursor)
            while (chunkSize > 0) {
                chunk = hira.substring(cursor, cursor + chunkSize)
                if (isKatakana(chunk)) {
                    chunk = _katakanaToHiragana(chunk)
                }
                if (chunk[0].toString() == "っ" && chunkSize == 1 && cursor < len - 1) {
                    nextCharIsDoubleConsonant = true
                    romaChar = ""
                    break
                }
                romaChar = mJtoR[chunk]
                if (romaChar != null && nextCharIsDoubleConsonant) {
                    romaChar = romaChar[0].toString() + romaChar
                    nextCharIsDoubleConsonant = false
                }
                if (romaChar != null) {
                    break
                }
                chunkSize--
            }
            if (romaChar == null) {
                romaChar = chunk
            }
            roma += romaChar
            cursor += if (chunkSize > 0) chunkSize else 1
        }
        return roma
    }

    private fun _romajiToHiragana(roma: String): String? {
        return _romajiToKana(roma, true)
    }

    private fun _romajiToKana(roma: String, ignoreCase: Boolean): String? {
        var ignoreCase: Boolean? = ignoreCase
        var chunk = ""
        var chunkLC = ""
        var chunkSize: Int
        var position = 0
        val len = roma.length
        val maxChunk = 3
        var kana: String? = ""
        var kanaChar: String? = ""
        if (ignoreCase == null) {
            ignoreCase = false
        }
        while (position < len) {
            chunkSize = Math.min(maxChunk, len - position)
            while (chunkSize > 0) {
                chunk = roma.substring(position, position + chunkSize)
                chunkLC = chunk.toLowerCase()
                if ((chunkLC == "lts" || chunkLC == "xts") && len - position >= 4) {
                    chunkSize++
                    // The second parameter in substring() is an end point, not a length!
                    chunk = roma.substring(position, position + chunkSize)
                    chunkLC = chunk.toLowerCase()
                }
                if (chunkLC[0].toString() == "n") { // Convert n' to ん
                    if (mOptions[OPTION_IME_MODE]!! && chunk.length == 2 && chunkLC[1].toString() == "'"
                    ) {
                        chunkSize = 2
                        chunk = "nn"
                        chunkLC = chunk.toLowerCase()
                    }
                    // If the user types "nto", automatically convert "n" to "ん" first
// "y" is excluded from the list of consonants so we can still get にゃ, にゅ, and にょ
                    if (chunk.length > 2 && _isCharConsonant(
                            chunkLC[1],
                            false
                        ) && _isCharVowel(chunkLC[2], true)
                    ) {
                        chunkSize = 1
                        // I removed the "n"->"ん" mapping because the IME wouldn't let me type "na" for "な" without returning "んあ",
// so the chunk needs to be manually set to a value that will map to "ん"
                        chunk = "nn"
                        chunkLC = chunk.toLowerCase()
                    }
                }
                // Prepare to return a small-つ because we're looking at double-consonants.
                if (chunk.length > 1 && chunkLC[0].toString() != "n" && _isCharConsonant(
                        chunkLC[0],
                        true
                    ) && chunk[0] == chunk[1]
                ) {
                    chunkSize = 1
                    // Return a small katakana ツ when typing in uppercase
                    if (_isCharInRange(
                            chunk[0],
                            UPPERCASE_START,
                            UPPERCASE_END
                        )
                    ) {
                        chunk = "ッ"
                        chunkLC = chunk
                    } else {
                        chunk = "っ"
                        chunkLC = chunk
                    }
                }
                kanaChar = mRtoJ[chunkLC]
                if (kanaChar != null) {
                    break
                }
                chunkSize--
            }
            if (kanaChar == null) {
                chunk = _convertPunctuation(chunk[0].toString())
                kanaChar = chunk
            }
            if (mOptions[OPTION_USE_OBSOLETE_KANA]!!) {
                if (chunkLC == "wi") {
                    kanaChar = "ゐ"
                }
                if (chunkLC == "we") {
                    kanaChar = "ゑ"
                }
            }
            if (roma.length > position + 1 && mOptions[OPTION_IME_MODE]!! && chunkLC[0].toString() == "n"
            ) {
                if (roma[position + 1].toString().toLowerCase() == "y" && position == len - 2 || position == len - 1) {
                    kanaChar = chunk[0].toString()
                }
            }
            if (!ignoreCase) {
                if (_isCharInRange(
                        chunk[0],
                        UPPERCASE_START,
                        UPPERCASE_END
                    )
                ) {
                    kanaChar = _hiraganaToKatakana(kanaChar)
                }
            }
            kana += kanaChar
            position += if (chunkSize > 0) chunkSize else 1
        }
        return kana
    }

    private fun _convertPunctuation(input: String): String {
        if (input == '　'.toString()) {
            return ' '.toString()
        }
        return if (input == '-'.toString()) {
            'ー'.toString()
        } else input
    }

    /**
     * Returns true if input is entirely hiragana.
     */
    fun isHiragana(input: String): Boolean {
        return _allTrue(input) { str: String ->
            _isCharHiragana(str[0])
        }
    }

    fun isKatakana(input: String): Boolean {
        return _allTrue(input) {
            _isCharKatakana(it[0])
        }
    }

    fun isKana(input: String): Boolean {
        return _allTrue(input) {
            isHiragana(it) || isKatakana(it)
        }
    }

    fun isRomaji(input: String): Boolean {
        return _allTrue(input) {
            !isHiragana(it) && !isKatakana(it)
        }
    }

    fun toHiragana(input: String): String? {
        if (isRomaji(input)) {
            return _romajiToHiragana(input)
        }
        return if (isKatakana(input)) {
            _katakanaToHiragana(input)
        } else input
    }

    fun toKatakana(input: String): String {
        if (isHiragana(input)) {
            return _hiraganaToKatakana(input)
        }
        return if (isRomaji(input)) {
            _hiraganaToKatakana(_romajiToHiragana(input))
        } else input
    }

    fun toKana(input: String): String? {
        return _romajiToKana(input, false)
    }

    fun toRomaji(input: String): String? {
        return _hiraganaToRomaji(input)
    }

    private fun prepareRtoJ() {
        mRtoJ["a"] = "あ"
        mRtoJ["i"] = "い"
        mRtoJ["u"] = "う"
        mRtoJ["e"] = "え"
        mRtoJ["o"] = "お"
        mRtoJ["yi"] = "い"
        mRtoJ["wu"] = "う"
        mRtoJ["whu"] = "う"
        mRtoJ["xa"] = "ぁ"
        mRtoJ["xi"] = "ぃ"
        mRtoJ["xu"] = "ぅ"
        mRtoJ["xe"] = "ぇ"
        mRtoJ["xo"] = "ぉ"
        mRtoJ["xyi"] = "ぃ"
        mRtoJ["xye"] = "ぇ"
        mRtoJ["ye"] = "いぇ"
        mRtoJ["wha"] = "うぁ"
        mRtoJ["whi"] = "うぃ"
        mRtoJ["whe"] = "うぇ"
        mRtoJ["who"] = "うぉ"
        mRtoJ["wi"] = "うぃ"
        mRtoJ["we"] = "うぇ"
        mRtoJ["va"] = "ゔぁ"
        mRtoJ["vi"] = "ゔぃ"
        mRtoJ["vu"] = "ゔ"
        mRtoJ["ve"] = "ゔぇ"
        mRtoJ["vo"] = "ゔぉ"
        mRtoJ["vya"] = "ゔゃ"
        mRtoJ["vyi"] = "ゔぃ"
        mRtoJ["vyu"] = "ゔゅ"
        mRtoJ["vye"] = "ゔぇ"
        mRtoJ["vyo"] = "ゔょ"
        mRtoJ["ka"] = "か"
        mRtoJ["ki"] = "き"
        mRtoJ["ku"] = "く"
        mRtoJ["ke"] = "け"
        mRtoJ["ko"] = "こ"
        mRtoJ["lka"] = "ヵ"
        mRtoJ["lke"] = "ヶ"
        mRtoJ["xka"] = "ヵ"
        mRtoJ["xke"] = "ヶ"
        mRtoJ["kya"] = "きゃ"
        mRtoJ["kyi"] = "きぃ"
        mRtoJ["kyu"] = "きゅ"
        mRtoJ["kye"] = "きぇ"
        mRtoJ["kyo"] = "きょ"
        mRtoJ["qya"] = "くゃ"
        mRtoJ["qyu"] = "くゅ"
        mRtoJ["qyo"] = "くょ"
        mRtoJ["qwa"] = "くぁ"
        mRtoJ["qwi"] = "くぃ"
        mRtoJ["qwu"] = "くぅ"
        mRtoJ["qwe"] = "くぇ"
        mRtoJ["qwo"] = "くぉ"
        mRtoJ["qa"] = "くぁ"
        mRtoJ["qi"] = "くぃ"
        mRtoJ["qe"] = "くぇ"
        mRtoJ["qo"] = "くぉ"
        mRtoJ["kwa"] = "くぁ"
        mRtoJ["qyi"] = "くぃ"
        mRtoJ["qye"] = "くぇ"
        mRtoJ["ga"] = "が"
        mRtoJ["gi"] = "ぎ"
        mRtoJ["gu"] = "ぐ"
        mRtoJ["ge"] = "げ"
        mRtoJ["go"] = "ご"
        mRtoJ["gya"] = "ぎゃ"
        mRtoJ["gyi"] = "ぎぃ"
        mRtoJ["gyu"] = "ぎゅ"
        mRtoJ["gye"] = "ぎぇ"
        mRtoJ["gyo"] = "ぎょ"
        mRtoJ["gwa"] = "ぐぁ"
        mRtoJ["gwi"] = "ぐぃ"
        mRtoJ["gwu"] = "ぐぅ"
        mRtoJ["gwe"] = "ぐぇ"
        mRtoJ["gwo"] = "ぐぉ"
        mRtoJ["sa"] = "さ"
        mRtoJ["si"] = "し"
        mRtoJ["shi"] = "し"
        mRtoJ["su"] = "す"
        mRtoJ["se"] = "せ"
        mRtoJ["so"] = "そ"
        mRtoJ["za"] = "ざ"
        mRtoJ["zi"] = "じ"
        mRtoJ["zu"] = "ず"
        mRtoJ["ze"] = "ぜ"
        mRtoJ["zo"] = "ぞ"
        mRtoJ["ji"] = "じ"
        mRtoJ["sya"] = "しゃ"
        mRtoJ["syi"] = "しぃ"
        mRtoJ["syu"] = "しゅ"
        mRtoJ["sye"] = "しぇ"
        mRtoJ["syo"] = "しょ"
        mRtoJ["sha"] = "しゃ"
        mRtoJ["shu"] = "しゅ"
        mRtoJ["she"] = "しぇ"
        mRtoJ["sho"] = "しょ"
        mRtoJ["swa"] = "すぁ"
        mRtoJ["swi"] = "すぃ"
        mRtoJ["swu"] = "すぅ"
        mRtoJ["swe"] = "すぇ"
        mRtoJ["swo"] = "すぉ"
        mRtoJ["zya"] = "じゃ"
        mRtoJ["zyi"] = "じぃ"
        mRtoJ["zyu"] = "じゅ"
        mRtoJ["zye"] = "じぇ"
        mRtoJ["zyo"] = "じょ"
        mRtoJ["ja"] = "じゃ"
        mRtoJ["ju"] = "じゅ"
        mRtoJ["je"] = "じぇ"
        mRtoJ["jo"] = "じょ"
        mRtoJ["jya"] = "じゃ"
        mRtoJ["jyi"] = "じぃ"
        mRtoJ["jyu"] = "じゅ"
        mRtoJ["jye"] = "じぇ"
        mRtoJ["jyo"] = "じょ"
        mRtoJ["ta"] = "た"
        mRtoJ["ti"] = "ち"
        mRtoJ["tu"] = "つ"
        mRtoJ["te"] = "て"
        mRtoJ["to"] = "と"
        mRtoJ["chi"] = "ち"
        mRtoJ["tsu"] = "つ"
        mRtoJ["ltu"] = "っ"
        mRtoJ["xtu"] = "っ"
        mRtoJ["tya"] = "ちゃ"
        mRtoJ["tyi"] = "ちぃ"
        mRtoJ["tyu"] = "ちゅ"
        mRtoJ["tye"] = "ちぇ"
        mRtoJ["tyo"] = "ちょ"
        mRtoJ["cha"] = "ちゃ"
        mRtoJ["chu"] = "ちゅ"
        mRtoJ["che"] = "ちぇ"
        mRtoJ["cho"] = "ちょ"
        mRtoJ["cya"] = "ちゃ"
        mRtoJ["cyi"] = "ちぃ"
        mRtoJ["cyu"] = "ちゅ"
        mRtoJ["cye"] = "ちぇ"
        mRtoJ["cyo"] = "ちょ"
        mRtoJ["tsa"] = "つぁ"
        mRtoJ["tsi"] = "つぃ"
        mRtoJ["tse"] = "つぇ"
        mRtoJ["tso"] = "つぉ"
        mRtoJ["tha"] = "てゃ"
        mRtoJ["thi"] = "てぃ"
        mRtoJ["thu"] = "てゅ"
        mRtoJ["the"] = "てぇ"
        mRtoJ["tho"] = "てょ"
        mRtoJ["twa"] = "とぁ"
        mRtoJ["twi"] = "とぃ"
        mRtoJ["twu"] = "とぅ"
        mRtoJ["twe"] = "とぇ"
        mRtoJ["two"] = "とぉ"
        mRtoJ["da"] = "だ"
        mRtoJ["di"] = "ぢ"
        mRtoJ["du"] = "づ"
        mRtoJ["de"] = "で"
        mRtoJ["do"] = "ど"
        mRtoJ["dya"] = "ぢゃ"
        mRtoJ["dyi"] = "ぢぃ"
        mRtoJ["dyu"] = "ぢゅ"
        mRtoJ["dye"] = "ぢぇ"
        mRtoJ["dyo"] = "ぢょ"
        mRtoJ["dha"] = "でゃ"
        mRtoJ["dhi"] = "でぃ"
        mRtoJ["dhu"] = "でゅ"
        mRtoJ["dhe"] = "でぇ"
        mRtoJ["dho"] = "でょ"
        mRtoJ["dwa"] = "どぁ"
        mRtoJ["dwi"] = "どぃ"
        mRtoJ["dwu"] = "どぅ"
        mRtoJ["dwe"] = "どぇ"
        mRtoJ["dwo"] = "どぉ"
        mRtoJ["na"] = "な"
        mRtoJ["ni"] = "に"
        mRtoJ["nu"] = "ぬ"
        mRtoJ["ne"] = "ね"
        mRtoJ["no"] = "の"
        mRtoJ["nya"] = "にゃ"
        mRtoJ["nyi"] = "にぃ"
        mRtoJ["nyu"] = "にゅ"
        mRtoJ["nye"] = "にぇ"
        mRtoJ["nyo"] = "にょ"
        mRtoJ["ha"] = "は"
        mRtoJ["hi"] = "ひ"
        mRtoJ["hu"] = "ふ"
        mRtoJ["he"] = "へ"
        mRtoJ["ho"] = "ほ"
        mRtoJ["fu"] = "ふ"
        mRtoJ["hya"] = "ひゃ"
        mRtoJ["hyi"] = "ひぃ"
        mRtoJ["hyu"] = "ひゅ"
        mRtoJ["hye"] = "ひぇ"
        mRtoJ["hyo"] = "ひょ"
        mRtoJ["fya"] = "ふゃ"
        mRtoJ["fyu"] = "ふゅ"
        mRtoJ["fyo"] = "ふょ"
        mRtoJ["fwa"] = "ふぁ"
        mRtoJ["fwi"] = "ふぃ"
        mRtoJ["fwu"] = "ふぅ"
        mRtoJ["fwe"] = "ふぇ"
        mRtoJ["fwo"] = "ふぉ"
        mRtoJ["fa"] = "ふぁ"
        mRtoJ["fi"] = "ふぃ"
        mRtoJ["fe"] = "ふぇ"
        mRtoJ["fo"] = "ふぉ"
        mRtoJ["fyi"] = "ふぃ"
        mRtoJ["fye"] = "ふぇ"
        mRtoJ["ba"] = "ば"
        mRtoJ["bi"] = "び"
        mRtoJ["bu"] = "ぶ"
        mRtoJ["be"] = "べ"
        mRtoJ["bo"] = "ぼ"
        mRtoJ["bya"] = "びゃ"
        mRtoJ["byi"] = "びぃ"
        mRtoJ["byu"] = "びゅ"
        mRtoJ["bye"] = "びぇ"
        mRtoJ["byo"] = "びょ"
        mRtoJ["pa"] = "ぱ"
        mRtoJ["pi"] = "ぴ"
        mRtoJ["pu"] = "ぷ"
        mRtoJ["pe"] = "ぺ"
        mRtoJ["po"] = "ぽ"
        mRtoJ["pya"] = "ぴゃ"
        mRtoJ["pyi"] = "ぴぃ"
        mRtoJ["pyu"] = "ぴゅ"
        mRtoJ["pye"] = "ぴぇ"
        mRtoJ["pyo"] = "ぴょ"
        mRtoJ["ma"] = "ま"
        mRtoJ["mi"] = "み"
        mRtoJ["mu"] = "む"
        mRtoJ["me"] = "め"
        mRtoJ["mo"] = "も"
        mRtoJ["mya"] = "みゃ"
        mRtoJ["myi"] = "みぃ"
        mRtoJ["myu"] = "みゅ"
        mRtoJ["mye"] = "みぇ"
        mRtoJ["myo"] = "みょ"
        mRtoJ["ya"] = "や"
        mRtoJ["yu"] = "ゆ"
        mRtoJ["yo"] = "よ"
        mRtoJ["xya"] = "ゃ"
        mRtoJ["xyu"] = "ゅ"
        mRtoJ["xyo"] = "ょ"
        mRtoJ["ra"] = "ら"
        mRtoJ["ri"] = "り"
        mRtoJ["ru"] = "る"
        mRtoJ["re"] = "れ"
        mRtoJ["ro"] = "ろ"
        mRtoJ["rya"] = "りゃ"
        mRtoJ["ryi"] = "りぃ"
        mRtoJ["ryu"] = "りゅ"
        mRtoJ["rye"] = "りぇ"
        mRtoJ["ryo"] = "りょ"
        mRtoJ["la"] = "ら"
        mRtoJ["li"] = "り"
        mRtoJ["lu"] = "る"
        mRtoJ["le"] = "れ"
        mRtoJ["lo"] = "ろ"
        mRtoJ["lya"] = "りゃ"
        mRtoJ["lyi"] = "りぃ"
        mRtoJ["lyu"] = "りゅ"
        mRtoJ["lye"] = "りぇ"
        mRtoJ["lyo"] = "りょ"
        mRtoJ["wa"] = "わ"
        mRtoJ["wo"] = "を"
        mRtoJ["lwe"] = "ゎ"
        mRtoJ["xwa"] = "ゎ"
        mRtoJ["nn"] = "ん"
        mRtoJ["'n '"] = "ん"
        mRtoJ["xn"] = "ん"
        mRtoJ["ltsu"] = "っ"
        mRtoJ["xtsu"] = "っ"
    }

    private fun prepareJtoR() {
        mJtoR["あ"] = "a"
        mJtoR["い"] = "i"
        mJtoR["う"] = "u"
        mJtoR["え"] = "e"
        mJtoR["お"] = "o"
        mJtoR["ゔぁ"] = "va"
        mJtoR["ゔぃ"] = "vi"
        mJtoR["ゔ"] = "vu"
        mJtoR["ゔぇ"] = "ve"
        mJtoR["ゔぉ"] = "vo"
        mJtoR["か"] = "ka"
        mJtoR["き"] = "ki"
        mJtoR["きゃ"] = "kya"
        mJtoR["きぃ"] = "kyi"
        mJtoR["きゅ"] = "kyu"
        mJtoR["く"] = "ku"
        mJtoR["け"] = "ke"
        mJtoR["こ"] = "ko"
        mJtoR["が"] = "ga"
        mJtoR["ぎ"] = "gi"
        mJtoR["ぐ"] = "gu"
        mJtoR["げ"] = "ge"
        mJtoR["ご"] = "go"
        mJtoR["ぎゃ"] = "gya"
        mJtoR["ぎぃ"] = "gyi"
        mJtoR["ぎゅ"] = "gyu"
        mJtoR["ぎぇ"] = "gye"
        mJtoR["ぎょ"] = "gyo"
        mJtoR["さ"] = "sa"
        mJtoR["す"] = "su"
        mJtoR["せ"] = "se"
        mJtoR["そ"] = "so"
        mJtoR["ざ"] = "za"
        mJtoR["ず"] = "zu"
        mJtoR["ぜ"] = "ze"
        mJtoR["ぞ"] = "zo"
        mJtoR["し"] = "shi"
        mJtoR["しゃ"] = "sha"
        mJtoR["しゅ"] = "shu"
        mJtoR["しょ"] = "sho"
        mJtoR["じ"] = "ji"
        mJtoR["じゃ"] = "ja"
        mJtoR["じゅ"] = "ju"
        mJtoR["じょ"] = "jo"
        mJtoR["た"] = "ta"
        mJtoR["ち"] = "chi"
        mJtoR["ちゃ"] = "cha"
        mJtoR["ちゅ"] = "chu"
        mJtoR["ちょ"] = "cho"
        mJtoR["つ"] = "tsu"
        mJtoR["て"] = "te"
        mJtoR["と"] = "to"
        mJtoR["だ"] = "da"
        mJtoR["ぢ"] = "di"
        mJtoR["づ"] = "du"
        mJtoR["で"] = "de"
        mJtoR["ど"] = "do"
        mJtoR["な"] = "na"
        mJtoR["に"] = "ni"
        mJtoR["にゃ"] = "nya"
        mJtoR["にゅ"] = "nyu"
        mJtoR["にょ"] = "nyo"
        mJtoR["ぬ"] = "nu"
        mJtoR["ね"] = "ne"
        mJtoR["の"] = "no"
        mJtoR["は"] = "ha"
        mJtoR["ひ"] = "hi"
        mJtoR["ふ"] = "fu"
        mJtoR["へ"] = "he"
        mJtoR["ほ"] = "ho"
        mJtoR["ひゃ"] = "hya"
        mJtoR["ひゅ"] = "hyu"
        mJtoR["ひょ"] = "hyo"
        mJtoR["ふぁ"] = "fa"
        mJtoR["ふぃ"] = "fi"
        mJtoR["ふぇ"] = "fe"
        mJtoR["ふぉ"] = "fo"
        mJtoR["ば"] = "ba"
        mJtoR["び"] = "bi"
        mJtoR["ぶ"] = "bu"
        mJtoR["べ"] = "be"
        mJtoR["ぼ"] = "bo"
        mJtoR["びゃ"] = "bya"
        mJtoR["びゅ"] = "byu"
        mJtoR["びょ"] = "byo"
        mJtoR["ぱ"] = "pa"
        mJtoR["ぴ"] = "pi"
        mJtoR["ぷ"] = "pu"
        mJtoR["ぺ"] = "pe"
        mJtoR["ぽ"] = "po"
        mJtoR["ぴゃ"] = "pya"
        mJtoR["ぴゅ"] = "pyu"
        mJtoR["ぴょ"] = "pyo"
        mJtoR["ま"] = "ma"
        mJtoR["み"] = "mi"
        mJtoR["む"] = "mu"
        mJtoR["め"] = "me"
        mJtoR["も"] = "mo"
        mJtoR["みゃ"] = "mya"
        mJtoR["みゅ"] = "myu"
        mJtoR["みょ"] = "myo"
        mJtoR["や"] = "ya"
        mJtoR["ゆ"] = "yu"
        mJtoR["よ"] = "yo"
        mJtoR["ら"] = "ra"
        mJtoR["り"] = "ri"
        mJtoR["る"] = "ru"
        mJtoR["れ"] = "re"
        mJtoR["ろ"] = "ro"
        mJtoR["りゃ"] = "rya"
        mJtoR["りゅ"] = "ryu"
        mJtoR["りょ"] = "ryo"
        mJtoR["わ"] = "wa"
        mJtoR["を"] = "wo"
        mJtoR["ん"] = "n"
        mJtoR["ゐ"] = "wi"
        mJtoR["ゑ"] = "we"
        mJtoR["きぇ"] = "kye"
        mJtoR["きょ"] = "kyo"
        mJtoR["じぃ"] = "jyi"
        mJtoR["じぇ"] = "jye"
        mJtoR["ちぃ"] = "cyi"
        mJtoR["ちぇ"] = "che"
        mJtoR["ひぃ"] = "hyi"
        mJtoR["ひぇ"] = "hye"
        mJtoR["びぃ"] = "byi"
        mJtoR["びぇ"] = "bye"
        mJtoR["ぴぃ"] = "pyi"
        mJtoR["ぴぇ"] = "pye"
        mJtoR["みぇ"] = "mye"
        mJtoR["みぃ"] = "myi"
        mJtoR["りぃ"] = "ryi"
        mJtoR["りぇ"] = "rye"
        mJtoR["にぃ"] = "nyi"
        mJtoR["にぇ"] = "nye"
        mJtoR["しぃ"] = "syi"
        mJtoR["しぇ"] = "she"
        mJtoR["いぇ"] = "ye"
        mJtoR["うぁ"] = "wha"
        mJtoR["うぉ"] = "who"
        mJtoR["うぃ"] = "wi"
        mJtoR["うぇ"] = "we"
        mJtoR["ゔゃ"] = "vya"
        mJtoR["ゔゅ"] = "vyu"
        mJtoR["ゔょ"] = "vyo"
        mJtoR["すぁ"] = "swa"
        mJtoR["すぃ"] = "swi"
        mJtoR["すぅ"] = "swu"
        mJtoR["すぇ"] = "swe"
        mJtoR["すぉ"] = "swo"
        mJtoR["くゃ"] = "qya"
        mJtoR["くゅ"] = "qyu"
        mJtoR["くょ"] = "qyo"
        mJtoR["くぁ"] = "qwa"
        mJtoR["くぃ"] = "qwi"
        mJtoR["くぅ"] = "qwu"
        mJtoR["くぇ"] = "qwe"
        mJtoR["くぉ"] = "qwo"
        mJtoR["ぐぁ"] = "gwa"
        mJtoR["ぐぃ"] = "gwi"
        mJtoR["ぐぅ"] = "gwu"
        mJtoR["ぐぇ"] = "gwe"
        mJtoR["ぐぉ"] = "gwo"
        mJtoR["つぁ"] = "tsa"
        mJtoR["つぃ"] = "tsi"
        mJtoR["つぇ"] = "tse"
        mJtoR["つぉ"] = "tso"
        mJtoR["てゃ"] = "tha"
        mJtoR["てぃ"] = "thi"
        mJtoR["てゅ"] = "thu"
        mJtoR["てぇ"] = "the"
        mJtoR["てょ"] = "tho"
        mJtoR["とぁ"] = "twa"
        mJtoR["とぃ"] = "twi"
        mJtoR["とぅ"] = "twu"
        mJtoR["とぇ"] = "twe"
        mJtoR["とぉ"] = "two"
        mJtoR["ぢゃ"] = "dya"
        mJtoR["ぢぃ"] = "dyi"
        mJtoR["ぢゅ"] = "dyu"
        mJtoR["ぢぇ"] = "dye"
        mJtoR["ぢょ"] = "dyo"
        mJtoR["でゃ"] = "dha"
        mJtoR["でぃ"] = "dhi"
        mJtoR["でゅ"] = "dhu"
        mJtoR["でぇ"] = "dhe"
        mJtoR["でょ"] = "dho"
        mJtoR["どぁ"] = "dwa"
        mJtoR["どぃ"] = "dwi"
        mJtoR["どぅ"] = "dwu"
        mJtoR["どぇ"] = "dwe"
        mJtoR["どぉ"] = "dwo"
        mJtoR["ふぅ"] = "fwu"
        mJtoR["ふゃ"] = "fya"
        mJtoR["ふゅ"] = "fyu"
        mJtoR["ふょ"] = "fyo"
        mJtoR["ぁ"] = "a"
        mJtoR["ぃ"] = "i"
        mJtoR["ぇ"] = "e"
        mJtoR["ぅ"] = "u"
        mJtoR["ぉ"] = "o"
        mJtoR["ゃ"] = "ya"
        mJtoR["ゅ"] = "yu"
        mJtoR["ょ"] = "yo"
        mJtoR["っ"] = ""
        mJtoR["ゕ"] = "ka"
        mJtoR["ゖ"] = "ka"
        mJtoR["ゎ"] = "wa"
        mJtoR["'　'"] = " "
        mJtoR["んあ"] = "n'a"
        mJtoR["んい"] = "n'i"
        mJtoR["んう"] = "n'u"
        mJtoR["んえ"] = "n'e"
        mJtoR["んお"] = "n'o"
        mJtoR["んや"] = "n'ya"
        mJtoR["んゆ"] = "n'yu"
        mJtoR["んよ"] = "n'yo"
    }

    //static final int LOWERCASE_START = 0x61;
//static final int LOWERCASE_END = 0x7A;
    const val UPPERCASE_START = 0x41
    const val UPPERCASE_END = 0x5A
    const val HIRAGANA_START = 0x3041
    const val HIRAGANA_END = 0x3096
    const val KATAKANA_START = 0x30A1
    const val KATAKANA_END = 0x30FA
    const val OPTION_USE_OBSOLETE_KANA = "useObsoleteKana"
    const val OPTION_IME_MODE = "IMEMode"


    init {
        mOptions[OPTION_USE_OBSOLETE_KANA] = useObsoleteKana
        mOptions[OPTION_IME_MODE] = false
        prepareRtoJ()
        prepareJtoR()
    }
}