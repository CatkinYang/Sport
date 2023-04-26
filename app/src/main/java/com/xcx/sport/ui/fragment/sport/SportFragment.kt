package com.xcx.sport.ui.fragment.sport

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.xcx.sport.App
import com.xcx.sport.BR
import com.xcx.sport.R
import com.xcx.sport.data.model.SportCourse
import com.xcx.sport.data.model.SportType
import com.xcx.sport.ui.BaseFragment
import com.xcx.sport.ui.adapter.SportCourseAdapter
import com.xcx.sport.ui.adapter.SportTypeAdapter
import com.xcx.sport.viewmodels.StateHolder


class SportFragment : BaseFragment() {
    private val states by viewModels<SportStates>()
    private val sportTypeAdapter by lazy { SportTypeAdapter(states.sportTypeList) }
    private val sportCourseAdapter by lazy { SportCourseAdapter(states.sportCourseList.filter { it.type == 1 }) }

    override fun onInitData() {
        if (App.sUsername.isEmpty()) {
            nav().navigate(R.id.action_sportFragment_to_loginFragment)
        }
    }

    override fun onOutput() {
    }

    override fun onInput() {
        sportTypeAdapter.onClick(R.id.item_sport_type) {
            sportCourseAdapter.models =
                states.sportCourseList.filter { it.type == modelPosition + 1 }
        }
        sportCourseAdapter.onClick(R.id.item_sport_history) {
            val bundle = Bundle()
            bundle.putSerializable("data", getModel<SportCourse>())
            nav().navigate(R.id.action_sportFragment_to_sportCourseDetailFragment, bundle)
        }
    }


    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(
            R.layout.fragment_sport,
            BR.state,
            states
        ).addBindingParam(BR.sportTypeAdapter, sportTypeAdapter)
            .addBindingParam(BR.sportCourseAdapter, sportCourseAdapter)
    }

    class SportStates : StateHolder() {
        val sportTypeList = mutableListOf(
            SportType("Running", R.drawable.ic_run, 1),
            SportType("Yoga", R.drawable.ic_yoga, 2),
            SportType("Riding", R.drawable.ic_cycle, 3),
            SportType("Strength", R.drawable.ic_strength, 4)
        )
        val sportCourseList = mutableListOf(
            SportCourse(
                "21天学会正确跑姿，无伤跑步！",
                "https://mmbiz.qpic.cn/mmbiz_png/ic0Rs4Czsibich6Tpyz6xGcWVsD53ZS0yzUuhTibSRpH6DXIW4OK4Sd90jR4otXAmhCaKlE0Siav9d42zFWa8u0YOkg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/tF78l-J3R5WnKGCRbzKyzg",
                1
            ),
            SportCourse(
                "跑步小白必收藏！12周｜0基础跑10K教程！",
                "https://mmbiz.qpic.cn/mmbiz_jpg/iaP7KwYxToPtp8w28dQXjwB3JOUtGVgWmyYGvc1A19ffFnhwicfZWVNUibdiatUyfM63FXlqaAGcrg3qTvB8vygRfg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/RypwDcJrO0GevrlKtQQrbQ",
                1
            ),
            SportCourse(
                "瑜伽教程（全集-附46个瑜伽常见体式）",
                "https://mmbiz.qpic.cn/mmbiz_jpg/4L6b7JiamlPeK6R92NV9ib8NzGRvJv9dZibtInd5TNCMMZeH1gSLBpzIM7BAm7szZ78zUlf9sBYj5OibM6swZPebdw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/paqWwXeL_WukpsjiHY_6dw",
                2
            ),
            SportCourse(
                "《荷心.能量流瑜伽》初中高系列习练教程【合集】",
                "https://mmbiz.qpic.cn/mmbiz_jpg/vVB8xkxzzrw0YMlRgv8V1KHqDFSclgZMTgL9YG7ABbl7NvBNKF4E5XqptN1ibkfGGuq9E2zIMVV5HruibKzCbwfw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/Pg4TPvohyTiPUBadaaEklA",
                2
            ),
            SportCourse(
                "这个夏天，16 个瑜伽动作送你一双大长腿！（动图教程）",
                "https://mmbiz.qpic.cn/mmbiz_jpg/B6A1IXxfibsaJSbzXuUrHoBAhhpHzd6hj2zJ2QnUib7xkq2ITnWJePfGRb80EEc0icyppgPGtJJhiaUVUVVEJ5TVZg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/7hT9JL3i6rwQmH5XY3urOg",
                2
            ),
            SportCourse(
                "新手入门：正确的骑行姿势",
                "https://mmbiz.qpic.cn/mmbiz_jpg/iaWeRdjzP1bIg6ym2bdyUg2gBSlvwaEzt8UN2cm7biaRpzIrW57BatqOvtibcUef4KppibrHweSn88USp3gFmcV1nw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/_vS_d_xxyzdYAEinsFc1ow",
                3
            ),
            SportCourse(
                "科学健身周周练丨公路自行车怎么骑？教学来啦！",
                "https://mmbiz.qpic.cn/mmbiz_png/Ttadq9M9edGy0jqh1VvUF3zP4icZp7D35c7vsMohHklBOesAotxhpE7BAml7LDh9R7X0OPxoBELa6e5lex2m7Mg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/r4CVlEQEKRKjl40s5aw05A",
                3
            ),
            SportCourse(
                "科学健身｜50组居家徒手健身教程，不去健身房也能锻炼！",
                "https://mmbiz.qpic.cn/mmbiz/leehicOGxia3EG0dJRFt0kdoNAnXq0HacROb6ZNNCFOp0I1dckebeT35GWiaO53pNlAkF5Td8clz81CPZEzypDgVA/640.gif?wx_fmt=gif&wxfrom=5&wx_lazy=1",
                "https://mp.weixin.qq.com/s?__biz=MzIxMDMzMjIxMw==&mid=2247543402&idx=2&sn=85972f248bd4de4289ab3259637f1d79&chksm=97645530a013dc26513697c153860163cb6d08c7b2b4bab35f35c6830ab8bc13f3043e3f9444&scene=20",
                4
            ),
            SportCourse(
                "史上最全健身动图教程，告别瞎练，撸铁不求人！",
                "https://mmbiz.qpic.cn/mmbiz/3qicX9KqnEAJzUNYO3KqJJydOIhy2ljjJ7Bm3XobAFvibxAEQqrCykXnzJRGibJKQEn44Nib71ag75nlLAwEWJ5Q3w/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s?__biz=MzIzMzE5NjcyNg==&mid=2684218344&idx=1&sn=96238ec2816f9491222090edae0c67de&chksm=f2801631c5f79f27014d2eecfb5bef73303db01dff161cf41875cf3307bc0d2029b287128c45&scene=27",
                4
            ),
            SportCourse(
                "健身小知识丨每天坚持完成这9个动作帮助改善关节活动度",
                "https://mmbiz.qpic.cn/mmbiz_png/rZfg3b1um1B9d3KwKHNRLAMh37r1bUiamAJL3GKnn6iaBCrCMjtoCVFyXuic1Ku2zp2DfL8xNNIgyRMia2icrQpLiaAw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1",
                "https://mp.weixin.qq.com/s/_VM1UZpVpUNGZJRszujuZw",
                4
            ),
            SportCourse(
                "腰背肌锻炼动作大合集！",
                "https://mmbiz.qpic.cn/mmbiz_gif/VLFnq5vYMwUS1nBFicasATRDHIYHtgZRiaJH6MkibuVn5oqgibtlFKBbhWzOWibKHcqicDgrpvsgv8RNViatoQEEXZrxA/640?wx_fmt=gif&wxfrom=5&wx_lazy=1",
                "https://mp.weixin.qq.com/s/5X1MAhAEMu0Oa_uLZHhc2w",
                4
            ),
        )
    }
}