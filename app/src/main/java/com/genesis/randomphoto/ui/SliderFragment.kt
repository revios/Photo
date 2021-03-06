package com.genesis.randomphoto.ui


import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import com.genesis.randomphoto.R
import com.genesis.randomphoto.adapter.SliderAdapter
import com.genesis.randomphoto.dto.PhotoDTO
import com.genesis.randomphoto.framework.slide.ItemConfig
import com.genesis.randomphoto.framework.slide.ItemTouchHelperCallback
import com.genesis.randomphoto.framework.slide.OnSlideListener
import com.genesis.randomphoto.framework.slide.SlideLayoutManager
import com.genesis.randomphoto.network.SendRequest
import kotlinx.android.synthetic.main.fab_layout.*
import kotlinx.android.synthetic.main.fragment_slider.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SliderFragment : Fragment(), Callback<ArrayList<PhotoDTO>> {
    override fun onFailure(call: Call<ArrayList<PhotoDTO>>, t: Throwable) {
        Toast.makeText(context, "Resimleri Görebilmek İçin İnternet Bağlantısı Gerekir.", Toast.LENGTH_LONG).show()
        mPhotoList.add(PhotoDTO(9999))
        initView()
        initListener()
    }

    override fun onResponse(call: Call<ArrayList<PhotoDTO>>, response: Response<ArrayList<PhotoDTO>>) {
        mPhotoList = response.body()!!
        mPhotoList.shuffle()
        initView()
        initListener()
    }


    private lateinit var rootView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSlideLayoutManager: SlideLayoutManager
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var mItemTouchHelperCallback: ItemTouchHelperCallback<Int>
    private var mAdapter: SliderAdapter? = null
    private var mPhotoList = ArrayList<PhotoDTO>()

    //fab
    private var FAB_Status = false
    private lateinit var showFabEdit: Animation
    private lateinit var hideFabEdit: Animation
    private lateinit var showFabSave: Animation
    private lateinit var hideFabSave: Animation
    private lateinit var showFabShare: Animation
    private lateinit var hideFabShare: Animation
    private lateinit var rotateMainFab: Animation
    private lateinit var revertMainFab: Animation
    /* private lateinit var fabMain: FloatingActionButton
     private lateinit var fabEdit: FloatingActionButton
     private lateinit var fabSave: FloatingActionButton
     private lateinit var fabShare: FloatingActionButton*/
    private lateinit var rootLayout: CoordinatorLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_slider, container, false)
        addData()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Animations
        showFabEdit = AnimationUtils.loadAnimation(context, R.anim.show_fab_edit)
        showFabSave = AnimationUtils.loadAnimation(context, R.anim.show_fab_save)
        showFabShare = AnimationUtils.loadAnimation(context, R.anim.show_fab_share)
        hideFabEdit = AnimationUtils.loadAnimation(context, R.anim.hide_fab_edit)
        hideFabSave = AnimationUtils.loadAnimation(context, R.anim.hide_fab_save)
        hideFabShare = AnimationUtils.loadAnimation(context, R.anim.hide_fab_share)
        rotateMainFab = AnimationUtils.loadAnimation(context, R.anim.fab_main_rotate)
        revertMainFab = AnimationUtils.loadAnimation(context, R.anim.fab_main_revert)
        fab_main.setOnClickListener {
            FAB_Status = if (!FAB_Status) {
                expandFAB()
                true
            } else {
                hideFAB()
                false
            }
        }

        fab_share.setOnClickListener {
            Toast.makeText(context, "Share!!!", Toast.LENGTH_SHORT).show()
        }
        fab_edit.setOnClickListener {
            Toast.makeText(context, "Edit!!!", Toast.LENGTH_SHORT).show()
        }
        fab_save.setOnClickListener {
            Toast.makeText(context, "Save!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun expandFAB() {

        fab_main.startAnimation(rotateMainFab)
        //Floating Action Button 1
        val layoutParams: FrameLayout.LayoutParams = fab_edit.layoutParams as FrameLayout.LayoutParams
        layoutParams.rightMargin += (fab_edit.width * 1.7).toInt()
        layoutParams.bottomMargin += (fab_edit.height * 0.25).toInt()
        fab_edit.layoutParams = layoutParams
        fab_edit.startAnimation(showFabEdit)
        fab_edit.isClickable = true

        //Floating Action Button 2
        val layoutParams2: FrameLayout.LayoutParams = fab_save.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin += (fab_save.width * 1.5).toInt()
        layoutParams2.bottomMargin += (fab_save.height * 1.5).toInt()
        fab_save.layoutParams = layoutParams2
        fab_save.startAnimation(showFabSave)
        fab_save.isClickable = true

        //Floating Action Button 3
        val layoutParams3: FrameLayout.LayoutParams = fab_share.layoutParams as FrameLayout.LayoutParams
        layoutParams3.rightMargin += (fab_share.width * 0.25).toInt()
        layoutParams3.bottomMargin += (fab_share.height * 1.7).toInt()
        fab_share.layoutParams = layoutParams3
        fab_share.startAnimation(showFabShare)
        fab_share.isClickable = true
    }

    private fun hideFAB() {

        fab_main.startAnimation(revertMainFab)

        //Floating Action Button 1
        val layoutParams: FrameLayout.LayoutParams = fab_edit.layoutParams as FrameLayout.LayoutParams
        layoutParams.rightMargin -= (fab_edit.width * 1.7).toInt()
        layoutParams.bottomMargin -= (fab_edit.height * 0.25).toInt()
        fab_edit.layoutParams = layoutParams
        fab_edit.startAnimation(hideFabEdit)
        fab_edit.isClickable = false

        //Floating Action Button 2
        val layoutParams2: FrameLayout.LayoutParams = fab_save.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin -= (fab_save.width * 1.5).toInt()
        layoutParams2.bottomMargin -= (fab_save.height * 1.5).toInt()
        fab_save.layoutParams = layoutParams2
        fab_save.startAnimation(hideFabSave)
        fab_save.isClickable = false

        //Floating Action Button 3
        val layoutParams3: FrameLayout.LayoutParams = fab_share.layoutParams as FrameLayout.LayoutParams
        layoutParams3.rightMargin -= (fab_share.width * 0.25).toInt()
        layoutParams3.bottomMargin -= (fab_share.height * 1.7).toInt()
        fab_share.layoutParams = layoutParams3
        fab_share.startAnimation(hideFabShare)
        fab_share.isClickable = false
    }

    private fun initView() {
        Log.e("SliderFragment", "initView")
        mRecyclerView = rootView.findViewById(R.id.recycler_view)
        mAdapter = SliderAdapter(rootView.context, mPhotoList)
        mRecyclerView.adapter = mAdapter
        //addData()
        mItemTouchHelperCallback = ItemTouchHelperCallback<Int>(mRecyclerView.adapter!!, mPhotoList)
        mItemTouchHelper = ItemTouchHelper(mItemTouchHelperCallback)
        mSlideLayoutManager = SlideLayoutManager(mRecyclerView, mItemTouchHelper)
        mItemTouchHelper.attachToRecyclerView(mRecyclerView)
        mRecyclerView.layoutManager = mSlideLayoutManager
    }

    private fun initListener() {
        mItemTouchHelperCallback.setOnSlideListener(object : OnSlideListener<PhotoDTO> {
            override fun onSlided(viewHolder: RecyclerView.ViewHolder, t: PhotoDTO, direction: Int) {
                Log.e("SliderFragment", "onSlided")
                if (FAB_Status) {
                    hideFAB()
                    FAB_Status = false
                }
            }

            override fun onSliding(viewHolder: RecyclerView.ViewHolder, ratio: Float, direction: Int) {
                if (direction == ItemConfig.SLIDING_LEFT) {
                } else if (direction == ItemConfig.SLIDING_RIGHT) {
                }
            }

            override fun onClear() {
                addData()
            }
        })
    }

    fun addData() {
        SendRequest.getPhotos().enqueue(this@SliderFragment)
    }
}


