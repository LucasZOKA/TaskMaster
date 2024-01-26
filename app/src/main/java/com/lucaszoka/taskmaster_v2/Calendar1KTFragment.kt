package com.lucaszoka.taskmaster_v2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.lucaszoka.taskmaster_v2.databinding.CalendarDay1Binding
import com.lucaszoka.taskmaster_v2.databinding.CalendarHeader1Binding
import com.lucaszoka.taskmaster_v2.databinding.FragmentCalendar1KTBinding
import com.lucaszoka.taskmaster_v2.model.Task
import java.lang.String.format
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalQueries.localDate
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


var sPref: SharedPreferences? = null
var userEmail: String? = null
var taskList: List<Task>? = null
var tasksMap: MutableMap<String, String>? = null
var tasksDup: MutableMap<String, String>? = null

/**
 * A simple [Fragment] subclass.
 * Use the [Calendar1KTFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//nao ta entrando a programação, culpa do gui < mentira, culpa do butuca
class Calendar1KTFragment : Fragment() {

    private var selectedDate: LocalDate? = null
    private var param1: String? = null
    private var param2: String? = null


    lateinit var mAdapter: TaskAdapter
    private lateinit var binding: FragmentCalendar1KTBinding
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        database = FirebaseDatabase.getInstance().reference.child("tasks")

        sPref = requireContext().getSharedPreferences("SPX", Context.MODE_PRIVATE)
        userEmail = sPref!!.getString("email", null)
        taskList = ArrayList()
        tasksMap = mutableMapOf<String, String>()
        tasksDup = mutableMapOf<String, String>()

        if (sPref?.contains("MGName") == true) {
            val valueEventListener = database.orderByChild("groupID").equalTo(sPref!!.getString("MGID", null))
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            (taskList as ArrayList<Task>).clear()
                            for (itemSnapshot in snapshot.children) {
                                val task = itemSnapshot.getValue(
                                    Task::class.java
                                )
                                if (task != null) {
                                    (taskList as ArrayList<Task>).add(task)
                                    tasksMap!![task.dateEnd] = task.difficulty
                                    Log.d("TAREFAS", tasksMap!!.values.toString())
                                    mAdapter.notifyDataSetChanged()


                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
        } else {
            val valueEventListener = database.orderByChild("email").equalTo(userEmail)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        (taskList as ArrayList<Task>).clear()
                        for (itemSnapshot in snapshot.children) {
                            val task = itemSnapshot.getValue(
                                Task::class.java
                            )
                            if (task != null) {
                                (taskList as ArrayList<Task>).add(task)
                                if(tasksMap!!.containsKey(task.dateEnd)){
                                    //Log.d("TAREFAS", "Adicionou")
                                    tasksDup!![task.dateEnd] = task.id
                                } else {
                                    tasksMap!![task.dateEnd] = task.id
                                }
                                //Log.d("TAREFAS", "tarefas/id " + tasksMap!!.values.toString())
                                //Log.d("TAREFAS","tarefas duplicadas " + tasksDup!!.values.toString())
                                mAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar1_k_t, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("FODA", "Ta foda")
        super.onViewCreated(view, savedInstanceState)
        mAdapter = TaskAdapter(context, taskList)
        binding = FragmentCalendar1KTBinding.bind(view)

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(daysOfWeek)


        binding.exFiveCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.exFiveCalendar.scrollToMonth(currentMonth.minusMonths(2))
        //binding.exFiveCalendar.scrollToMonth(currentMonth)

        /*Log.d("adapter", "taskList: " + taskList)
        binding.exFiveRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)
        binding.exFiveRv.adapter = mAdapter*/


        /*val recyclerView = view.findViewById<RecyclerView>(R.id.exFiveRv)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)

        recyclerView.adapter = TaskAdapter(context, taskList)*/

        binding.exFiveCalendar.monthScrollListener = { month ->
            binding.exFiveMonthYearText.text = month.yearMonth.toString()

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.exFiveCalendar.notifyDateChanged(it)
            }
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        Log.d("adapter", "taskList: " + taskList)
        binding.exFiveRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)
        binding.exFiveRv.adapter = mAdapter
    }

    private fun updateAdapterForList(list: MutableList<Task>) {
        mAdapter.changeList(list)
        mAdapter.notifyDataSetChanged()



    /*flightsAdapter.flights.clear()
        flightsAdapter.flights.addAll(flights[date].orEmpty())
        flightsAdapter.notifyDataSetChanged()*/
    }

    private fun updateListForDate(date: LocalDate?): MutableList<Task> {
        val mTaskList = mutableListOf<Task>()

        Log.d("Calendario", "date: $date")
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate: String = date!!.format(formatter)

        Log.d("Calendario", "date formatada: $formattedDate")


        for (task in taskList!!){
            //Log.d("Calendario", "task dateEnd: ${task.dateEnd}")
            if (task.dateEnd.equals(formattedDate)){
                //Log.d("Calendario", "task igual: ${task.dateEnd}")
                mTaskList.add(task)
            }
        }

        return mTaskList
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDay1Binding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            val binding = this@Calendar1KTFragment.binding
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }
                            updateAdapterForList(updateListForDate(day.date))
                        }
                    }
                }
            }
        }
        val tasks = taskList
        binding.exFiveCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val context = container.binding.root.context
                val textView = container.binding.exFiveDayText
                val layout = container.binding.exFiveDayLayout
                val flightTopView = container.binding.exFiveDayFlightTop
                val flightBottomView = container.binding.exFiveDayFlightBottom
                textView.text = data.date.dayOfMonth.toString()
                //Log.d("Colores", "bind " + data.date.dayOfMonth.toString())


                flightTopView.background = null
                flightBottomView.background = null

                //Só entra no for quando clicka em algum dia por algum motivo
                //val tasks = taskList
                //Log.d("Colores", " 1 list: "+ tasks)

                if (tasks != null) {
                    //Log.d("Colores", " 2 list: "+ tasks)

                    for (item in tasks) {
                        Log.d("Colores", " 3 list: "+ tasks)
                        Log.d("Colores", "entrou no for")
                        Log.d("Colores", "item: " + item.title)
                        if (item.dateEnd == data.date.toString()){
                            val taskDate = LocalDate.parse(item.dateEnd)

                            if(tasksDup?.containsKey(item.dateEnd) == true){
                                if (tasksDup?.getValue(item.dateEnd).equals(item.id)){
                                    when(item.difficulty){
                                        "Nenhuma" -> flightBottomView.setBackgroundColor(context.getColor(R.color.subtext_color))
                                        "Baixa" -> flightBottomView.setBackgroundColor(context.getColor(R.color.taskDifficulty_easy))
                                        "Média" -> flightBottomView.setBackgroundColor(context.getColor(R.color.taskDifficulty_medium))
                                        "Alta" -> flightBottomView.setBackgroundColor(context.getColor(R.color.taskDifficulty_hard))
                                    }
                                }
                            }
                            when(item.difficulty){
                                "Nenhuma" -> flightTopView.setBackgroundColor(context.getColor(R.color.subtext_color))
                                "Baixa" -> flightTopView.setBackgroundColor(context.getColor(R.color.taskDifficulty_easy))
                                "Média" -> flightTopView.setBackgroundColor(context.getColor(R.color.taskDifficulty_medium))
                                "Alta" -> flightTopView.setBackgroundColor(context.getColor(R.color.taskDifficulty_hard))
                            }


                            /*when(item.difficulty){
                                "Nenhuma" -> flightTopView.setBackgroundColor(context.getColor(R.color.subtext_color))
                                "Baixa" -> flightTopView.setBackgroundColor(context.getColor(R.color.taskDifficulty_easy))
                                "Média" -> flightTopView.setBackgroundColor(context.getColor(R.color.taskDifficulty_medium))
                                "Alta" -> flightTopView.setBackgroundColor(context.getColor(R.color.taskDifficulty_hard))
                            }*/
                        }
                    }


                    mAdapter.notifyDataSetChanged()
                }

                if (data.position == DayPosition.MonthDate) {
                    //Log.d("DATARALHO", data.date.toString())
                    //textView.setTextColor(resources.getColor(R.color.example_5_text_grey))
                    layout.setBackgroundResource(if (selectedDate == data.date) R.drawable.example_5_selected_bg else 0)

                } else {
                    //textView.setTextColor(resources.getColor(R.color.example_5_text_grey_light))
                    layout.background = null
                }


            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeader1Binding.bind(view).legendLayout.root
        }

        val typeFace = Typeface.create("sans-serif-light", Typeface.NORMAL)
        binding.exFiveCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                //tv.text = daysOfWeek[index].toString()
                                val daysOfWeek = daysOfWeek[index]
                                tv.text = daysOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                //tv.setTextColor(resources.getColor(R.color.white))
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                                tv.typeface = typeFace
                            }
                    }
                }
            }
    }
}