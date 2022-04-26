package com.pratham.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pratham.happyplaces.R
import com.pratham.happyplaces.adapters.HappyPlacesAdapter
import com.pratham.happyplaces.database.DatabaseHandler
import com.pratham.happyplaces.models.HappyPlaceModel
import com.pratham.happyplaces.utils.SwipeToDeleteCallback
import com.pratham.happyplaces.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabAddHappyPlace.setOnClickListener {
            val intent= Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent,ADD_PLACE_ACTIVITY_REQUEST_CODE)

        }
        getHappyPlacesListFromLocalDatabase()
    }
    private fun setupHappyPlacesRecyclerView(happyPlaceList:ArrayList<HappyPlaceModel>){
        rv_happy_places_list.layoutManager=LinearLayoutManager(this)
        rv_happy_places_list.setHasFixedSize(true)
        val placesAdapter=HappyPlacesAdapter(this,happyPlaceList)
        rv_happy_places_list.adapter=placesAdapter
        placesAdapter.setOnClickListener(object:HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent=Intent(this@MainActivity,
                        HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })
       val editSwipeHandler=object:SwipeToEditCallback(this)
       {
           override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
               val adapter=rv_happy_places_list.adapter as HappyPlacesAdapter
               adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
           }

       }
        val editItemTouchHelper=ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_places_list)
        val deleteSwipeHandler=object: SwipeToDeleteCallback(this)
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getHappyPlacesListFromLocalDatabase()
            }

        }
        val deleteItemTouchHelper=ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_places_list)
    }

private fun getHappyPlacesListFromLocalDatabase(){
    val dbHandler=DatabaseHandler(this)
    val getHappyPlaceList:ArrayList<HappyPlaceModel> =dbHandler.getHappyPlacesList()
    if(getHappyPlaceList.size!=0){
        for(i in getHappyPlaceList){
            rv_happy_places_list.visibility= View.VISIBLE
            tv_no_records_available.visibility=View.GONE
            setupHappyPlacesRecyclerView(getHappyPlaceList)
        }
    }else{
        rv_happy_places_list.visibility= View.GONE
        tv_no_records_available.visibility=View.VISIBLE
    }
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode==Activity.RESULT_OK){
                getHappyPlacesListFromLocalDatabase()
            }else{
                Log.e("Activity","Cancelled or back pressed")
            }
        }
    }
    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE=1
        var EXTRA_PLACE_DETAILS="extra place details"
    }
}