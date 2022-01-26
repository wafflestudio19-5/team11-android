package com.example.toyproject.ui.main.tableFragment

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.edit
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableFilterMajorBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import kotlin.NullPointerException

@AndroidEntryPoint
class TableAddFilterMajorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTableFilterMajorBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    private var items : HashMap<String, ArrayList<TableAddFilterItemView>> = hashMapOf()

    private var folderLevel : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전환 이펙트 : 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding = ActivityTableFilterMajorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val views = arrayListOf(binding.folderLevel0Text, binding.folderLevel1Text,
            binding.folderLevel2Text, binding.folderLevel3Text)


        items["전체"] = arrayListOf(TableAddFilterItemView(this, "개설학과"),
            TableAddFilterItemView(this, "교양영역"))

        items["개설학과"] = arrayListOf(
            TableAddFilterItemView(this, "인문대학"),
            TableAddFilterItemView(this, "사회과학대학"),
            TableAddFilterItemView(this, "자연과학대학"),
            TableAddFilterItemView(this, "간호대학"),
            TableAddFilterItemView(this, "경영대학"),
            TableAddFilterItemView(this, "공과대학"),
            TableAddFilterItemView(this, "농업생명과학대학"),
            TableAddFilterItemView(this, "미술대학"),
            TableAddFilterItemView(this, "법과대학"),
            TableAddFilterItemView(this, "사범대학"),
            TableAddFilterItemView(this, "생활과학대학"),
            TableAddFilterItemView(this, "수의과대학"),
            TableAddFilterItemView(this, "약학대학"),
            TableAddFilterItemView(this, "음악대학"),
            TableAddFilterItemView(this, "의과대학"),
            TableAddFilterItemView(this, "자유전공학부"),
            TableAddFilterItemView(this, "치과대학"),
            TableAddFilterItemView(this, "보건대학원"),
            TableAddFilterItemView(this, "행정대학원"),
            TableAddFilterItemView(this, "환경대학원"),
            TableAddFilterItemView(this, "국제대학원"),
            TableAddFilterItemView(this, "치의학대학원"),
            TableAddFilterItemView(this, "의학대학원"),
            TableAddFilterItemView(this, "법학전문대학원"),
            TableAddFilterItemView(this, "융합과학기술대학원"),
            TableAddFilterItemView(this, "기초교육원"),
            TableAddFilterItemView(this, "국제농업기술대학원"),
            TableAddFilterItemView(this, "공학전문대학원"),
            TableAddFilterItemView(this, "데이터사이언스대학원"),
            TableAddFilterItemView(this, "학군단"),
            TableAddFilterItemView(this, "대학원"),
        )

        items["인문대학"] = arrayListOf(
            TableAddFilterItemView(this, "인문대학", next=false),
            TableAddFilterItemView(this, "국어국문학과"),
            TableAddFilterItemView(this, "중어중문학과", next=false),
            TableAddFilterItemView(this, "영어영문학과", next=false),
            TableAddFilterItemView(this, "불어불문학과", next=false),
            TableAddFilterItemView(this, "독어독문학과", next=false),
            TableAddFilterItemView(this, "노어노문학과"),
            TableAddFilterItemView(this, "서어서문학과"),
            TableAddFilterItemView(this, "언어학과"),
            TableAddFilterItemView(this, "국사학과"),
            TableAddFilterItemView(this, "동양사학과", next=false),
            TableAddFilterItemView(this, "서양사학과", next=false),
            TableAddFilterItemView(this, "고고미술사학과"),
            TableAddFilterItemView(this, "철학과"),
            TableAddFilterItemView(this, "종교학과", next=false),
            TableAddFilterItemView(this, "미학과", next=false),
            TableAddFilterItemView(this, "협동과정 공연예술학전공", next=false),
            TableAddFilterItemView(this, "협동과정 기록학전공", next=false),
            TableAddFilterItemView(this, "아시아언어문명학부"))
        items["사회과학대학"] = arrayListOf(
            TableAddFilterItemView(this, "정치외교학부"),
            TableAddFilterItemView(this, "경제학부", next=false),
            TableAddFilterItemView(this, "사회학과", next=false),
            TableAddFilterItemView(this, "인류학과"),
            TableAddFilterItemView(this, "지리학과"),
            TableAddFilterItemView(this, "사회복지학과", next=false),
            TableAddFilterItemView(this, "언론정보학과"),
            TableAddFilterItemView(this, "심리학과"),
            TableAddFilterItemView(this, "협동과정 평화∙통일학전공", next=false)
        )
        items["자연과학대학"] = arrayListOf(
            TableAddFilterItemView(this, "자연과학대학", next=false),
            TableAddFilterItemView(this, "수리과학부", next=false),
            TableAddFilterItemView(this, "통계학과", next=false),
            TableAddFilterItemView(this, "물리∙천문학부"),
            TableAddFilterItemView(this, "화학부", next=false),
            TableAddFilterItemView(this, "생명과학부"),
            TableAddFilterItemView(this, "지구환경과학부"),
            TableAddFilterItemView(this, "뇌인지학과", next=false),
            TableAddFilterItemView(this, "협동과정 과학사 및 과학철학전공", next=false),
            TableAddFilterItemView(this, "협동과정 유전과학전공", next=false),
            TableAddFilterItemView(this, "협동과정 생물정보학전공", next=false),
            TableAddFilterItemView(this, "협동과정 계산과학학전공", next=false),
            TableAddFilterItemView(this, "협동과정 생물물리 및 화학생물학전공", next=false)
        )
        items["간호대학"] = arrayListOf(
            TableAddFilterItemView(this, "간호대학", next=false),
            TableAddFilterItemView(this, "간호학과", next=false),
            TableAddFilterItemView(this, "임상간호학과", next=false),
        )
        items["경영대학"] = arrayListOf(
            TableAddFilterItemView(this, "경영대학", next=false),
            TableAddFilterItemView(this, "경영학과")
        )
        items["공과대학"] = arrayListOf(
            TableAddFilterItemView(this, "공과대학", next=false),
            TableAddFilterItemView(this, "재료공학부", next=false),
            TableAddFilterItemView(this, "전기∙정보공학부"),
            TableAddFilterItemView(this, "에너지자원공학과", next=false),
            TableAddFilterItemView(this, "화학생물공학부"),
            TableAddFilterItemView(this, "건축학과", next=false),
            TableAddFilterItemView(this, "산업공학과"),
            TableAddFilterItemView(this, "원자핵공학과", next=false),
            TableAddFilterItemView(this, "조선해양공학과", next=false),
            TableAddFilterItemView(this, "협동과정 기술경영∙경제∙정책전공"),
            TableAddFilterItemView(this, "협동과정 도시설계학전공", next=false),
            TableAddFilterItemView(this, "협동과정 바이오엔지니어링전공", next=false),
            TableAddFilterItemView(this, "컴퓨터공학부"),
            TableAddFilterItemView(this, "협동과정 우주시스템전공"),
            TableAddFilterItemView(this, "협동과정 인공지능전공", next=false),
            TableAddFilterItemView(this, "건설환경공학부"),
            TableAddFilterItemView(this, "에너지시스템공학부"),
            TableAddFilterItemView(this, "항공우주공학과", next=false),
            TableAddFilterItemView(this, "기계공학부")
        )
        items["농업생명과학대학"] = arrayListOf(
            TableAddFilterItemView(this, "농업생명과학대학", next=false),
            TableAddFilterItemView(this, "식물생산과학부"),
            TableAddFilterItemView(this, "산림과학부"),
            TableAddFilterItemView(this, "농림생물자원학부"),
            TableAddFilterItemView(this, "응용생물화학부"),
            TableAddFilterItemView(this, "식품∙동물생명공학부"),
            TableAddFilterItemView(this, "바이오시스템∙소재학부"),
            TableAddFilterItemView(this, "바이오시스템∙공학과"),
            TableAddFilterItemView(this, "조경∙지역시스템공학부"),
            TableAddFilterItemView(this, "농경제사회학부"),
            TableAddFilterItemView(this, "농생명공학부"),
            TableAddFilterItemView(this, "농산업교육과", next=false),
            TableAddFilterItemView(this, "생태조경∙지역시스템공학부"),
            TableAddFilterItemView(this, "협동과정 농림기상학"),
            TableAddFilterItemView(this, "협동과정 농생명유전체학전공", next=false)
        )
        items["미술대학"] = arrayListOf(
            TableAddFilterItemView(this, "미술대학", next=false),
            TableAddFilterItemView(this, "동양화과", next=false),
            TableAddFilterItemView(this, "서양화과", next=false),
            TableAddFilterItemView(this, "조소과"),
            TableAddFilterItemView(this, "디자인학부"),
            TableAddFilterItemView(this, "협동과정 미술경영", next=false),
            TableAddFilterItemView(this, "미술학과"),
            TableAddFilterItemView(this, "공예괴", next=false),
            TableAddFilterItemView(this, "디자인과", next=false)
        )
        items["법과대학"] = arrayListOf(
            TableAddFilterItemView(this, "법학과", next=false),
            TableAddFilterItemView(this, "법학대학", next=false)
        )
        items["사범대학"] = arrayListOf(
            TableAddFilterItemView(this, "사범대학", next=false),
            TableAddFilterItemView(this, "교육학과", next=false),
            TableAddFilterItemView(this, "국어교육과", next=false),
            TableAddFilterItemView(this, "국어교육과", next=false),
            TableAddFilterItemView(this, "영어교육과", next=false),
            TableAddFilterItemView(this, "불어교육과", next=false),
            TableAddFilterItemView(this, "독어교육과", next=false),
            TableAddFilterItemView(this, "사회교육과"),
            TableAddFilterItemView(this, "역사교육과", next=false),
            TableAddFilterItemView(this, "지리교육과", next=false),
            TableAddFilterItemView(this, "윤리교육과", next=false),
            TableAddFilterItemView(this, "수학교육과", next=false),
            TableAddFilterItemView(this, "물리교육과"),
            TableAddFilterItemView(this, "화학교육과"),
            TableAddFilterItemView(this, "생물교육과", next=false),
            TableAddFilterItemView(this, "지구과학교육과", next=false),
            TableAddFilterItemView(this, "체육교육과", next=false),
            TableAddFilterItemView(this, "협동과정 음악교육전공", next=false),
            TableAddFilterItemView(this, "협동과정 미술교육전공", next=false),
            TableAddFilterItemView(this, "협동과정 가정교육전공", next=false),
            TableAddFilterItemView(this, "협동과정 특수교육전공", next=false),
            TableAddFilterItemView(this, "협동과정 환경교육전공", next=false),
            TableAddFilterItemView(this, "협동과정 육아교육전공", next=false),
            TableAddFilterItemView(this, "협동과정 글로벌교육협력전공", next=false),
            TableAddFilterItemView(this, "외국어교육과"),
            TableAddFilterItemView(this, "과학교육과"),
            TableAddFilterItemView(this, "과학교육계", next=false)
        )
        items["생활과학대학"] = arrayListOf(
            TableAddFilterItemView(this, "생활과학대학", next=false),
            TableAddFilterItemView(this, "소비자아동학부"),
            TableAddFilterItemView(this, "식품영양학과", next=false),
            TableAddFilterItemView(this, "의류학과"),
            TableAddFilterItemView(this, "소비자학과", next=false),
            TableAddFilterItemView(this, "아동가족학과", next=false),
        )
        items["수의과대학"] = arrayListOf(
            TableAddFilterItemView(this, "수의과대학", next=false),
            TableAddFilterItemView(this, "수의학과", next=false),
            TableAddFilterItemView(this, "수의예과", next=false),
        )
        items["약학대학"] = arrayListOf(
            TableAddFilterItemView(this, "약학대학", next=false),
            TableAddFilterItemView(this, "약학과", next=false)
        )
        items["음악대학"] = arrayListOf(
            TableAddFilterItemView(this, "음악대학", next=false),
            TableAddFilterItemView(this, "성학과", next=false),
            TableAddFilterItemView(this, "작곡과"),
            TableAddFilterItemView(this, "기악과"),
            TableAddFilterItemView(this, "음악과"),
            TableAddFilterItemView(this, "국악과"),
        )
        items["의과대학"] = arrayListOf(
            TableAddFilterItemView(this, "의예과", next=false),
            TableAddFilterItemView(this, "의학과"),
            TableAddFilterItemView(this, "의과학과", next=false),
            TableAddFilterItemView(this, "휴먼시스템의학과", next=false),
            TableAddFilterItemView(this, "임상의과학과", next=false),
            TableAddFilterItemView(this, "의료기기산업학과", next=false),
            TableAddFilterItemView(this, "협동과정 종양생물학전공", next=false),
            TableAddFilterItemView(this, "협동과정 임상약리학전공", next=false),
            TableAddFilterItemView(this, "협동과정 의료정보학전공", next=false),
            TableAddFilterItemView(this, "협동과정 줄기세포생물학전공", next=false)
        )
        items["자유전공학부"] = arrayListOf(TableAddFilterItemView(this, "자유전공학부", next=false))
        items["치과대학"] = arrayListOf(TableAddFilterItemView(this, "치의과학과", next=false))
        items["보건대학원"] = arrayListOf(
            TableAddFilterItemView(this, "보건대학원", next=false),
            TableAddFilterItemView(this, "보건학과"),
            TableAddFilterItemView(this, "환경보건학과")
        )
        items["행정대학원"] = arrayListOf(
            TableAddFilterItemView(this, "행정대학원", next=false),
            TableAddFilterItemView(this, "행정학과"),
            TableAddFilterItemView(this, "공기업정책학과", next=false),
        )
        items["환경대학원"] = arrayListOf(
            TableAddFilterItemView(this, "환경대학원", next=false),
            TableAddFilterItemView(this, "환경계획학과"),
            TableAddFilterItemView(this, "환경조경학과"),
            TableAddFilterItemView(this, "도시및지역계획학과")
        )
        items["국제대학원"] = arrayListOf(TableAddFilterItemView(this, "국제학과"))
        items["치의학대학원"] = arrayListOf(
            TableAddFilterItemView(this, "치의학대학원", next=false),
            TableAddFilterItemView(this, "치의학과"),
        )
        items["의학대학원"] = arrayListOf(TableAddFilterItemView(this, "의학과", next=false))
        items["법학전문대학원"] = arrayListOf(
            TableAddFilterItemView(this, "법학전문대학원", next=false),
            TableAddFilterItemView(this, "법학과", next=false)
        )
        items["융합과학기술대학원"] = arrayListOf(
            TableAddFilterItemView(this, "융합과학기술대학원", next=false),
            TableAddFilterItemView(this, "분자의학 및 바이오제약학과", next=false),
            TableAddFilterItemView(this, "수리정보과학과", next=false),
            TableAddFilterItemView(this, "응용바이오공학과", next=false),
            TableAddFilterItemView(this, "지능정보융합학과", next=false),
            TableAddFilterItemView(this, "헬스케어융합학과", next=false)
        )
        items["기초교육원"] = arrayListOf(TableAddFilterItemView(this, "기초교육원", next=false))
        items["국제농업기술대학원"] = arrayListOf(TableAddFilterItemView(this, "국제농업기술학과", next=false))
        items["공학전문대학원"] = arrayListOf(TableAddFilterItemView(this, "응용공학과", next=false))
        items["데이터사이언스대학원"] = arrayListOf(TableAddFilterItemView(this, "데이터사이언스학과", next=false))
        items["학군단"] = arrayListOf(TableAddFilterItemView(this, "학군단", next=false))
        items["대학원"] = arrayListOf(TableAddFilterItemView(this, "대학원", next=false))

        items["국어국문학과"] = arrayListOf(
            TableAddFilterItemView(this, "연계전공 고전문헌학", next=false),
            TableAddFilterItemView(this, "국어국문학과", next=false),
        )
        items["노어노문학과"] = arrayListOf(
            TableAddFilterItemView(this, "노어국문학과", next=false),
            TableAddFilterItemView(this, "연계전공 러시아학", next=false),
        )
        items["서어서문학과"] = arrayListOf(
            TableAddFilterItemView(this, "서어국문학과", next=false),
            TableAddFilterItemView(this, "연계전공 라틴아메리카학", next=false),
        )
        items["언어학과"] = arrayListOf(
            TableAddFilterItemView(this, "언어학과", next=false),
            TableAddFilterItemView(this, "연계전공 인문데이터과학", next=false),
        )
        items["국사학과"] = arrayListOf(
            TableAddFilterItemView(this, "국사학과", next=false),
            TableAddFilterItemView(this, "국사학전공", next=false),
            TableAddFilterItemView(this, "연계전공 동아시아비교인문학", next=false),
        )
        items["고고미술사학과"] = arrayListOf(
            TableAddFilterItemView(this, "고고학전공", next=false),
            TableAddFilterItemView(this, "미술사학전공", next=false),
        )
        items["철학과"] = arrayListOf(
            TableAddFilterItemView(this, "철학과", next=false),
            TableAddFilterItemView(this, "동양철학전공", next=false),
            TableAddFilterItemView(this, "서양철학전공", next=false),
            TableAddFilterItemView(this, "연계전공 정치-경제-철학", next=false),
        )
        items["아시아언어문명학부"] = arrayListOf(
            TableAddFilterItemView(this, "아시아언어문명학부", next=false),
            TableAddFilterItemView(this, "서아시아언어문명전공", next=false),
        )
        items["정치외교학부"] = arrayListOf(
            TableAddFilterItemView(this, "정치외교학부", next=false),
            TableAddFilterItemView(this, "외교학전공", next=false),
            TableAddFilterItemView(this, "정치학전공", next=false),
        )
        items["인류학과"] = arrayListOf(
            TableAddFilterItemView(this, "인류학과", next=false),
            TableAddFilterItemView(this, "연계전공 일본학", next=false)
        )
        items["지리학과"] = arrayListOf(
            TableAddFilterItemView(this, "지리학과", next=false),
            TableAddFilterItemView(this, "지리학전공", next=false),
        )
        items["언론정보학과"] = arrayListOf(
            TableAddFilterItemView(this, "언론정보학과", next=false),
            TableAddFilterItemView(this, "연합전공 정보문화학", next=false),
        )
        items["심리학과"] = arrayListOf(
            TableAddFilterItemView(this, "심리학과", next=false),
            TableAddFilterItemView(this, "심리학전공", next=false),
        )
        items["물리∙천문학부"] = arrayListOf(
            TableAddFilterItemView(this, "물리학전공", next=false),
            TableAddFilterItemView(this, "천문학전공", next=false),
        )
        items["생명과학부"] = arrayListOf(
            TableAddFilterItemView(this, "생명과학부", next=false),
            TableAddFilterItemView(this, "연계전공 과학기술학", next=false),
        )
        items["지구환경과학부"] = arrayListOf(
            TableAddFilterItemView(this, "지구환경과학부", next=false),
            TableAddFilterItemView(this, "연합전공 계산과학", next=false),
        )
        items["경영학과"] = arrayListOf(
            TableAddFilterItemView(this, "경영학과", next=false),
            TableAddFilterItemView(this, "연합전공 벤처경영학", next=false),
        )
        items["전기∙정보공학부"] = arrayListOf(
            TableAddFilterItemView(this, "전기∙정보공학부", next=false),
            TableAddFilterItemView(this, "연합전공 인공지능반도체공학", next=false),
        )
        items["화학생물공학부"] = arrayListOf(
            TableAddFilterItemView(this, "화학생물공학부", next=false),
            TableAddFilterItemView(this, "에너지환경화학융합기술전공", next=false),
        )
        items["산업공학과"] = arrayListOf(
            TableAddFilterItemView(this, "산업공학과", next=false),
            TableAddFilterItemView(this, "연합전공 기술경영", next=false),
        )
        items["협동과정 기술경영∙경제∙정책전공"] = arrayListOf(
            TableAddFilterItemView(this, "협동과정 기술경영∙경제∙정책전공", next=false),
            TableAddFilterItemView(this, "융합전공 스마트시티 글로벌 융합", next=false),
        )
        items["컴퓨터공학부"] = arrayListOf(
            TableAddFilterItemView(this, "컴퓨터공학부", next=false),
            TableAddFilterItemView(this, "연합전공 인공지능", next=false),
        )
        items["협동과정 우주시스템전공"] = arrayListOf(
            TableAddFilterItemView(this, "협동과정 우주시스템전공", next=false),
            TableAddFilterItemView(this, "우주시스템전공", next=false),
        )
        items["건설환경공학부"] = arrayListOf(
            TableAddFilterItemView(this, "건설환경공학부", next=false),
            TableAddFilterItemView(this, "건설환경공학전공", next=false),
            TableAddFilterItemView(this, "스마트도시공학전공", next=false),
        )
        items["에너지시스템공학부"] = arrayListOf(
            TableAddFilterItemView(this, "에너지자원공학전공", next=false),
            TableAddFilterItemView(this, "원자핵공학전공", next=false),
        )
        items["기계공학부"] = arrayListOf(
            TableAddFilterItemView(this, "기계공학부", next=false),
            TableAddFilterItemView(this, "멀티스케일기게설계전공", next=false),
        )
        items["식물생산과학부"] = arrayListOf(
            TableAddFilterItemView(this, "산업인력개발학", next=false),
            TableAddFilterItemView(this, "작물생명과학전공", next=false),
            TableAddFilterItemView(this, "원예생명공학전공", next=false),
        )
        items["산림과학부"] = arrayListOf(
            TableAddFilterItemView(this, "산림환경학전공", next=false),
            TableAddFilterItemView(this, "환경재료과학전공", next=false),
            TableAddFilterItemView(this, "연합전공 글로벌환경경영학", next=false),
        )
        items["농림생물자원학부"] = arrayListOf(
            TableAddFilterItemView(this, "작물생명과학전공", next=false),
            TableAddFilterItemView(this, "원에생명공학전공", next=false),
            TableAddFilterItemView(this, "산림환경학전공", next=false),
            TableAddFilterItemView(this, "환경재료학전공", next=false),
            TableAddFilterItemView(this, "바이오소재공학전공", next=false),
        )
        items["응용생물화학부"] = arrayListOf(
            TableAddFilterItemView(this, "응용생물화학부", next=false),
            TableAddFilterItemView(this, "응용생명화학전공", next=false),
            TableAddFilterItemView(this, "응용생물학전공", next=false),
        )
        items["식품∙동물생명공학부"] = arrayListOf(
            TableAddFilterItemView(this, "식품생명공학전공", next=false),
            TableAddFilterItemView(this, "동물생명공학전공", next=false)
        )
        items["바이오시스템∙소재학부"] = arrayListOf(
            TableAddFilterItemView(this, "바이오시스템공학전공", next=false),
            TableAddFilterItemView(this, "바이오소재공학전공", next=false)
        )
        items["바이오시스템∙공학과"] = arrayListOf(
            TableAddFilterItemView(this, "바이오시스템공학전공", next=false),
            TableAddFilterItemView(this, "융합전공 글로벌 스마트팜", next=false)
        )
        items["조경∙지역시스템공학부"] = arrayListOf(
            TableAddFilterItemView(this, "조경학전공", next=false),
            TableAddFilterItemView(this, "지역시스템공학전공", next=false)
        )
        items["농경제사회학부"] = arrayListOf(
            TableAddFilterItemView(this, "농업∙자원경제학전공", next=false),
            TableAddFilterItemView(this, "지역정보학전공", next=false),
            TableAddFilterItemView(this, "융합전공 지역∙공간분석학", next=false),
        )
        items["농생명공학부"] = arrayListOf(
            TableAddFilterItemView(this, "농생명공학부", next=false),
            TableAddFilterItemView(this, "바이오모듈레이션전공", next=false)
        )
        items["생태조경∙지역시스템공학부"] = arrayListOf(
            TableAddFilterItemView(this, "생태조경학전공", next=false),
            TableAddFilterItemView(this, "지역시스템공학전공", next=false)
        )
        items["협동과정 농림기상학"] = arrayListOf(TableAddFilterItemView(this, "농림기상학전공", next=false))
        items["조소과"] = arrayListOf(
            TableAddFilterItemView(this, "조소과", next=false),
            TableAddFilterItemView(this, "조소전공", next=false),
            TableAddFilterItemView(this, "연합전공 영상매체예술", next=false),
        )
        items["디자인학부"] = arrayListOf(
            TableAddFilterItemView(this, "공예전공", next=false),
            TableAddFilterItemView(this, "디자인전공", next=false),
        )
        items["미술학과"] = arrayListOf(
            TableAddFilterItemView(this, "미술학과", next=false),
            TableAddFilterItemView(this, "동양화전공", next=false),
            TableAddFilterItemView(this, "서양화∙판화전공", next=false),
            TableAddFilterItemView(this, "조소전공", next=false)
        )
        items["사회교육과"] = arrayListOf(
            TableAddFilterItemView(this, "사회교육과", next=false),
            TableAddFilterItemView(this, "일반사회전공", next=false),
            TableAddFilterItemView(this, "역사전공", next=false),
            TableAddFilterItemView(this, "지리전공", next=false),
            TableAddFilterItemView(this, "사회교육전공", next=false),
        )
        items["물리교육과"] = arrayListOf(TableAddFilterItemView(this, "물리교육전공", next=false))
        items["화학교육과"] = arrayListOf(TableAddFilterItemView(this, "화학교육전공", next=false))
        items["외국어교육과"] = arrayListOf(
            TableAddFilterItemView(this, "영어전공", next=false),
            TableAddFilterItemView(this, "불어전공", next=false),
            TableAddFilterItemView(this, "독어전공", next=false),
        )
        items["과학교육과"] = arrayListOf(
            TableAddFilterItemView(this, "과학교육전공", next=false),
            TableAddFilterItemView(this, "물리전공", next=false),
            TableAddFilterItemView(this, "화학전공", next=false),
            TableAddFilterItemView(this, "생물전공", next=false),
            TableAddFilterItemView(this, "지구과학전공", next=false),
        )
        items["소비자아동학부"] = arrayListOf(
            TableAddFilterItemView(this, "소비자학전공", next=false),
            TableAddFilterItemView(this, "아동가족학전공", next=false)
        )
        items["의류학과"] = arrayListOf(
            TableAddFilterItemView(this, "의류학과", next=false),
            TableAddFilterItemView(this, "의류학전공", next=false)
        )
        items["작곡과"] = arrayListOf(
            TableAddFilterItemView(this, "작곡과", next=false),
            TableAddFilterItemView(this, "작곡전공", next=false),
            TableAddFilterItemView(this, "이론전공", next=false),
        )
        items["기악과"] = arrayListOf(
            TableAddFilterItemView(this, "현악전공", next=false),
            TableAddFilterItemView(this, "관악전공", next=false),
            TableAddFilterItemView(this, "피아노전공", next=false),
        )
        items["음악과"] = arrayListOf(
            TableAddFilterItemView(this, "음악과", next=false),
            TableAddFilterItemView(this, "작곡전공", next=false),
            TableAddFilterItemView(this, "기악전공", next=false),
            TableAddFilterItemView(this, "작곡∙지휘∙음악학전공", next=false),
        )
        items["국악과"] = arrayListOf(
            TableAddFilterItemView(this, "국악과", next=false),
            TableAddFilterItemView(this, "국악전공", next=false)
        )
        items["보건학과"] = arrayListOf(
            TableAddFilterItemView(this, "보건학전공", next=false),
            TableAddFilterItemView(this, "보건정책관리학전공", next=false)
        )
        items["환경보건학과"] = arrayListOf(TableAddFilterItemView(this, "환경보건학전공", next=false))
        items["행정학과"] = arrayListOf(
            TableAddFilterItemView(this, "행정학과", next=false),
            TableAddFilterItemView(this, "글로벌행정전공", next=false)
        )
        items["환경계획학과"] = arrayListOf(
            TableAddFilterItemView(this, "환경계획학과", next=false),
            TableAddFilterItemView(this, "도시및지역계획학전공", next=false),
            TableAddFilterItemView(this, "환경관리학전공", next=false),
            TableAddFilterItemView(this, "교통학전공", next=false),
            TableAddFilterItemView(this, "도시사회혁신전공", next=false),
        )
        items["환경조경학과"] = arrayListOf(
            TableAddFilterItemView(this, "환경조경학과", next=false),
            TableAddFilterItemView(this, "환경조경학전공", next=false),
            TableAddFilterItemView(this, "도시환경설계전공", next=false)
        )
        items["도시및지역계획학과"] = arrayListOf(TableAddFilterItemView(this, "도시및지역계획학전공", next=false))
        items["국제학과"] = arrayListOf(
            TableAddFilterItemView(this, "국제학과", next=false),
            TableAddFilterItemView(this, "국제통상전공", next=false),
            TableAddFilterItemView(this, "국제협력전공", next=false),
            TableAddFilterItemView(this, "국제지역학전공", next=false),
            TableAddFilterItemView(this, "한국학전공", next=false),
        )
        items["의학과"] = arrayListOf(
            TableAddFilterItemView(this, "의학과", next=false),
            TableAddFilterItemView(this, "해부학전공", next=false),
            TableAddFilterItemView(this, "생리학전공", next=false),
            TableAddFilterItemView(this, "병리학전공", next=false),
            TableAddFilterItemView(this, "미생물학전공", next=false),
            TableAddFilterItemView(this, "예방의학전공", next=false),
            TableAddFilterItemView(this, "인문의학전공", next=false),
            TableAddFilterItemView(this, "법의학전공", next=false),
            TableAddFilterItemView(this, "의료관리학전공", next=false),
            TableAddFilterItemView(this, "의공학전공", next=false),
            TableAddFilterItemView(this, "내과학전공", next=false),
            TableAddFilterItemView(this, "외과학전공", next=false),
            TableAddFilterItemView(this, "산부인과학전공", next=false),
            TableAddFilterItemView(this, "소아과학전공", next=false),
            TableAddFilterItemView(this, "정신과학전공", next=false),
            TableAddFilterItemView(this, "피부과학전공", next=false),
            TableAddFilterItemView(this, "정형외과학전공", next=false),
            TableAddFilterItemView(this, "흉부외과학전공", next=false),
            TableAddFilterItemView(this, "비뇨의학전공", next=false),
            TableAddFilterItemView(this, "이비인후과학전공", next=false),
            TableAddFilterItemView(this, "영상의학전공", next=false),
            TableAddFilterItemView(this, "마취통증의학전공", next=false),
            TableAddFilterItemView(this, "성형외과학전공", next=false),
            TableAddFilterItemView(this, "방사선종양학전공", next=false),
            TableAddFilterItemView(this, "검사의학전공", next=false),
            TableAddFilterItemView(this, "재활의학전공", next=false),
            TableAddFilterItemView(this, "핵의학전공", next=false),
            TableAddFilterItemView(this, "가정의학전공", next=false),
            TableAddFilterItemView(this, "중개의학전공", next=false),
            TableAddFilterItemView(this, "응급의학전공", next=false),
            TableAddFilterItemView(this, "열대의학전공", next=false),
            TableAddFilterItemView(this, "융합전공 혁신의과학", next=false),
            TableAddFilterItemView(this, "안과학전공", next=false),
        )
        items["치의학과"] = arrayListOf(
            TableAddFilterItemView(this, "치의학과", next=false),
            TableAddFilterItemView(this, "치의학학사과정", next=false)
        )
        items["국제농업기술학과"] = arrayListOf(TableAddFilterItemView(this, "국제농업기술학과", next=false))
        items["응용공학과"] = arrayListOf(TableAddFilterItemView(this, "응용공학전공", next=false))
        items["데이터사이언스학과"] = arrayListOf(
            TableAddFilterItemView(this, "데이터사이언스학과", next=false),
            TableAddFilterItemView(this, "데이터사이언스학전공", next=false),
        )

        items["교양영역"] = arrayListOf(
            TableAddFilterItemView(this, "학문의 기초"),
            TableAddFilterItemView(this, "학문의 세계"),
            TableAddFilterItemView(this, "선택교양"),
            TableAddFilterItemView(this, "전공영역"),
        )
        items["학문의 기초"] = arrayListOf(
            TableAddFilterItemView(this, "사고와 표현", next=false),
            TableAddFilterItemView(this, "외국어", next=false),
            TableAddFilterItemView(this, "수량적 분석과 추론", next=false),
            TableAddFilterItemView(this, "과학적 사고와 실험", next=false),
            TableAddFilterItemView(this, "컴퓨터와 정보 활용", next=false),
        )
        items["학문의 세계"] = arrayListOf(
            TableAddFilterItemView(this, "언어와 문학", next=false),
            TableAddFilterItemView(this, "문화와 예출", next=false),
            TableAddFilterItemView(this, "역사와 철학", next=false),
            TableAddFilterItemView(this, "정치와 경제", next=false),
            TableAddFilterItemView(this, "인간과 사회", next=false),
            TableAddFilterItemView(this, "자연과 기술", next=false),
            TableAddFilterItemView(this, "생명과 환경", next=false),
        )
        items["선택교양"] = arrayListOf(
            TableAddFilterItemView(this, "체육", next=false),
            TableAddFilterItemView(this, "예술 실기", next=false),
            TableAddFilterItemView(this, "대학과 리더십", next=false),
            TableAddFilterItemView(this, "창의와 융합", next=false),
            TableAddFilterItemView(this, "한국의 이해", next=false),
        )
        items["전공영역"] = arrayListOf(
            TableAddFilterItemView(this, "교과교육", next=false)
        )


        // 각 아이템에 clickListener 적용
        items.values.forEach { array ->
            array.forEach { item ->
                item.clickLayout.setOnClickListener {
                    // 하위 항목이 없는 아이템이면 그걸 선택
                    if(item.next.visibility == View.GONE) {
                        // 선택한 걸로 바꾸고,
                        item.setView(item.isFavorite, true)
                        // 이전 액티비티로 돌아가기 (선택한 학과 정보 챙겨서)
                        val resultIntent = Intent()
                        resultIntent.putExtra("major", item.title.text.toString())

                        // 경로 정보 저장
                        val path : ArrayList<String> = arrayListOf()
                        for(idx in 0..folderLevel) path.add(views[idx].text.toString())
                        resultIntent.putStringArrayListExtra("path", path)

                        // 즐겨찾기 정보 저장
                        updateFavorite()

                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                    else {
                        binding.filterList.removeAllViews()
                        // 선택한 폴더에 따른 새로운 뷰 넣기
                        setFolder(level=folderLevel, mode= NORMAL)
                        folderLevel += 1
                        setFolder(item.title.text.toString(), level=folderLevel, mode= BOLD)
                    }
                }
                item.findViewById<CardView>(R.id.filter_star_card).setOnClickListener {
                    //  반대로 바꾼 후
                    item.setView(!item.isFavorite, item.checked.visibility==View.VISIBLE)
                    // 재정렬
                    when (folderLevel) {
                        0 -> setFolder(binding.folderLevel0Text.text.toString(), folderLevel, BOLD)
                        1 -> setFolder(binding.folderLevel1Text.text.toString(), folderLevel, BOLD)
                        2 -> setFolder(binding.folderLevel2Text.text.toString(), folderLevel, BOLD)
                        3 -> setFolder(binding.folderLevel3Text.text.toString(), folderLevel, BOLD)
                    }
                }
                item.favoriteStar.setOnClickListener {
                    //  반대로 바꾼 후
                    item.setView(!item.isFavorite, item.checked.visibility==View.VISIBLE)
                    // 재정렬
                    when (folderLevel) {
                        0 -> setFolder(binding.folderLevel0Text.text.toString(), folderLevel, BOLD)
                        1 -> setFolder(binding.folderLevel1Text.text.toString(), folderLevel, BOLD)
                        2 -> setFolder(binding.folderLevel2Text.text.toString(), folderLevel, BOLD)
                        3 -> setFolder(binding.folderLevel3Text.text.toString(), folderLevel, BOLD)
                    }
                }
            }
        }
        binding.folderLevel0Text.setOnClickListener {
            setFolder(binding.folderLevel0Text.text.toString(), level=0, mode=BOLD)
            setFolder(level=1, mode=DELETE)
            setFolder(level=2, mode=DELETE)
            setFolder(level=3, mode=DELETE)
        }
        binding.folderLevel1Text.setOnClickListener {
            setFolder(binding.folderLevel1Text.text.toString(), level=1, mode=BOLD)
            setFolder(level=2, mode=DELETE)
            setFolder(level=3, mode=DELETE)
        }
        binding.folderLevel2Text.setOnClickListener {
            setFolder(binding.folderLevel2Text.text.toString(), level=2, mode=BOLD)
            setFolder(level=3, mode=DELETE)
        }


        // 기존 선택됐던 학과 다시 적용
        intent.getStringArrayListExtra("path")!!.forEachIndexed { index, s ->
            setFolder(s, index, BOLD)
        }
        items.values.forEach { list ->
            list.forEach { item ->
                if(item.title.text.toString() == intent.getStringExtra("before")!!) {
                    item.setView(item.isFavorite, true)
                }
            }
        }

        // 기존 즐겨찾기 다시 적용
        try {
            sharedPreferences.getStringSet("favorite_major", null)!!.forEach { favor ->
                items.values.forEach { list ->
                    list.forEach { item ->
                        if(item.title.text.toString() == favor) item.setView(true, item.checked.visibility==View.VISIBLE) }
                }
            }
        } catch (n : NullPointerException) {}


        // X 버튼
       binding.tableFilterCloseButton.setOnClickListener {
           // 즐겨찾기 정보 저장
           updateFavorite()

           setResult(RESULT_CANCELED)
           finish()
           // 뒤로 버튼 누르면 아래로 내려가기
           overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
       }
    }

    private fun updateFavorite() {
        // 즐겨찾기 정보 저장
        val favorites = mutableSetOf<String>()
        items.values.forEach { list ->
            list.forEach { item ->
                if(item.isFavorite) favorites.add(item.title.text.toString()) }
        }
        sharedPreferences.edit {
            this.putStringSet("favorite_major", favorites)
        }
    }

    private fun setList(name : String) {
        binding.filterList.removeAllViews()
        try {
            // 즐겨찾기 한 거 먼저 넣고
            items[name]!!.forEach { item ->
                if(item.isFavorite) {
                    binding.filterList.addView(item)
                }
            }
            // 나머지 넣기
            items[name]!!.forEach { item ->
                if(!item.isFavorite) {
                    binding.filterList.addView(item)
                }
            }
        } catch (n : NullPointerException) {
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
        }
    }
    private fun setFolder(name : String="", level : Int, mode : Int) {
        val views = arrayListOf(binding.folderLevel0Text, binding.folderLevel1Text,
            binding.folderLevel2Text, binding.folderLevel3Text)
        val arrows = arrayListOf(binding.folderLevel1Arrow,
            binding.folderLevel2Arrow, binding.folderLevel3Arrow)
        when(mode) {
            BOLD -> {
                views[level].text = name
                views[level].visibility = View.VISIBLE
                if(level>0) {
                    arrows[level-1].visibility = View.VISIBLE
                    views[level-1].setTypeface(null, Typeface.NORMAL)
                }
                views[level].setTextColor(resources.getColor(R.color.OnBackground))
                views[level].setTypeface(null, Typeface.BOLD)
                setList(views[level].text.toString())
                folderLevel = level
            }
            NORMAL -> {
                if(level>0) arrows[level-1].visibility = View.VISIBLE
                views[level].visibility = View.VISIBLE
                views[level].setTypeface(null, Typeface.NORMAL)
                views[level].setTextColor(resources.getColor(R.color.color_filter_folder))
            }
            DELETE -> {
                if(level>0) arrows[level-1].visibility = View.GONE
                views[level].visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        when (folderLevel) {
            0 -> {
                // 즐겨찾기 정보 저장
                updateFavorite()
                setResult(RESULT_CANCELED)
                finish()

                // 뒤로 버튼 누르면 아래로 내려가기
                overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
            }
            1 -> {
                setFolder(level=1, mode= DELETE)
                setFolder(binding.folderLevel0Text.text.toString(), level=0, mode= BOLD)
            }
            2 -> {
                setFolder(level=2, mode= DELETE)
                setFolder(binding.folderLevel1Text.text.toString(), level=1, mode= BOLD)
            }
            3 -> {
                setFolder(level=3, mode= DELETE)
                setFolder(binding.folderLevel2Text.text.toString(), level=2, mode= BOLD)
            }
        }

    }
    private companion object {
        const val DELETE = 0
        const val NORMAL = 1
        const val BOLD = 2
    }
}