package com.example.pr_cbs.Fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.cardview.widget.CardView

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.InfiniteCarouselTransformer
import com.example.pr_cbs.R
import com.example.pr_cbs.Adapters.RecommendedBooksAdapter
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter.wrap
import kotlinx.android.synthetic.main.home_fragment.*
import com.example.pr_cbs.Adapters.CarouselLatestAdapter
import com.example.pr_cbs.Adapters.ShortEventAdapter
import com.example.pr_cbs.MainActivity


class HomeFragment : Fragment(),
    DiscreteScrollView.OnItemChangedListener<CarouselLatestAdapter.MyViewHolder> {


    private lateinit var mInfiniteScrollWrapper: InfiniteScrollAdapter<*>

    private lateinit var mLatestAdapterMain: CarouselLatestAdapter
    private lateinit var mRecommendedAdapterMain: RecommendedBooksAdapter
    private lateinit var mShortEventAdapter: ShortEventAdapter


    override fun onCurrentItemChanged(p0: CarouselLatestAdapter.MyViewHolder?, p1: Int) {
        val realPosition = mInfiniteScrollWrapper.realCurrentPosition
        Log.v("HomeTag", ("onCurrentItemChanged $realPosition"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.home_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ((activity as MainActivity).getInfoAboutLatestError()) {

            //Последние поступления
            this.mLatestAdapterMain = CarouselLatestAdapter(this@HomeFragment.context)

            // Бесконечное прокручивание
            this.mInfiniteScrollWrapper = wrap(this.mLatestAdapterMain)
            infinite_carousel.adapter = mInfiniteScrollWrapper

            // Трансформирование элемента
            this.infinite_carousel.setItemTransformer(InfiniteCarouselTransformer())

            this.infinite_carousel.addOnItemChangedListener(this)

        } else {

            home_latest_error_block.visibility = VISIBLE
            infinite_carousel.visibility = GONE

        }

        if ((activity as MainActivity).getInfoAboutEventError()) {


            //Ближайшие мероприятия
            this.mShortEventAdapter = ShortEventAdapter(this@HomeFragment.context)

            recyclerViewShortEvents.layoutManager =
                LinearLayoutManager(this@HomeFragment.context, RecyclerView.VERTICAL, false)

            recyclerViewShortEvents.adapter = this.mShortEventAdapter


        } else {
            home_event_error_block.visibility = VISIBLE
            recyclerViewShortEvents.visibility = GONE

        }


        if ((activity as MainActivity).getInfoAboutRecommendedError()) {

            //Рекомендуемые книги
            this.mRecommendedAdapterMain = RecommendedBooksAdapter(this@HomeFragment.context)
            recyclerViewRecommendedBooks.layoutManager =
                LinearLayoutManager(this@HomeFragment.context, RecyclerView.HORIZONTAL, false)
            recyclerViewRecommendedBooks.adapter =
                RecommendedBooksAdapter(this@HomeFragment.context)

        } else {
            home_recommended_error_block.visibility = VISIBLE
            recyclerViewRecommendedBooks.visibility = GONE
        }


    }

}





