package com.example.pr_cbs.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.InfiniteCarouselTransformer
import com.example.pr_cbs.R
import com.example.pr_cbs.Adapters.LatestBooksAdapter
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter.wrap
import kotlinx.android.synthetic.main.home_fragment.*
import com.example.pr_cbs.Adapters.RecommendedBooksAdapterCarousel
import com.example.pr_cbs.Adapters.ShortEventAdapter
import com.example.pr_cbs.MainActivity


class HomeFragment : Fragment(),
    DiscreteScrollView.OnItemChangedListener<RecommendedBooksAdapterCarousel.MyViewHolder> {


    private lateinit var mInfiniteScrollWrapper: InfiniteScrollAdapter<*>

    private lateinit var mLatestAdapterMain: RecommendedBooksAdapterCarousel
    private lateinit var mRecommendedAdapterMain: LatestBooksAdapter
    private lateinit var mShortEventAdapter: ShortEventAdapter


    override fun onCurrentItemChanged(p0: RecommendedBooksAdapterCarousel.MyViewHolder?, p1: Int) {
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

            //Наши рекомендации
            this.mLatestAdapterMain = RecommendedBooksAdapterCarousel(this@HomeFragment.context)

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

        if ((activity as MainActivity).getInfoAboutEventError() == 0) {


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

            //Последние поступления
            this.mRecommendedAdapterMain = LatestBooksAdapter(this@HomeFragment.context)
            recyclerView_latest_books.layoutManager =
                LinearLayoutManager(this@HomeFragment.context, RecyclerView.HORIZONTAL, false)
            recyclerView_latest_books.adapter =
                LatestBooksAdapter(this@HomeFragment.context)

        } else {
            home_recommended_error_block.visibility = VISIBLE
            recyclerView_latest_books.visibility = GONE
        }

//        home_more_events.setOnClickListener {
//            (activity as MainActivity).loadFragment(EventFragment())
//        }


    }

}





