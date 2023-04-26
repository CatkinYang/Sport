package com.xcx.sport.ui.bind

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.staggered
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.xcx.sport.App
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.data.network.BaseUrl

//静态方法提供给XML设置为View的方法
object BindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["initBar"], requireAll = false)
    fun initBar(bar: BarChart, map: Map<String, Double>) {
        val list = mutableListOf<BarEntry>()
        val dateList = mutableListOf<String>()
        map.keys.forEach {
            dateList.add(it)
        }
        map.values.forEachIndexed { index, i ->
            list.add(BarEntry(index.toFloat(), i.toFloat()))
        }
        val lineDataSet = BarDataSet(list, "Daily kilometers run")
        lineDataSet.valueTextSize = 12f
//        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        val lineData = BarData(lineDataSet)
        bar.description.isEnabled = false
        bar.axisRight.isEnabled = false
        bar.isScaleXEnabled = true
        bar.isScaleYEnabled = false
        bar.xAxis.granularity = 1f
        bar.xAxis.axisMaximum = list.size + 0.5f;
        bar.axisLeft.axisMinimum = 0f
//        line.xAxis.setLabelCount(list.size, false)
        bar.setScaleMinima(1.5f, 1f)
//        line.xAxis.labelRotationAngle = -30f
        //有多条数据则使用adddataset()方法 参数是你的DataSet
        bar.data = lineData
        bar.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value < dateList.size && value >= 0) {
                    Log.i("TAG", "getFormattedValue: $value,${value.toInt()}")
                    dateList[value.toInt()]
                } else {
                    ""
                }
            }
        }
        bar.invalidate()
    }

    @JvmStatic
    @BindingAdapter(value = ["historyTitle"], requireAll = false)
    fun historyTitle(textView: TextView, sportInfo: SportInfo) {
        if (sportInfo.title.isEmpty()) {
            textView.text =
                "Time:" + sportInfo.time + "\nSpeed:" + sportInfo.speed + "min/km" + " Distance:" + sportInfo.distance + "km"
        } else {
            textView.text = sportInfo.title
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["visible"], requireAll = false)
    fun visible(view: View, visible: Boolean) {
        if (visible && view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        } else if (!visible && view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["invisible"], requireAll = false)
    fun invisible(view: View, visible: Boolean) {
        if (visible && view.visibility == View.INVISIBLE) {
            view.visibility = View.VISIBLE
        } else if (!visible && view.visibility == View.VISIBLE) {
            view.visibility = View.INVISIBLE
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["imgRes"], requireAll = false)
    fun setImageResource(imageView: ImageView, imgRes: Int) {
        imageView.setImageResource(imgRes)
    }

    @JvmStatic
    @BindingAdapter(value = ["imgRemote"], requireAll = true)
    fun setRemoteImage(imageView: ImageView, data: Any?) {
        if (data == null) return
        if (data is String && !data.contains("http")) imageView.load("$BaseUrl/$data")
        else imageView.load(data)
    }

    @JvmStatic
    @BindingAdapter(value = ["addItemDecoration"], requireAll = false)
    fun addItemDecoration(recyclerView: RecyclerView, int: Int) {
        //添加Android自带的分割线
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                if (int == 1) DividerItemDecoration.VERTICAL else DividerItemDecoration.HORIZONTAL
            )
        )
    }

    /**
     * 1是垂直，0是平行
     * */
    @JvmStatic
    @BindingAdapter(value = ["linearLayoutManager"], requireAll = true)
    fun linearLayoutManager(
        recyclerView: RecyclerView,
        orientation: Int,
    ) {
        recyclerView.linear(orientation)
    }

    @JvmStatic
    @BindingAdapter(value = ["gridLayoutManager"], requireAll = true)
    fun gridLayoutManager(recyclerView: RecyclerView, int: Int) {
        recyclerView.grid(int)
    }

    @JvmStatic
    @BindingAdapter(value = ["staggeredLayoutManager"], requireAll = true)
    fun staggeredLayoutManager(recyclerView: RecyclerView, int: Int) {
        recyclerView.staggered(int)
    }

    @JvmStatic
    @BindingAdapter(value = ["refreshListener"], requireAll = false)
    fun refreshListener(smartRefreshLayout: SmartRefreshLayout, l: OnRefreshListener) {
        Log.i("TAG", "refreshStatus: ")
//        if (state.get() == false) smartRefreshLayout.finishRefresh()
//        val l = OnRefreshListener { TODO("Not yet implemented") }
        smartRefreshLayout.setOnRefreshListener(l)
//        smartRefreshLayout.setOnRefreshListener {
//            Log.i("TAG", "refreshStatus2 ")
//            if (it.isRefreshing) state.set(true)
//        }
    }

    @JvmStatic
    @BindingAdapter(value = ["finishRefresh"], requireAll = false)
    fun finishRefresh(smartRefreshLayout: SmartRefreshLayout, boolean: Boolean) {
        Log.i("TAG", "finishRefresh: ")
        if (!boolean) smartRefreshLayout.finishRefresh()
    }

    @JvmStatic
    @BindingAdapter(value = ["textColor"], requireAll = false)
    fun setTextColor(textView: TextView, textColorRes: Int) {
        textView.setTextColor(textView.context.getColor(textColorRes))
    }

    @JvmStatic
    @BindingAdapter(value = ["selected"], requireAll = false)
    fun selected(view: View, select: Boolean) {
        view.isSelected = select
    }

    @JvmStatic
    @BindingAdapter(value = ["clipToOutline"], requireAll = false)
    fun clipToOutline(view: View, clipToOutline: Boolean) {
        view.clipToOutline = clipToOutline
    }

    @JvmStatic
    @BindingAdapter(value = ["requestFocus"], requireAll = false)
    fun requestFocus(view: View, requestFocus: Boolean) {
        if (requestFocus) view.requestFocus()
    }

    @JvmStatic
    @BindingAdapter(value = ["showKeyboard"], requireAll = false)
    fun showKeyboard(view: View, showKeyboard: Boolean) {
        val imm =
            App.sApplication?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}