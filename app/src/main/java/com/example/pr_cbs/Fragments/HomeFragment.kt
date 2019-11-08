package com.example.pr_cbs.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.InfiniteCarouselTransformer
import com.example.pr_cbs.R
import com.example.pr_cbs.Adapters.RecommendedBooksAdapter
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter.wrap
import kotlinx.android.synthetic.main.home_fragment.*
import com.example.pr_cbs.Adapters.CarouselLatestAdapter
import com.example.pr_cbs.Models.LatestModel
import com.example.pr_cbs.Models.RecommendedBook


class HomeFragment: Fragment(), DiscreteScrollView.OnItemChangedListener<CarouselLatestAdapter.MyViewHolder> {

    override fun onCurrentItemChanged(p0: CarouselLatestAdapter.MyViewHolder?, p1: Int) {
        val realPosition = mInfiniteScrollWrapper.realCurrentPosition
        log("onCurrentItemChanged $realPosition")
    }

    private lateinit var mInfiniteScrollWrapper: InfiniteScrollAdapter<*>
    private lateinit var LatestAdapterMain: CarouselLatestAdapter
    private var arrayList: ArrayList<LatestModel> = arrayListOf()


    private lateinit var RecommendedAdapterMain: RecommendedBooksAdapter
    private var recommendedList: ArrayList<RecommendedBook> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


       return inflater.inflate(R.layout.home_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Рекомендуемые книги
        this.RecommendedAdapterMain = RecommendedBooksAdapter(this@HomeFragment.context, recommendedList)
        recyclerViewRecommendedBooks.layoutManager = LinearLayoutManager(this@HomeFragment.context, RecyclerView.HORIZONTAL, false)
        recyclerViewRecommendedBooks.adapter = RecommendedBooksAdapter(this@HomeFragment.context, recommendedList)
        this.loadRecommendedBooks()


        //Последние поступления
        this.LatestAdapterMain = CarouselLatestAdapter(this@HomeFragment.context, arrayList)

        // Бесконечное прокручивание
        this.mInfiniteScrollWrapper = wrap(this.LatestAdapterMain)
        infinite_carousel.adapter = mInfiniteScrollWrapper

        // Трансформирование элемента
        this.infinite_carousel.setItemTransformer(InfiniteCarouselTransformer())

        // Item change listener
        this.infinite_carousel.addOnItemChangedListener(this)

        // data
        this.loadData()


    }


    private fun loadRecommendedBooks() {

        recommendedList.add( RecommendedBook("Вий", R.drawable.for_reccom_2))
        recommendedList.add(RecommendedBook("Война и мир", R.drawable.for_reccom_1))
        recommendedList.add( RecommendedBook("Пришельцы атакуют!", R.drawable.for_reccom_1))
        recommendedList.add(RecommendedBook("Книга с оооочееень длинным названием!", R.drawable.for_reccom_2))

        RecommendedAdapterMain.notifyDataSetChanged()

    }

    private fun loadData() {

        arrayList.add(LatestModel("Война и мир"))
        arrayList.add(LatestModel("Kotlin в действии"))
        arrayList.add(LatestModel("Трое в лодке не считая собаки"))
        arrayList.add(LatestModel("Название этой прекрасной книги очень длиннннннное"))

        LatestAdapterMain.notifyDataSetChanged()
    }

    private fun log(message: String) {
        Log.d("BackgroundToForeground", message)
    }





}



