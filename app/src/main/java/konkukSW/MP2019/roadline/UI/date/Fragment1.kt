package konkukSW.MP2019.roadline.UI.date


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.DateItemTouchHelperCallback
import konkukSW.MP2019.roadline.Data.Adapter.DateListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R


/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment1 : Fragment(), DateListAdapter.ItemDragListener {  //리스트

    lateinit var planList:ArrayList<Plan>
    lateinit var adapter:DateListAdapter
    lateinit var rView:RecyclerView
    lateinit var v:View
    lateinit var itemTouchHelper:ItemTouchHelper
    lateinit var callback: DateItemTouchHelperCallback

    var ListID = "a"
    var DayNum = 0;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragment1, container, false)
        init()
        return v
    }

    fun init(){
        initData()
        initLayout()
        addListener()
        initSwipe()
    }

    fun initData(){
        if(activity != null){
            val intent = activity!!.intent
            if(intent != null){
                ListID = intent.getStringExtra("ListID")
                DayNum = intent.getIntExtra("DayNum", 0)
            }
        }
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        val results:RealmResults<T_Plan> = realm.where<T_Plan>(T_Plan::class.java)
            .equalTo("listID", ListID)
            .equalTo("dayNum", DayNum)
            .findAll()
            .sort("pos")
        planList = ArrayList<Plan>()
        for(T_Plan in results){
            planList.add(Plan(T_Plan.listID, T_Plan.dayNum, T_Plan.id, T_Plan.name,
                T_Plan.locationX, T_Plan.locationY, T_Plan.time, T_Plan.memo, T_Plan.pos, -1, false))
        }
        if(planList.size == 1)
            planList.get(0).viewType = -2
        else if(planList.size > 1){
            planList.get(0).viewType = -4
            planList.get(planList.size - 1).viewType = -3
        }
    }

    fun initLayout(){
        rView = v!!.findViewById(R.id.f1_rView) as RecyclerView
        val layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rView.layoutManager = layoutManager
        adapter = DateListAdapter(planList, this, context!!)
        rView.adapter = adapter

    }

    fun addListener(){
        adapter.itemClickListener = object : DateListAdapter.OnItemClickListener{
            //addBtn 클릭했을 때
            override fun OnItemClick(holder: DateListAdapter.FooterViewHolder) {
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("pos", planList.size)
                i.putExtra("DayNum", DayNum)
                i.putExtra("ListID", ListID)
                startActivityForResult(i,123)
            }

            //리사이클러뷰 아이템 클릭했을 때
            override fun OnItemClick(holder: DateListAdapter.ItemViewHolder, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("spot", data)
                i.putExtra("DayNum", DayNum)
                i.putExtra("ListID", ListID)
                i.putExtra("path", 1)
                i.putExtra("pos", position)
                startActivityForResult(i, 123)
            }
        }


    }

    override fun onStartDrag(holder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(holder)

    }

    override fun onStartSwipe(holder: RecyclerView.ViewHolder) {
        itemTouchHelper.startSwipe(holder)
    }

    fun initSwipe(){
        callback = DateItemTouchHelperCallback(adapter, activity!!, ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rView) //recyclerView에 붙이기
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 애드스팟 하고나서 돌아왔을때 어댑터뷰 바로 갱신
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                val ft = fragmentManager!!.beginTransaction()
                ft.detach(this).attach(this).commit()

            }
        }
    }

    fun refresh()
    {
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
    }

    fun getScreenshotFromRecyclerView(): Bitmap? {
        //view.rs_dragBtn.visibility = View.INVISIBLE
        var bigBitmap: Bitmap? = null

        if (adapter != null) {
            val size = adapter.itemCount - 1
            if(size == 0)
                return null
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache = LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(rView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(rView.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmaCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }

            bigBitmap = Bitmap.createBitmap(rView.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap!!)
            bigCanvas.drawColor(Color.WHITE)

            for (i in 0 until size) {
                val bitmap = bitmaCache.get(i.toString())
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.getHeight()
                bitmap.recycle()
            }

        }
        //view.rs_dragBtn.visibility = View.VISIBLE
        return bigBitmap
    }

}
